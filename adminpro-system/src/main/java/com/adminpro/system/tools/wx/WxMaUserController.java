package com.adminpro.system.tools.wx;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.base.util.UUIDUtil;
import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.rbac.api.Device;
import com.adminpro.system.rbac.api.LoginHelper;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.domains.entity.user.UserService;
import com.adminpro.system.rbac.domains.vo.jwt.JwtLoginResponse;
import com.adminpro.system.rbac.domains.vo.login.LoginResponse;
import com.adminpro.system.rbac.enums.UserStatus;
import com.adminpro.system.tools.wx.config.WxMaProperties;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 微信小程序用户接口。
 * <p>
 * 提供小程序登录、资料维护和手机号绑定，使用 JWT 进行认证。
 * </p>
 *
 * <h3>身份标识</h3>
 * <ul>
 * <li>openid：按小程序隔离，存入 {@link UserEntity#getExtUserId()}。</li>
 * <li>unionid：同一开放平台账号下跨应用唯一，存入 {@link UserEntity#getUnionId()}，
 * 是后续打通公众号与 App 的唯一依据。</li>
 * </ul>
 *
 * <h3>日志约定</h3>
 * <p>
 * session_key 可解密微信返回的加密数据，openid 与 unionid 属于用户标识，
 * 三者一律不得写入日志。排查问题请使用内部用户 ID。
 * </p>
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/wechat/user")
public class WxMaUserController {

    private static final Logger logger = LoggerFactory.getLogger(WxMaUserController.class);

    /** 小程序按移动端设备签发令牌。 */
    private static final Device MINI_PROGRAM_DEVICE = new Device() {
        @Override
        public boolean isNormal() {
            return false;
        }

        @Override
        public boolean isMobile() {
            return true;
        }

        @Override
        public boolean isTablet() {
            return false;
        }
    };

    private final WxMaService wxService;

    private final WxMaProperties wxMaProperties;

    /**
     * 小程序登录：用 wx.login 返回的 code 换取会话并签发 JWT。
     *
     * @param code  wx.login 返回的一次性 jsCode
     * @param appid 多小程序接入时指定使用哪个配置，单小程序可不传
     */
    @GetMapping("/login")
    public R login(@RequestParam("code") String code,
            @RequestParam(value = "appid", required = false) String appid) {
        if (StringUtils.isBlank(code)) {
            return R.error("empty jscode");
        }
        if (StringUtils.isNotBlank(appid) && !wxService.switchover(appid)) {
            return R.error("未配置的小程序 appid");
        }
        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
            String openid = session.getOpenid();
            String unionId = StringUtils.trimToNull(session.getUnionid());
            if (StringUtils.isBlank(openid)) {
                logger.warn("微信 jscode2session 未返回 openid");
                return R.error("微信登录失败");
            }

            String userDomain = wxMaProperties.resolveUserDomain(appid);
            UserEntity user = findBoundUser(userDomain, openid, unionId);
            if (user == null) {
                UserEntity conflict = UserService.getInstance().findByExtUserId(openid);
                if (conflict != null) {
                    // sys_user_tbl 的 unq_ext_userid 是全局唯一索引，直接建号会撞唯一约束，
                    // 这里给出明确结论而不是抛 500。
                    logger.warn("微信 openid 已注册在其它用户域: expected={}, actual={}, userId={}",
                            userDomain, conflict.getUserDomain(), conflict.getId());
                    return R.error("该微信账号已在其它用户域注册");
                }
                user = createUser(userDomain, openid, unionId);
            } else {
                syncWechatBinding(user, openid, unionId);
            }

            LoginUser loginUser = LoginUser.convertFrom(user);
            JwtLoginResponse jwtResponse = LoginHelper.getInstance()
                    .login(loginUser, MINI_PROGRAM_DEVICE, true);

            // 缓存 session_key，供后续解密微信加密数据使用；仅存缓存，不落库、不打日志。
            AppCache.getInstance().set(RbacCacheConstants.WX_SESSION_KEY_CACHE,
                    jwtResponse.getAccessToken(), session, (int) jwtResponse.getExpiresIn());

            return R.ok(toLoginResponse(user, jwtResponse.getAccessToken()));
        } catch (WxErrorException e) {
            logger.error("微信小程序登录失败", e);
            return R.error("微信登录失败");
        } finally {
            WxMaConfigHolder.remove();
        }
    }

    /**
     * 返回当前登录用户资料。
     * <p>
     * 微信自 2021-04-13 起对 wx.getUserInfo / getUserProfile 的加密资料只返回匿名昵称
     * 和灰色默认头像，signature、rawData、encryptedData、iv 已无解密价值，仅为兼容旧
     * 调用方保留且不再使用。需要真实昵称头像请改用 {@link #profile(String, String)}。
     * </p>
     */
    @GetMapping("/info")
    public R info(@RequestParam(required = false) String signature,
            @RequestParam(required = false) String rawData,
            @RequestParam(required = false) String encryptedData,
            @RequestParam(required = false) String iv) {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return R.authFailed("Unauthorized");
        }
        // 不能直接返回 LoginUser：其 getUser() 携带 UserEntity 的密码散列、
        // 支付密码和身份证号，会被整体序列化给客户端。
        return R.ok(toLoginResponse(loginUser.getUser(), null));
    }

    /**
     * 保存小程序端采集的昵称与头像。
     * <p>
     * 对应 open-type="chooseAvatar" 按钮与昵称输入框的新版取值方式，
     * 替代已失效的加密资料解密链路。
     * </p>
     *
     * @param nickName  用户填写的昵称，为空表示不修改
     * @param avatarUrl 头像地址，为空表示不修改
     */
    @PostMapping("/profile")
    public R profile(@RequestParam(required = false) String nickName,
            @RequestParam(required = false) String avatarUrl) {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return R.authFailed("Unauthorized");
        }
        UserEntity user = loginUser.getUser();

        boolean changed = false;
        if (StringUtils.isNotBlank(nickName)) {
            user.setDisplay(StringUtils.abbreviate(nickName.trim(), 255));
            changed = true;
        }
        if (StringUtils.isNotBlank(avatarUrl)) {
            user.setAvatarUrl(StringUtils.abbreviate(avatarUrl.trim(), 255));
            changed = true;
        }
        if (changed) {
            UserService.getInstance().update(user);
        }
        return R.ok(toLoginResponse(user, null));
    }

    /**
     * 绑定微信手机号。
     * <p>
     * 使用 open-type="getPhoneNumber" 回调返回的 code 直接换取手机号
     * （wxa/business/getuserphonenumber），不依赖 session_key，
     * 也不需要客户端传加密数据。
     * </p>
     *
     * @param code  getPhoneNumber 回调返回的动态令牌，五分钟内有效且只能消费一次
     * @param appid 多小程序接入时指定使用哪个配置，单小程序可不传
     */
    @GetMapping("/phone")
    public R phone(@RequestParam String code,
            @RequestParam(value = "appid", required = false) String appid) {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return R.authFailed("Unauthorized");
        }
        UserEntity user = loginUser.getUser();
        if (StringUtils.isBlank(code)) {
            return R.error("empty phone code");
        }
        if (StringUtils.isNotBlank(appid) && !wxService.switchover(appid)) {
            return R.error("未配置的小程序 appid");
        }

        try {
            WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getNewPhoneNoInfo(code);
            String phoneNumber = phoneNoInfo == null ? null : phoneNoInfo.getPurePhoneNumber();
            if (StringUtils.isBlank(phoneNumber)) {
                logger.warn("微信未返回手机号: userId={}", user.getId());
                return R.error("获取手机号失败");
            }
            user.setMobileNo(phoneNumber);
            UserService.getInstance().update(user);
            return R.ok(toLoginResponse(user, null));
        } catch (WxErrorException e) {
            logger.error("获取微信手机号失败: userId={}", user.getId(), e);
            return R.error("获取手机号失败");
        } finally {
            WxMaConfigHolder.remove();
        }
    }

    /**
     * 按 openid 定位账号；openid 未命中且携带 unionid 时用 unionid 兜底，
     * 覆盖同一自然人换小程序或重新授权后 openid 变化的情况。
     */
    private UserEntity findBoundUser(String userDomain, String openid, String unionId) {
        UserEntity user = UserService.getInstance().findByDomainAndExtUserId(userDomain, openid);
        if (user != null) {
            return user;
        }
        if (StringUtils.isNotBlank(unionId)) {
            return UserService.getInstance().findByDomainAndUnionId(userDomain, unionId);
        }
        return null;
    }

    private UserEntity createUser(String userDomain, String openid, String unionId) {
        UserEntity user = new UserEntity();
        user.setId(IdGenerator.getInstance().nextStringId());
        user.setUserDomain(userDomain);
        user.setLoginName(openid);
        user.setExtUserId(openid);
        user.setUnionId(unionId);
        user.setStatus(UserStatus.ACTIVE.getCode());
        user.setPassword(UUIDUtil.getUUID()); // 随机密码，小程序端不使用密码登录
        UserService.getInstance().create(user);
        logger.info("微信小程序创建账号: userDomain={}, userId={}", userDomain, user.getId());
        return user;
    }

    /** 补齐历史账号缺失的 openid / unionid 绑定；已有绑定不覆盖。 */
    private void syncWechatBinding(UserEntity user, String openid, String unionId) {
        boolean changed = false;
        if (StringUtils.isBlank(user.getExtUserId())) {
            user.setExtUserId(openid);
            changed = true;
        }
        if (StringUtils.isNotBlank(unionId)) {
            if (StringUtils.isBlank(user.getUnionId())) {
                user.setUnionId(unionId);
                changed = true;
            } else if (!StringUtils.equals(unionId, user.getUnionId())) {
                logger.warn("微信 UnionID 与既有绑定不一致，保留原绑定: userId={}", user.getId());
            }
        }
        if (changed) {
            UserService.getInstance().update(user);
        }
    }

    /** 统一对外响应，避免直接序列化 UserEntity 泄露密码散列与身份证号。 */
    private LoginResponse toLoginResponse(UserEntity user, String accessToken) {
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUserId(user.getLoginName());
        if (StringUtils.isNotBlank(accessToken)) {
            response.setToken(accessToken);
        }
        response.setAuthed(user.isAuthenticated());
        response.setIdNo(user.getIdNo());
        response.setRealName(user.getRealName());
        response.setDomain(user.getUserDomain());
        response.setDisplay(user.getDisplay());
        response.setMobileNo(user.getMobileNo());
        response.setDate(DateUtil.formatDate(new Date()));
        response.setAvatarUrl(user.getAvatarUrl());
        response.setExtUserId(user.getExtUserId());
        return response;
    }
}
