package com.adminpro.tools.domains.entity.session;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.tools.domains.enums.SessionStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户Session 服务层实现
 *
 * @author simon
 * @date 2020-06-17
 */
@Service
public class SessionService extends BaseService<SessionEntity, String> {

    private SessionDao dao;

    @Autowired
    public SessionService(SessionDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static SessionService getInstance() {
        return SpringUtil.getBean(SessionService.class);
    }

    public QueryResultSet<SessionEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<SessionEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    @Transactional
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }
        String[] split = ids.split(",");
        for (int i = 0; i < split.length; i++) {
            dao.delete(split[i]);
        }
    }

    public List<SessionEntity> findByUser(String userDomain, String userId) {
        return dao.findByUserDomainAndUserIdAndStatus(userDomain, userId, SessionStatus.ACTIVE.getCode());
    }

    @Cacheable(value = RbacCacheConstants.SESSION_CACHE, key = "'session_' + #sessionId")
    public SessionEntity findBySessionId(String sessionId) {
        return dao.findBySessionId(sessionId);
    }

    @Override
    @CacheEvict(value = RbacCacheConstants.SESSION_CACHE, key = "'session_' + #entity.sessionId")
    public void update(SessionEntity entity) {
        super.update(entity);
    }

    @Override
    @CacheEvict(value = RbacCacheConstants.SESSION_CACHE, key = "'session_' + #entity.sessionId")
    public void delete(String s) {
        super.delete(s);
    }

    public List<SessionEntity> findActiveSessions() {
        return dao.findByStatus(SessionStatus.ACTIVE.getCode());
    }

    @Transactional
    @CacheEvict(value = RbacCacheConstants.SESSION_CACHE, allEntries = true)
    public void invalid(List<SessionEntity> list) {
        for (int i = 0; i < list.size(); i++) {
            SessionEntity sessionEntity = list.get(i);
            sessionEntity.setStatus(SessionStatus.EXPIRE.getCode());
            dao.update(sessionEntity);
        }
    }

    @Transactional
    @CacheEvict(value = RbacCacheConstants.SESSION_CACHE, key = "'session_' + #sessionId")
    public void invalid(String sessionId) {
        SessionEntity entity = findBySessionId(sessionId);
        if (entity != null) {
            entity.setStatus(SessionStatus.EXPIRE.getCode());
            dao.update(entity);
        }
    }

    @CacheEvict(value = RbacCacheConstants.SESSION_CACHE, allEntries = true)
    public void deleteAll() {
        dao.deleteAll();
    }
}
