package com.adminpro.rbac.domains.entity.user;

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
 * 用户标签表 数据库持久层
 *
 * @author simon
 * @date 2020-05-21
 */
@Repository
public class UserTagDao extends BaseDao<UserTagEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<UserTagEntity> search(SearchParam param) {
        SelectBuilder<UserTagEntity> select = new SelectBuilder<UserTagEntity>(UserTagEntity.class);
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
    public List<UserTagEntity> findByParam(SearchParam param) {
        SelectBuilder<UserTagEntity> select = new SelectBuilder<UserTagEntity>(UserTagEntity.class);
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
            //select.addWhereAnd(UserTagEntity.COL_TITLE + " like ?", "%" + condition+"%");
        }
    }

    public List<UserTagEntity> findByUserIden(UserIden userIden) {
        SelectBuilder<UserTagEntity> select = new SelectBuilder<UserTagEntity>(UserTagEntity.class);
        select.addWhereAnd(UserTagEntity.COL_USER_DOMAIN + " = ? ", userIden.getUserDomain());
        select.addWhereAnd(UserTagEntity.COL_USER_ID + " = ? ", userIden.getUserId());
        return execute(select);
    }

    public void deleteByUserIden(UserIden userIden) {
        DeleteBuilder delete = new DeleteBuilder(UserTagEntity.TABLE_NAME);
        delete.addWhereAnd(UserTagEntity.COL_USER_DOMAIN + " = ? ", userIden.getUserDomain());
        delete.addWhereAnd(UserTagEntity.COL_USER_ID + " = ? ", userIden.getUserId());
        execute(delete);
    }
}
