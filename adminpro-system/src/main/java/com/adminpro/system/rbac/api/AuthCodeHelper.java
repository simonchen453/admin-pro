package com.adminpro.system.rbac.api;

import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.entity.authcode.AuthCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 验证码辅助类
 * <p>
 * 本类提供手机验证码的管理功能，包括：
 * <ul>
 * <li>注册验证码的生成、查询和删除</li>
 * <li>重置密码验证码的生成、查询和删除</li>
 * <li>通用短信验证码的生成、查询和删除</li>
 * </ul>
 * <p>
 * 验证码存储在缓存中，具有过期时间。验证码为6位数字，
 * 默认过期时间由系统配置决定
 * <p>
 * 使用场景：
 * <ul>
 * <li>用户注册时发送手机验证码</li>
 * <li>用户忘记密码时发送重置验证码</li>
 * <li>其他需要短信验证的场景</li>
 * </ul>
 * <p>
 * 注意：验证码生成后会缓存，过期后自动失效
 *
 * @author simon
 */
@Component
public class AuthCodeHelper {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取验证码过期时间（毫秒）
     * <p>
     * 直接调用 RbacConstants.getAuthCodeExpirePeriod()，利用 ConfigHelper 的缓存机制
     *
     * @return 验证码过期时间（毫秒）
     */
    private static int getExpire() {
        return 1000 * 60 * RbacConstants.getAuthCodeExpirePeriod();
    }

    /**
     * 获取AuthCodeHelper的单例实例
     * <p>
     * 通过Spring容器获取Bean实例
     *
     * @return AuthCodeHelper实例
     */
    public static AuthCodeHelper getInstance() {
        return SpringUtil.getBean(AuthCodeHelper.class);
    }

    /**
     * 生成注册验证码
     * <p>
     * 为指定手机号生成6位数字验证码，并存储到缓存中。
     * 如果该手机号已存在未过期的验证码，则返回已有的验证码（避免频繁发送）。
     * 验证码过期时间由系统配置决定
     * <p>
     * 使用场景：
     * <ul>
     * <li>用户注册时发送手机验证码</li>
     * <li>手机号绑定验证</li>
     * </ul>
     *
     * @param mobileNo 手机号码，不能为空
     * @return 生成的或已存在的验证码实体，包含验证码、过期时间等信息
     */
    public AuthCodeEntity generateRegisterCode(String mobileNo) {
        AuthCodeEntity entity = AppCache.getInstance().get(RbacCacheConstants.REGISTER_CODE_CACHE, getRegisterKey(mobileNo), AuthCodeEntity.class);
        if (entity == null || entity.isExpired()) {
            entity = new AuthCodeEntity();
            String code = StringHelper.getRandStr(6);
            entity.setCode(code);
            Date now = new Date();
            Date expireTime = new Date(now.getTime() + getExpire());
            entity.setExpireTime(expireTime);
            entity.setMobileNo(mobileNo);
            logger.debug("send register code, " + "mobileNo(" + mobileNo + "), code(" + code + ");");
            AppCache.getInstance().set(RbacCacheConstants.REGISTER_CODE_CACHE, getRegisterKey(mobileNo), entity);
        }
        return entity;
    }

    /**
     * 获取注册验证码
     * <p>
     * 从缓存中查询指定手机号的注册验证码。
     * 如果验证码不存在或已过期，则返回null
     * <p>
     * 使用场景：
     * <ul>
     * <li>用户提交注册表单时验证验证码</li>
     * <li>验证码校验逻辑中获取缓存验证码</li>
     * </ul>
     *
     * @param mobileNo 手机号码，不能为空
     * @return 验证码实体，如果不存在或已过期则返回null
     */
    public AuthCodeEntity getRegisterCode(String mobileNo) {
        AuthCodeEntity authCodeEntity = AppCache.getInstance().get(RbacCacheConstants.REGISTER_CODE_CACHE, getRegisterKey(mobileNo), AuthCodeEntity.class);
        if (authCodeEntity != null && !authCodeEntity.isExpired()) {
            return authCodeEntity;
        } else {
            return null;
        }
    }

    /**
     * 删除注册验证码
     * <p>
     * 从缓存中删除指定手机号的注册验证码
     * <p>
     * 使用场景：
     * <ul>
     * <li>验证码验证成功后删除（一次性使用）</li>
     * <li>用户取消注册后清理</li>
     * </ul>
     *
     * @param mobileNo 手机号码，不能为空
     */
    public void removeRegisterCode(String mobileNo) {
        AppCache.getInstance().delete(RbacCacheConstants.REGISTER_CODE_CACHE, getRegisterKey(mobileNo));
    }

    public AuthCodeEntity generateResetPwdCode(String mobileNo) {
        AuthCodeEntity entity = AppCache.getInstance().get(RbacCacheConstants.REGISTER_CODE_CACHE, getResetPwdKey(mobileNo), AuthCodeEntity.class);
        if (entity == null || entity.isExpired()) {
            entity = new AuthCodeEntity();
            String code = StringHelper.getRandStr(6);
            entity.setCode(code);
            Date now = new Date();
            Date expireTime = new Date(now.getTime() + getExpire());
            entity.setExpireTime(expireTime);
            entity.setMobileNo(mobileNo);
            logger.debug("send resetpwd code, " + "mobileNo(" + mobileNo + "), code(" + code + ");");
            AppCache.getInstance().set(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, getResetPwdKey(mobileNo), entity);
        }

        return entity;
    }

    public AuthCodeEntity getResetPwdCode(String mobileNo) {
        AuthCodeEntity authCodeEntity = AppCache.getInstance().get(RbacCacheConstants.REGISTER_CODE_CACHE, getResetPwdKey(mobileNo), AuthCodeEntity.class);
        if (authCodeEntity != null && !authCodeEntity.isExpired()) {
            return authCodeEntity;
        } else {
            return null;
        }
    }

    public void removeResetPwdCode(String mobileNo) {
        AppCache.getInstance().delete(RbacCacheConstants.REGISTER_CODE_CACHE, getResetPwdKey(mobileNo));
    }

    public AuthCodeEntity generateMessageCode(String platform, String type, String mobileNo) {
        AuthCodeEntity entity = AppCache.getInstance().get(RbacCacheConstants.REGISTER_CODE_CACHE, getCodeKey(platform, type, mobileNo), AuthCodeEntity.class);
        if (entity == null || entity.isExpired()) {
            entity = new AuthCodeEntity();
            String code = StringHelper.getRandStr(6);
            entity.setCode(code);
            Date now = new Date();
            Date expireTime = new Date(now.getTime() + getExpire());
            entity.setExpireTime(expireTime);
            entity.setMobileNo(mobileNo);
            entity.setType(type);
            entity.setPlatform(platform);
            logger.debug("send resetpwd code, " + "type(" + type + "),mobileNo(" + mobileNo + "), code(" + code + ");");
            AppCache.getInstance().set(RbacCacheConstants.REGISTER_CODE_CACHE, getCodeKey(platform, type, mobileNo), entity);
        }

        return entity;
    }

    public AuthCodeEntity getMessageCode(String platform, String type, String mobileNo) {
        AuthCodeEntity authCodeEntity = AppCache.getInstance().get(RbacCacheConstants.REGISTER_CODE_CACHE, getCodeKey(platform, type, mobileNo), AuthCodeEntity.class);
        if (authCodeEntity != null && !authCodeEntity.isExpired()) {
            return authCodeEntity;
        } else {
            return null;
        }
    }

    public void removeMessageCode(String platform, String type, String mobileNo) {
        AppCache.getInstance().delete(RbacCacheConstants.REGISTER_CODE_CACHE, getCodeKey(platform, type, mobileNo));
    }

    public String getCodeKey(String platform, String type, String mobileNo) {
        return platform + ":" + type + ":" + mobileNo;
    }

    public String getRegisterKey(String mobileNo) {
        return mobileNo;
    }

    private String getResetPwdKey(String mobileNo) {
        return mobileNo;
    }
}
