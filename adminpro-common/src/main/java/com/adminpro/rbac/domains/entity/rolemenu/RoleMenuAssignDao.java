package com.adminpro.rbac.domains.entity.rolemenu;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 角色和菜单关联表 数据库持久层
 *
 * @author simon
 * @date 2020-05-24
 */
@Repository
public class RoleMenuAssignDao extends BaseDao<RoleMenuAssignEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<RoleMenuAssignEntity> search(SearchParam param) {
        SelectBuilder<RoleMenuAssignEntity> select = new SelectBuilder<RoleMenuAssignEntity>(RoleMenuAssignEntity.class);
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
    public List<RoleMenuAssignEntity> findByParam(SearchParam param) {
        SelectBuilder<RoleMenuAssignEntity> select = new SelectBuilder<RoleMenuAssignEntity>(RoleMenuAssignEntity.class);
        prepareSelectBuilder(select, param);
        return execute(select);
    }

    public List<RoleMenuAssignEntity> findByRoleName(String roleName) {
        SelectBuilder<RoleMenuAssignEntity> select = new SelectBuilder<RoleMenuAssignEntity>(RoleMenuAssignEntity.class);
        select.addWhereAnd(RoleMenuAssignEntity.COL_ROLE_NAME + " = ?", roleName);
        return execute(select);
    }

    public List<RoleMenuAssignEntity> findByMenuName(String menuName) {
        SelectBuilder<RoleMenuAssignEntity> select = new SelectBuilder<RoleMenuAssignEntity>(RoleMenuAssignEntity.class);
        select.addWhereAnd(RoleMenuAssignEntity.COL_MENU_NAME + " = ?", menuName);
        return execute(select);
    }

    public void deleteByRoleName(String roleName) {
        DeleteBuilder delete = new DeleteBuilder(RoleMenuAssignEntity.TABLE_NAME);
        delete.addWhereAnd(RoleMenuAssignEntity.COL_ROLE_NAME + " = ? ", roleName);
        execute(delete);
    }

    /**
     * 准备查询条件
     *
     * @param param
     * @return
     */
    private void prepareSelectBuilder(SelectBuilder select, SearchParam param) {
        //TODO 页面过滤条件
        Map<String, Object> filters = param.getFilters();
        String condition = (String) filters.get("condition");
        if (StringUtils.isNotEmpty(condition)) {
            //select.addWhereAnd(RoleMenuAssignEntity.COL_TITLE + " like ?", "%" + condition+"%");
        }
    }
}
