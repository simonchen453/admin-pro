package com.adminpro.rbac.domains.entity.domain;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 用户域表 数据库持久层
 *
 * @author simon
 * @date 2018-09-06
 */
@Component
public class DomainDao extends BaseDao<DomainEntity, String> {

    public QueryResultSet<DomainEntity> search(SearchParam param) {
        SelectBuilder<DomainEntity> select = new SelectBuilder<DomainEntity>(DomainEntity.class);
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
    public List<DomainEntity> findByParam(SearchParam param) {
        SelectBuilder<DomainEntity> select = new SelectBuilder<DomainEntity>(DomainEntity.class);
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
        String display = (String) filters.get("display");
        if (StringUtils.isNotEmpty(name)) {
            select.addWhereAnd(DomainEntity.COL_NAME + " like ?", "%" + name + "%");
        }
        if (StringUtils.isNotEmpty(display)) {
            select.addWhereAnd(DomainEntity.COL_DISPLAY + " like ?", "%" + display + "%");
        }
    }

    public DomainEntity findByName(String name) {
        SelectBuilder<DomainEntity> select = new SelectBuilder<DomainEntity>(DomainEntity.class);
        select.addWhereAnd(DomainEntity.COL_NAME + " = ? ", name);
        return executeSingle(select);
    }

    public DomainEntity findByDisplay(String display) {
        SelectBuilder<DomainEntity> select = new SelectBuilder<DomainEntity>(DomainEntity.class);
        select.addWhereAnd(DomainEntity.COL_DISPLAY + " = ? ", display);
        return executeSingle(select);
    }

}
