package com.adminpro.system.rbac.domains.entity.userpost;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户角色分配 服务层实现
 *
 * @author simon
 * @date 2020-06-14
 */
@Service
public class UserPostAssignService extends BaseService<UserPostAssignEntity, String> {

    private UserPostAssignDao dao;

    @Autowired
    public UserPostAssignService(UserPostAssignDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static UserPostAssignService getInstance() {
        return SpringUtil.getBean(UserPostAssignService.class);
    }

    public QueryResultSet<UserPostAssignEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<UserPostAssignEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    public List<UserPostAssignEntity> findByUserId(String userId) {
        return dao.findByUserId(userId);
    }

    public void deleteByUserId(String userId) {
        dao.deleteByUserId(userId);
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
}
