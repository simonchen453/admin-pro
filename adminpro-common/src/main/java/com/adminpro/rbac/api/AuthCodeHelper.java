package com.adminpro.rbac.api;

import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.framework.cache.AppCache;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.rbac.common.RbacConstants;
import com.adminpro.rbac.domains.entity.authcode.AuthCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuthCodeHelper {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取验证码过期时间（毫秒）
     * 直接调用 RbacConstants.getAuthCodeExpirePeriod()，利用 ConfigHelper 的缓存机制
     */
    private static int getExpire() {
        return 1000 * 60 * RbacConstants.getAuthCodeExpirePeriod();
    }

    public static AuthCodeHelper getInstance() {
        return SpringUtil.getBean(AuthCodeHelper.class);
    }

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

    public AuthCodeEntity getRegisterCode(String mobileNo) {
        AuthCodeEntity authCodeEntity = AppCache.getInstance().get(RbacCacheConstants.REGISTER_CODE_CACHE, getRegisterKey(mobileNo), AuthCodeEntity.class);
        if (authCodeEntity != null && !authCodeEntity.isExpired()) {
            return authCodeEntity;
        } else {
            return null;
        }
    }

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
