package com.adminpro.system.rbac.domains.entity.usertoken;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.security.auth.TokenHelper;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.domains.entity.user.UserIden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户Token 服务层实现
 *
 * @author simon
 * @date 2018-09-03
 */
@Service
public class UserTokenService extends BaseService<UserTokenEntity, String> {

    private UserTokenDao dao;

    @Autowired
    protected UserTokenService(UserTokenDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static UserTokenService getInstance() {
        return SpringUtil.getBean(UserTokenService.class);
    }

    public QueryResultSet<UserTokenEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public void create(UserTokenEntity entity) {
        super.create(entity);
        Integer expireSeconds = calculateCacheExpire(entity);
        AppCache.getInstance().set(RbacCacheConstants.AUTH_TOKEN_CACHE, entity.getToken(), entity, expireSeconds);
    }

    public void update(UserTokenEntity entity) {
        super.update(entity);
        Integer expireSeconds = calculateCacheExpire(entity);
        AppCache.getInstance().set(RbacCacheConstants.AUTH_TOKEN_CACHE, entity.getToken(), entity, expireSeconds);
    }
    
    /**
     * 根据token的过期时间计算缓存过期时间（秒）
     * 如果token有过期时间，则使用token过期时间；否则使用默认过期时间
     */
    private Integer calculateCacheExpire(UserTokenEntity entity) {
        if (entity.getExpireTime() != null) {
            long expireTime = entity.getExpireTime().getTime();
            long now = System.currentTimeMillis();
            long seconds = (expireTime - now) / 1000;
            if (seconds > 0) {
                return (int) seconds;
            }
        }
        return TokenHelper.getInstance().getExpireSeconds(entity.getDevice());
    }

    public void inactive(UserTokenEntity entity) {
        entity.setUpdateTime(DateUtil.now());
        entity.setStatus(UserTokenEntity.STATUS_INACTIVITY);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_TOKEN_CACHE, entity.getToken());
        super.update(entity);
    }

    @Transactional
    public void inactive(UserIden userIden) {
        List<UserTokenEntity> list = dao.findByUserDomainAndUserIdAndStatus(userIden.getUserDomain(), userIden.getUserId(), UserTokenEntity.STATUS_ACTIVITY);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                UserTokenEntity userTokenEntity = list.get(i);
                inactive(userTokenEntity);
                AppCache.getInstance().delete(RbacCacheConstants.AUTH_TOKEN_CACHE, userTokenEntity.getToken());
            }
        }
    }

    public UserTokenEntity findByToken(String token) {
        UserTokenEntity tokenEntity = AppCache.getInstance().get(RbacCacheConstants.AUTH_TOKEN_CACHE, token, UserTokenEntity.class);
        if (tokenEntity != null) {
            return tokenEntity;
        } else {
            UserTokenEntity byToken = dao.findByToken(token);
            if (byToken != null && byToken.isValid()) {
                AppCache.getInstance().set(RbacCacheConstants.AUTH_TOKEN_CACHE, token, byToken);
                return byToken;
            } else {
                return null;
            }
        }
    }
}
