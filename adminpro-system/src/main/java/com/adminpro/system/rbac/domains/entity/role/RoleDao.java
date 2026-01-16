package com.adminpro.system.rbac.domains.entity.role;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.framework.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.system.core.common.helper.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    /**
     * 根据角色名称列表批量查询
     *
     * @param names 角色名称列表
     * @return 角色列表
     */
    public List<RoleEntity> findByNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }
        SelectBuilder<RoleEntity> select = new SelectBuilder<>(RoleEntity.class);
        select.addWhereAnd(RoleEntity.COL_NAME + " in (:names)");
        Map<String, Object> params = new HashMap<>();
        params.put("names", names);
        select.setWhereValuesMap(params);
        return execute(select);
    }

    /**
     * 根据ID列表批量查询
     *
     * @param ids 角色ID列表
     * @return 角色列表
     */
    public List<RoleEntity> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        SelectBuilder<RoleEntity> select = new SelectBuilder<>(RoleEntity.class);
        select.addWhereAnd(RoleEntity.COL_ID + " in (:ids)");
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        select.setWhereValuesMap(params);
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

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     */
    public void deleteByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        DeleteBuilder delete = new DeleteBuilder(RoleEntity.TABLE_NAME);
        delete.addWhereAnd(RoleEntity.COL_ID + " IN ", ids.toArray());
        execute(delete);
    }
}
