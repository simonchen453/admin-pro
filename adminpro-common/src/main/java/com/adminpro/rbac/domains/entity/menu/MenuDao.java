package com.adminpro.rbac.domains.entity.menu;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.rbac.domains.entity.rolemenu.RoleMenuAssignEntity;
import com.adminpro.rbac.domains.entity.user.UserIden;
import com.adminpro.rbac.domains.entity.userrole.UserRoleAssignEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 菜单权限表 数据库持久层
 *
 * @author simon
 * @date 2020-05-21
 */
@Repository
public class MenuDao extends BaseDao<MenuEntity, String> {

    public static final String MENU_LIST = "select distinct m.col_id, m.col_parent_id, m.col_name, m.col_display, m.col_url, m.col_visible, m.col_status, col_permission, m.col_is_frame, m.col_type, m.col_icon, " +
            "m.col_order_num,m.col_remark, " +
            "m.col_created_by_user_domain, m.col_created_by_user_id, m.col_created_date, " +
            "m.col_updated_by_user_domain, m.col_updated_by_user_id, m.col_updated_date " +
            "from sys_menu_tbl m " +
            "left join sys_role_menu_assign_tbl rm on rm.col_menu_name = m.col_name " +
            "left join sys_user_role_assign_tbl ur on ur.col_role_name = rm.col_role_name " +
            "left join sys_role_tbl r on r.col_name = ur.col_role_name ";

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<MenuEntity> search(SearchParam param) {
        SelectBuilder<MenuEntity> select = new SelectBuilder<MenuEntity>(MenuEntity.class);
        select.setQuery(MENU_LIST);
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
    public List<MenuEntity> findByParam(SearchParam param) {
        SelectBuilder<MenuEntity> select = new SelectBuilder<MenuEntity>(MenuEntity.class);
        select.setQuery(MENU_LIST);
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
        String name = (String) filters.get("name");
        Boolean visible = (Boolean) filters.get("visible");
        String status = (String) filters.get("status");
        UserIden userIden = (UserIden) filters.get("userIden");
        String roleName = (String) filters.get("roleName");
        String commonRole = (String) filters.get("commonRole");
        if (StringUtils.isNotEmpty(name)) {
            select.addWhereAnd("m." + MenuEntity.COL_DISPLAY + " like ?", "%" + name + "%");
        }
        if (StringUtils.isNotEmpty(status)) {
            select.addWhereAnd("m." + MenuEntity.COL_STATUS + " = ?", status);
        }
        if (visible != null) {
            select.addWhereAnd("m." + MenuEntity.COL_VISIBLE + " = ?", visible);
        }
        if (userIden != null) {
            select.addWhereAnd("ur." + UserRoleAssignEntity.COL_USER_DOMAIN + " = ?", userIden.getUserDomain());
            select.addWhereAnd("ur." + UserRoleAssignEntity.COL_USER_ID + " = ?", userIden.getUserId());
        }
        if (StringUtils.isNotEmpty(commonRole)) {
            //查找common role下面的所有菜单，包含父菜单
            select.addWhereAnd("rm." + RoleMenuAssignEntity.COL_ROLE_NAME + " = ?", commonRole);
        }
        if (StringUtils.isNotEmpty(roleName)) {
            //查找单个role下面的子菜单，不包含父菜单
            select.addWhereAnd("rm." + RoleMenuAssignEntity.COL_ROLE_NAME + " = ?", roleName);
            select.addWhereAnd("m.col_id not in (select m.col_parent_id from sys_menu_tbl m inner join sys_role_menu_assign_tbl rm on m.col_name = rm.col_menu_name and rm.col_role_name = ?)", roleName);
        }
        select.addOrderByAscending("m." + MenuEntity.COL_PARENT_ID);
        select.addOrderByAscending("m." + MenuEntity.COL_ORDER_NUM);
    }

    public MenuEntity findByName(String name) {
        SelectBuilder<MenuEntity> select = new SelectBuilder<MenuEntity>(MenuEntity.class);
        select.addWhereAnd(MenuEntity.COL_NAME + " = ? ", name);
        return executeSingle(select);
    }

    public List<String> findPermissionByRoleName(String roleName) {
        String sql = "select m.col_permission from sys_menu_tbl m " +
                "left join sys_role_menu_assign_tbl rma on rma.col_menu_name = m.col_name " +
                "left join sys_role_tbl r on r.col_name = rma.col_role_name ";
        SelectBuilder<String> select = new SelectBuilder<String>(new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString(1);
            }
        });
        select.setQuery(sql);
        select.addWhereAnd("r.col_name = ?", roleName);
        return execute(select);
    }
}
