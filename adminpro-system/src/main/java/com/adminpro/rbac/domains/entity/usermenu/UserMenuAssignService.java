package com.adminpro.rbac.domains.entity.usermenu;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户菜单分配 服务层实现
 *
 * @author simon
 * @date 2020-05-21
 */
@Service
public class UserMenuAssignService extends BaseService<UserMenuAssignEntity, String> {

    private UserMenuAssignDao dao;

    @Autowired
    public UserMenuAssignService(UserMenuAssignDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static UserMenuAssignService getInstance() {
        return SpringUtil.getBean(UserMenuAssignService.class);
    }

    public QueryResultSet<UserMenuAssignEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<UserMenuAssignEntity> findByParam(SearchParam param) {
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
}
