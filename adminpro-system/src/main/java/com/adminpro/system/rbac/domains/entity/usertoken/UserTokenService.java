package com.adminpro.system.rbac.domains.entity.usertoken;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.security.auth.TokenHelper;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户Token服务类
 * <p>
 * 提供用户登录令牌（Token）管理的核心业务功能，包括：
 * <ul>
 * <li>Token创建：创建新的登录令牌</li>
 * <li>Token更新：更新令牌信息</li>
 * <li>Token查询：根据Token字符串查询令牌信息</li>
 * <li>Token失效：停用指定的令牌</li>
 * <li>批量失效：停用用户的所有活动令牌</li>
 * <li>缓存管理：自动维护Token缓存</li>
 * </ul>
 * </p>
 * <p>
 * Token管理策略：
 * <ul>
 * <li>创建和更新Token时自动写入缓存</li>
 * <li>缓存过期时间根据Token过期时间动态计算</li>
 * <li>失效Token时同步清除缓存</li>
 * <li>支持多设备登录（PC、移动端等）</li>
 * </ul>
 * </p>
 *
 * @author simon
 * @date 2018-09-03
 * @version 1.0
 * @see UserTokenEntity
 * @see UserTokenDao
 */
@Service
public class UserTokenService extends BaseService<UserTokenEntity, String> {

    private UserTokenDao dao;

    @Autowired
    protected UserTokenService(UserTokenDao dao) {
        super(dao);
        this.dao = dao;
    }

    /**
     * 获取UserTokenService实例
     * <p>
     * 通过Spring容器获取Service实例，用于在非Spring管理的类中调用服务
     * </p>
     *
     * @return UserTokenService实例
     */
    public static UserTokenService getInstance() {
        return SpringUtil.getBean(UserTokenService.class);
    }

    /**
     * 搜索Token（分页）
     * <p>
     * 根据搜索参数进行分页查询，支持多种条件过滤
     * </p>
     *
     * @param param 搜索参数对象，包含分页信息和过滤条件
     * @return 分页查询结果集
     */
    public QueryResultSet<UserTokenEntity> search(SearchParam param) {
        return dao.search(param);
    }

    /**
     * 创建Token
     * <p>
     * 创建新的用户登录令牌，并自动写入缓存。
     * 缓存过期时间根据Token的过期时间动态计算。
     * </p>
     *
     * @param entity Token实体对象
     */
    public void create(UserTokenEntity entity) {
        super.create(entity);
        Integer expireSeconds = calculateCacheExpire(entity);
        AppCache.getInstance().set(RbacCacheConstants.AUTH_TOKEN_CACHE, entity.getToken(), entity, expireSeconds);
    }

    /**
     * 更新Token
     * <p>
     * 更新Token信息，并同步更新缓存。
     * 缓存过期时间根据Token的过期时间动态计算。
     * </p>
     *
     * @param entity Token实体对象
     */
    public void update(UserTokenEntity entity) {
        super.update(entity);
        Integer expireSeconds = calculateCacheExpire(entity);
        AppCache.getInstance().set(RbacCacheConstants.AUTH_TOKEN_CACHE, entity.getToken(), entity, expireSeconds);
    }

    /**
     * 计算Token缓存过期时间
     * <p>
     * 根据Token的过期时间计算缓存过期时间（秒）。
     * 如果Token有过期时间，则使用Token过期时间；否则使用设备类型对应的默认过期时间。
     * </p>
     *
     * @param entity Token实体对象
     * @return 缓存过期时间（秒）
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

    /**
     * 停用Token
     * <p>
     * 将指定Token状态设置为失效，并同步清除缓存。
     * 常用于用户退出登录场景。
     * </p>
     *
     * @param entity Token实体对象
     */
    public void inactive(UserTokenEntity entity) {
        entity.setUpdateTime(DateUtil.now());
        entity.setStatus(UserTokenEntity.STATUS_INACTIVITY);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_TOKEN_CACHE, entity.getToken());
        super.update(entity);
    }

    /**
     * 通过用户ID停用所有活动Token
     * <p>
     * 停用指定用户的所有活动状态的Token，并清除缓存。
     * 常用于强制用户下线或修改密码后使旧Token失效。
     * </p>
     *
     * @param userId 用户ID（全局唯一主键）
     */
    @Transactional
    public void inactiveByUserId(String userId) {
        if (userId == null) {
            return;
        }
        List<UserTokenEntity> list = dao.findByUserIdAndStatus(userId, UserTokenEntity.STATUS_ACTIVITY);
        if (list != null) {
            for (UserTokenEntity userTokenEntity : list) {
                inactive(userTokenEntity);
                AppCache.getInstance().delete(RbacCacheConstants.AUTH_TOKEN_CACHE, userTokenEntity.getToken());
            }
        }
    }

    /**
     * 根据Token字符串查询Token信息
     * <p>
     * 先从缓存中查询，缓存不存在则从数据库查询。
     * 从数据库查询后会将结果写入缓存。
     * 仅返回有效的Token（未过期且状态为活动）。
     * </p>
     *
     * @param token Token字符串
     * @return Token实体对象，不存在或已失效返回null
     */
    public UserTokenEntity findByToken(String token) {
        UserTokenEntity tokenEntity = AppCache.getInstance().get(RbacCacheConstants.AUTH_TOKEN_CACHE, token,
                UserTokenEntity.class);
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
