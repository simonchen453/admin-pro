package com.adminpro.rbac.domains.entity.post;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.core.jdbc.sqlbuilder.InsertBuilder;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.core.jdbc.sqlbuilder.UpdateBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 职位表 数据库持久层
 *
 * @author simon
 * @date 2020-05-21
 */
@Repository
public class PostDao extends BaseDao<PostEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<PostEntity> search(SearchParam param) {
        SelectBuilder<PostEntity> select = new SelectBuilder<PostEntity>(getPostRowMapper());
        select.setTable(PostEntity.TABLE_NAME);
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
    public List<PostEntity> findByParam(SearchParam param) {
        SelectBuilder<PostEntity> select = new SelectBuilder<PostEntity>(getPostRowMapper());
        select.setTable(PostEntity.TABLE_NAME);
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
        String code = (String) filters.get("code");
        String name = (String) filters.get("name");
        String status = (String) filters.get("status");
        if (StringUtils.isNotEmpty(code)) {
            select.addWhereAnd(PostEntity.COL_CODE + " like ?", "%" + code + "%");
        }
        if (StringUtils.isNotEmpty(name)) {
            select.addWhereAnd(PostEntity.COL_NAME + " like ?", "%" + name + "%");
        }
        if (StringUtils.isNotEmpty(status)) {
            select.addWhereAnd(PostEntity.COL_STATUS + " = ?", status);
        }
    }

    /**
     * 创建 PostEntity
     *
     * @param entity
     */
    @Override
    public void create(PostEntity entity) {
        InsertBuilder insert = new InsertBuilder(PostEntity.TABLE_NAME);

        insert.addColumnValue(PostEntity.COL_ID, entity.getId());
        insert.addColumnValue(PostEntity.COL_CODE, entity.getCode());
        insert.addColumnValue(PostEntity.COL_NAME, entity.getName());
        insert.addColumnValue(PostEntity.COL_SORT, entity.getSort());
        insert.addColumnValue(PostEntity.COL_STATUS, entity.getStatus());
        insert.addColumnValue(PostEntity.COL_REMARK, entity.getRemark());

        handleAuditColumnValues(insert, entity);
        execute(insert);
    }

    /**
     * 更新 PostEntity
     *
     * @param entity
     */
    @Override
    public void update(PostEntity entity) {
        UpdateBuilder update = new UpdateBuilder(PostEntity.TABLE_NAME);

        update.addColumnValue(PostEntity.COL_CODE, entity.getCode());
        update.addColumnValue(PostEntity.COL_NAME, entity.getName());
        update.addColumnValue(PostEntity.COL_SORT, entity.getSort());
        update.addColumnValue(PostEntity.COL_STATUS, entity.getStatus());
        update.addColumnValue(PostEntity.COL_REMARK, entity.getRemark());

        handleAuditColumnValues(update, entity);
        update.addWhereAnd(PostEntity.COL_ID + " = ?", entity.getId());
        execute(update);
    }

    /**
     * 根据id查找PostEntity对象
     *
     * @param id
     * @return
     */
    @Override
    public PostEntity findById(String id) {
        SelectBuilder<PostEntity> select = new SelectBuilder<PostEntity>(getPostRowMapper());
        select.setTable(PostEntity.TABLE_NAME);
        select.addWhereAnd(PostEntity.COL_ID + " = ? ", id);
        return executeSingle(select);
    }

    public PostEntity findByCode(String code) {
        SelectBuilder<PostEntity> select = new SelectBuilder<PostEntity>(getPostRowMapper());
        select.setTable(PostEntity.TABLE_NAME);
        select.addWhereAnd(PostEntity.COL_CODE + " = ? ", code);
        return executeSingle(select);
    }

    public PostEntity findByName(String name) {
        SelectBuilder<PostEntity> select = new SelectBuilder<PostEntity>(getPostRowMapper());
        select.setTable(PostEntity.TABLE_NAME);
        select.addWhereAnd(PostEntity.COL_NAME + " = ? ", name);
        return executeSingle(select);
    }

    /**
     * 删除PostEntity
     *
     * @param id
     * @return
     */
    @Override
    public void delete(String id) {
        DeleteBuilder delete = new DeleteBuilder(PostEntity.TABLE_NAME);
        delete.addWhereAnd(PostEntity.COL_ID + " = ? ", id);
        execute(delete);
    }

    /**
     * PostEntity表映射关系
     *
     * @return
     */
    protected RowMapper<PostEntity> getPostRowMapper() {
        return new RowMapper<PostEntity>() {
            @Override
            public PostEntity mapRow(ResultSet resultSet, int i) throws SQLException {
                PostEntity entity = new PostEntity();

                entity.setId(resultSet.getString(PostEntity.COL_ID));
                entity.setCode(resultSet.getString(PostEntity.COL_CODE));
                entity.setName(resultSet.getString(PostEntity.COL_NAME));
                entity.setSort(resultSet.getInt(PostEntity.COL_SORT));
                entity.setStatus(resultSet.getString(PostEntity.COL_STATUS));
                entity.setRemark(resultSet.getString(PostEntity.COL_REMARK));

                //处理日志字段
                retrieveAuditField(entity, resultSet);

                return entity;
            }
        };
    }
}
