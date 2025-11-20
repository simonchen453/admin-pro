package com.adminpro.rbac.domains.entity.userpost;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.rbac.domains.entity.user.UserIden;
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

    public List<UserPostAssignEntity> findByUserIden(UserIden userIden) {
        return dao.findByUserIden(userIden);
    }

    public void deleteByUserIden(UserIden userIden) {
        dao.deleteByUserIden(userIden);
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
