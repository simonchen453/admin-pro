package com.adminpro.tools.gen;

import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.SqlExecutor;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 代码生成 数据层
 */
@Repository
public class GenDao extends SqlExecutor<TableInfo, String> {
    /**
     * 查询ry数据库表信息
     *
     * @param param 表信息
     * @return 数据库表列表
     */
    public QueryResultSet<TableInfo> search(SearchParam param) {
        SelectBuilder<TableInfo> selectBuilder = new SelectBuilder<>(getTableInfoRowMapper());
        StringBuilder sql = new StringBuilder();
        sql.append("select table_name, table_comment, create_time, update_time from information_schema.tables ");

        Map<String, Object> filters = param.getFilters();
        String tableName = (String) filters.get("tableName");
        selectBuilder.setQuery(sql.toString());
        selectBuilder.setSearchParam(param);

        selectBuilder.addWhereAnd("table_comment <> ''");
        selectBuilder.addWhereAnd("table_schema = (select database())");

        if (StringUtils.isNotEmpty(tableName)) {
            selectBuilder.addWhereAnd("table_name like ?", "%" + tableName + "%");
            selectBuilder.addWhereOr("table_comment like ?", "%" + tableName + "%");
        }
        return search(selectBuilder);
    }

    public List<TableInfo> findAll() {
        SelectBuilder<TableInfo> selectBuilder = new SelectBuilder<>(getTableInfoRowMapper());
        StringBuilder sql = new StringBuilder();
        sql.append("select table_name, table_comment, create_time, update_time from information_schema.tables ");

        selectBuilder.setQuery(sql.toString());
        selectBuilder.addWhereAnd("table_comment <> ''");
        selectBuilder.addWhereAnd("table_schema = (select database())");

        return execute(selectBuilder);
    }

    /**
     * 根据表名称查询信息
     *
     * @param tableName 表名称
     * @return 表信息
     */
    public TableInfo findTableByName(String tableName) {
        SelectBuilder<TableInfo> selectBuilder = new SelectBuilder<>(getTableInfoRowMapper());
        StringBuilder sql = new StringBuilder();
        sql.append("select table_name, table_comment, create_time, update_time from information_schema.tables ");
        sql.append("where table_comment <> '' and table_schema = (select database()) ");
        sql.append("AND table_name = ? ");
        selectBuilder.setQuery(sql.toString());
        selectBuilder.getWhereValues().add(tableName);
        return executeSingle(selectBuilder);
    }

    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @return 列信息
     */
    public List<ColumnInfo> findTableColumnsByName(String tableName) {
        SelectBuilder<ColumnInfo> selectBuilder = new SelectBuilder<>(getColumnInfoRowMapper());
        StringBuilder sql = new StringBuilder();
        sql.append("select column_name, data_type, column_comment, character_maximum_length, is_nullable from information_schema.columns ");
        sql.append("where table_name = ? and table_schema = (select database()) order by ordinal_position ");
        selectBuilder.setQuery(sql.toString());
        selectBuilder.getWhereValues().add(tableName);
        return execute(selectBuilder);
    }

    protected RowMapper<TableInfo> getTableInfoRowMapper() {
        return new RowMapper<TableInfo>() {
            @Override
            public TableInfo mapRow(ResultSet resultSet, int i) throws SQLException {
                TableInfo entity = new TableInfo();
                entity.setTableName(resultSet.getString("table_name"));
                entity.setTableComment(resultSet.getString("table_comment"));
                entity.setCreatedDate(resultSet.getTimestamp("create_time"));
                entity.setUpdatedDate(resultSet.getTimestamp("update_time"));
                return entity;
            }
        };
    }

    protected RowMapper<ColumnInfo> getColumnInfoRowMapper() {
        return new RowMapper<ColumnInfo>() {
            @Override
            public ColumnInfo mapRow(ResultSet resultSet, int i) throws SQLException {
                ColumnInfo entity = new ColumnInfo();
                entity.setColumnName(resultSet.getString("column_name"));
                entity.setDataType(resultSet.getString("data_type"));
                entity.setColumnComment(resultSet.getString("column_comment"));
                entity.setMaxLength(resultSet.getInt("character_maximum_length"));
                entity.setCanNull(resultSet.getString("is_nullable"));
                return entity;
            }
        };
    }
}
