package com.adminpro.rbac.domains.entity.role;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.framework.common.helper.StringHelper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色表 数据库持久层
 *
 * @author simon
 * @date 2018-09-06
 */
@Component
public class RoleDao extends BaseDao<RoleEntity, String> {

    public QueryResultSet<RoleEntity> search(SearchParam param) {
        SelectBuilder<RoleEntity> select = new SelectBuilder<>(RoleEntity.class);
        select.setSearchParam(param);
        prepareSelectBuilder(select, param);
        return search(select);
    }

    /**
     * 根据查询参数获取所有的记录
     *
     * @param param
     * @return
     */
    public List<RoleEntity> findByParam(SearchParam param) {
        SelectBuilder<RoleEntity> select = new SelectBuilder<>(RoleEntity.class);
        select.setTable(RoleEntity.TABLE_NAME);
        prepareSelectBuilder(select, param);
        return execute(select);
    }

    /**
     * 准备查询条件
     *
     * @param param
     * @return
     */
    private void prepareSelectBuilder(SelectBuilder select, SearchParam param) {
        Map<String, Object> filters = param.getFilters();
        String display = (String) filters.get("display");
        String name = (String) filters.get("name");
        String status = (String) filters.get("status");
        Boolean system = (Boolean) filters.get("system");
        if (StringHelper.isNotEmpty(display)) {
            select.addWhereAnd(RoleEntity.COL_DISPLAY + " like ?", "%" + display + "%");
        }
        if (StringHelper.isNotEmpty(name)) {
            select.addWhereAnd(RoleEntity.COL_NAME + " like ?", "%" + name + "%");
        }
        if (StringHelper.isNotEmpty(status)) {
            select.addWhereAnd(RoleEntity.COL_STATUS + " = ?", status);
        }
        if (system != null) {
            select.addWhereAnd(RoleEntity.COL_IS_SYSTEM + " = ?", system);
        }
        select.addOrderByAscending(RoleEntity.COL_NAME);
    }

    public RoleEntity findByName(String name) {
        SelectBuilder<RoleEntity> select = new SelectBuilder<>(RoleEntity.class);
        select.addWhereAnd(RoleEntity.COL_NAME + " = ? ", name);
        return executeSingle(select);
    }

    public RoleEntity findByDisplay(String display) {
        SelectBuilder<RoleEntity> select = new SelectBuilder<>(RoleEntity.class);
        select.addWhereAnd(RoleEntity.COL_DISPLAY + " = ? ", display);
        return executeSingle(select);
    }

    public List<RoleEntity> findByNameIsLike(String name) {
        SelectBuilder<RoleEntity> select = new SelectBuilder<>(RoleEntity.class);
        select.addWhereAnd(RoleEntity.COL_NAME + " like ? ", "%" + name + "%");
        return execute(select);
    }

    public QueryResultSet<RoleEntity> unAssignedRole(SearchParam param) {
        SelectBuilder<RoleEntity> select = new SelectBuilder<>(RoleEntity.class);
        select.setSearchParam(param);
        Map<String, Object> filters = param.getFilters();
        List<String> assignedRoleNames = (List<String>) filters.get("assignedRoleNames");
        if (assignedRoleNames != null && assignedRoleNames.size() > 0) {
            select.addWhereAnd(RoleEntity.COL_NAME + " not in (:assignedRoleNames)");

            Map<String, Object> params = new HashMap<>();
            params.put("assignedRoleNames", assignedRoleNames);
            select.setWhereValuesMap(params);
        }

        select.addOrderByAscending(RoleEntity.COL_NAME);
        return search(select);
    }
}
