package com.adminpro.rbac.domains.entity.usermenu;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 用户菜单分配表 数据库持久层
 *
 * @author simon
 * @date 2020-05-21
 */
@Repository
public class UserMenuAssignDao extends BaseDao<UserMenuAssignEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<UserMenuAssignEntity> search(SearchParam param) {
        SelectBuilder<UserMenuAssignEntity> select = new SelectBuilder<UserMenuAssignEntity>(UserMenuAssignEntity.class);
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
    public List<UserMenuAssignEntity> findByParam(SearchParam param) {
        SelectBuilder<UserMenuAssignEntity> select = new SelectBuilder<UserMenuAssignEntity>(UserMenuAssignEntity.class);
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
        //TODO 页面过滤条件
        Map<String, Object> filters = param.getFilters();
        String condition = (String) filters.get("condition");
        if (StringUtils.isNotEmpty(condition)) {
            //select.addWhereAnd(UserMenuAssignEntity.COL_TITLE + " like ?", "%" + condition+"%");
        }
    }
}
