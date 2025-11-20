package com.adminpro.tools.wx;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.Watermark;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.enums.Sex;
import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.base.util.UUIDUtil;
import com.adminpro.core.exceptions.APIException;
import com.adminpro.framework.cache.AppCache;
import com.adminpro.framework.security.auth.TokenHelper;
import com.adminpro.rbac.api.LoginHelper;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.rbac.common.RbacConstants;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.entity.user.UserIden;
import com.adminpro.rbac.domains.entity.user.UserService;
import com.adminpro.rbac.domains.entity.usertoken.UserTokenEntity;
import com.adminpro.rbac.domains.vo.login.LoginResponse;
import com.adminpro.rbac.enums.UserLoginPlatform;
import com.adminpro.rbac.enums.UserStatus;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 微信小程序用户接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/wechat/user")
public class WxMaUserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private WxMaService wxService;

    /**
     * 登陆接口
     */
    @GetMapping("/login")
    public R login(@RequestParam("code") String code) throws APIException {
        if (StringUtils.isBlank(code)) {
            return R.error("empty jscode");
        }

        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
            logger.info("session key: " + session.getSessionKey());
            logger.info("Open Id: " + session.getOpenid());
            logger.info("Union Id: " + session.getUnionid());
            UserEntity user = UserService.getInstance().findByExtUserId(session.getOpenid());


            if (user == null) {
                user = new UserEntity();
                user.setLoginName(session.getOpenid());
                user.setExtUserId(session.getOpenid());
                user.setUserIden(new UserIden(RbacConstants.INTERNET_DOMAIN, IdGenerator.getInstance().nextStringId()));
                user.setStatus(UserStatus.ACTIVE.getCode());
                user.setPassword(UUIDUtil.getUUID());
                UserService.getInstance().create(user);
            }

            UserTokenEntity token = TokenHelper.getInstance().generateToken(user.getUserIden());

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setId(user.getUserIden().getUserId());
            loginResponse.setUserId(user.getLoginName());
            loginResponse.setToken(token.getToken());
            loginResponse.setAuthed(user.isAuthenticated());
            loginResponse.setIdNo(user.getIdNo());
            loginResponse.setRealName(user.getRealName());
            loginResponse.setPlatform(UserLoginPlatform.getPlatForm(user.getUserIden().getUserDomain()));
            loginResponse.setDisplay(user.getDisplay());
            loginResponse.setMobileNo(user.getMobileNo());
            loginResponse.setDate(DateUtil.formatDate(new Date()));
            loginResponse.setAvatarUrl(user.getAvatarUrl());
            loginResponse.setExtUserId(user.getExtUserId());

            AppCache.getInstance().set(RbacCacheConstants.WX_SESSION_KEY_CACHE, token.getToken(), session, TokenHelper.EXPIRE);
            return R.ok(loginResponse);
        } catch (WxErrorException e) {
            this.logger.error(e.getMessage(), e);
            WxMaConfigHolder.remove();//清理ThreadLocal
            return R.error(e.toString());
        }
    }

    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @GetMapping("/info")
    public R info(@RequestParam String signature, @RequestParam String rawData, @RequestParam String encryptedData, @RequestParam String iv) {
        // 用户信息校验
        String authToken = LoginHelper.getInstance().getAuthToken();
        if (StringUtils.isEmpty(authToken)) {
            WxMaConfigHolder.remove();//清理ThreadLocal
            return R.error("user check failed");
        }

        WxMaJscode2SessionResult sessionResult = AppCache.getInstance().get(RbacCacheConstants.WX_SESSION_KEY_CACHE, authToken, WxMaJscode2SessionResult.class);
        String sessionKey = sessionResult.getSessionKey();
        String openid = sessionResult.getOpenid();

        if (!this.wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return R.error("user check failed");
        }

        // 解密用户信息
        WxMaUserInfo userInfo = this.wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        logger.debug("获取用户信息：" + new Gson().toJson(userInfo));
        WxMaConfigHolder.remove();//清理ThreadLocal

        String avatarUrl = userInfo.getAvatarUrl();
        String nickName = userInfo.getNickName();
        String gender = userInfo.getGender();
        String city = userInfo.getCity();
        String country = userInfo.getCountry();
        String province = userInfo.getProvince();
        Watermark watermark = userInfo.getWatermark();
        String language = userInfo.getLanguage();
        String unionId = userInfo.getUnionId();
        UserEntity user = UserService.getInstance().findByUserDomainAndLoginName(RbacConstants.INTERNET_DOMAIN, openid);
        if (user != null) {
            user.setAvatarUrl(avatarUrl);
            user.setDisplay(nickName);
            if (StringUtils.equals(gender, "男")) {
                user.setSex(Sex.MALE.getCode());
            } else {
                user.setSex(Sex.FEMALE.getCode());
            }
            user.setProvince(province);
            user.setCity(city);
            UserService.getInstance().update(user);
        }

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(user.getUserIden().getUserId());
        loginResponse.setUserId(user.getLoginName());
        loginResponse.setToken(authToken);
        loginResponse.setAuthed(user.isAuthenticated());
        loginResponse.setIdNo(user.getIdNo());
        loginResponse.setRealName(user.getRealName());
        loginResponse.setPlatform(UserLoginPlatform.getPlatForm(user.getUserIden().getUserDomain()));
        loginResponse.setDisplay(user.getDisplay());
        loginResponse.setMobileNo(user.getMobileNo());
        loginResponse.setDate(DateUtil.formatDate(new Date()));
        loginResponse.setAvatarUrl(user.getAvatarUrl());
        loginResponse.setExtUserId(user.getExtUserId());
        return R.ok(loginResponse);
    }

    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
    @GetMapping("/phone")
    public R phone(@RequestParam String code) {
        // 用户信息校验
        String authToken = LoginHelper.getInstance().getAuthToken();
        if (StringUtils.isEmpty(authToken)) {
            WxMaConfigHolder.remove();//清理ThreadLocal
            return R.error("user check failed");
        }

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = null;
        try {
            phoneNoInfo = this.wxService.getUserService().getPhoneNoInfo(code);
        } catch (WxErrorException e) {
            logger.error("", e);
        }

        WxMaJscode2SessionResult sessionResult = AppCache.getInstance().get(RbacCacheConstants.WX_SESSION_KEY_CACHE, authToken, WxMaJscode2SessionResult.class);
        String openid = sessionResult.getOpenid();

        UserEntity user = UserService.getInstance().findByUserDomainAndLoginName(RbacConstants.INTERNET_DOMAIN, openid);
        if (user != null) {
            String phoneNumber = phoneNoInfo.getPurePhoneNumber();
            user.setMobileNo(phoneNumber);
            UserService.getInstance().update(user);
        }

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(user.getUserIden().getUserId());
        loginResponse.setUserId(user.getLoginName());
        loginResponse.setToken(authToken);
        loginResponse.setAuthed(user.isAuthenticated());
        loginResponse.setIdNo(user.getIdNo());
        loginResponse.setRealName(user.getRealName());
        loginResponse.setPlatform(UserLoginPlatform.getPlatForm(user.getUserIden().getUserDomain()));
        loginResponse.setDisplay(user.getDisplay());
        loginResponse.setMobileNo(user.getMobileNo());
        loginResponse.setDate(DateUtil.formatDate(new Date()));
        loginResponse.setAvatarUrl(user.getAvatarUrl());
        loginResponse.setExtUserId(user.getExtUserId());
        return R.ok(loginResponse);
    }
}
