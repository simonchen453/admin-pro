package com.adminpro.core.jdbc.sqlbuilder;

import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.jdbc.DBRowMapper;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.annotation.Table;
import com.adminpro.core.jdbc.sqlbuilder.dialect.*;
import com.adminpro.core.jdbc.utils.DbUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectBuilder<T> extends WhereClause {
    private static final Pattern pattern = Pattern.compile("\\[[A-Za-z0-9]+\\]");

    private String table;
    private StringBuilder columns;
    private RowMapper<T> rowMapper;
    private List<String> groupBy;
    private List<String> having;
    private List<String> orderBy;
    private String window;
    private String query;
    private int pageSize;
    private int pageNo;
    protected Integer limit;
    private boolean forUpdate = false;

    public SelectBuilder(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
        columns = new StringBuilder();
        where = "";
        window="";
        whereValues = new ArrayList<>();
        groupBy = new ArrayList<>();
        having = new ArrayList<>();
        orderBy = new ArrayList<>();
    }

    public SelectBuilder(DBRowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
        columns = new StringBuilder();
        where = "";
        window="";
        whereValues = new ArrayList<>();
        groupBy = new ArrayList<>();
        having = new ArrayList<>();
        orderBy = new ArrayList<>();
        Class<T> clazz = rowMapper.getGenericType();
        try {
            Table tbl = clazz.getAnnotation(Table.class);
            table = tbl.name();
        } catch (Exception x) {
            // ignore the ex
        }
    }

    public SelectBuilder(Class<T> clazz) {
        this(new DBRowMapper<T>(clazz));
    }

    public void addColumn(String column) {
        columns.append(column).append(", ");
    }

    public void addGroupBy(String column) {
        if (StringUtils.isBlank(columns.toString()) || columns.indexOf(column + ", ") != -1) {
            groupBy.add(column);
        } else {
            throw new BaseRuntimeException(String.format("%s is not in the selected columns list.", column));
        }
    }

    public void addHaving(String name, String operator, String value) {
        having.add(String.format("%s %s '%s'", name, operator, value));
    }

    public void addOrderBy(String column, String type) {
        if (StringUtils.isNotBlank(column)) {
            if (StringUtils.isBlank(type)) {
                type = "ASC";
            }
            orderBy.add(column + " " + type.toUpperCase());
        }
    }

    public void addOrderByAscending(String column) {
        addOrderBy(column, "ASC");
    }

    public void addOrderByDescending(String column) {
        addOrderBy(column, "DESC");
    }

    public void setSearchParam(SearchParam param) {
        if (param != null) {
            pageSize = param.getPageSize();
            pageNo = param.getPageNo();

            if (param.getOrderBy() != null) {
                for (Map.Entry<String, String> entry : param.getOrderBy().entrySet()) {
                    addOrderBy(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public String getMainSql() {
        final StringBuilder sql = new StringBuilder();
        if (StringUtils.isNotBlank(table) && StringUtils.isBlank(query)) {
            sql.append("SELECT ");
            if (StringUtils.isBlank(columns.toString())) {
                sql.append('*');
            } else {
                sql.append(columns.toString().substring(0, columns.lastIndexOf(", ")));
            }
            sql.append(" FROM ");
            sql.append(table);
        } else {
            sql.append(query);
        }
        // where clause
        if (!StringUtils.isEmpty(where)) {
            sql.append(" WHERE ").append(where);
        }

        // group by clause
        if (!groupBy.isEmpty()) {
            sql.append(" GROUP BY ");
            for (Iterator<String> i = groupBy.iterator(); i.hasNext(); ) {
                sql.append(i.next());
                if (i.hasNext()) {
                    sql.append(", ");
                }
            }
        }
        // having clause only if there is a groupby
        if (!having.isEmpty()) {
            if (!groupBy.isEmpty()) {
                throw new BaseRuntimeException("Can not have a 'HAVING' clause without a 'GROUP BY' clause!");
            } else {
                sql.append(" HAVING ");
                for (Iterator<String> i = having.iterator(); i.hasNext(); ) {
                    sql.append(i.next());
                    if (i.hasNext()) {
                        sql.append(" AND ");
                    }
                }
            }
        }

        if (StringUtils.isNotEmpty(window)){
            sql.append(" " ).append(window).append(" ");
        }
        return sql.toString();
    }

    public String getCountSql() {
        String sql = getMainSql();
        return "SELECT COUNT(*) FROM (" + sql + ") TT";
    }

    public String getSql() {
        final StringBuilder sql = new StringBuilder();
        sql.append(getMainSql());
        // order by clause
        if (!orderBy.isEmpty()) {
            sql.append(" ORDER BY ");
            for (Iterator<String> i = orderBy.iterator(); i.hasNext(); ) {
                sql.append(i.next());
                if (i.hasNext()) {
                    sql.append(", ");
                }
            }
        }
        // paging, it's only for Oracle
        String retStr = sql.toString();
        if (pageSize > 0 && pageNo > 0) {
            PageDialect dialect = null;
            if (DbUtil.isOracle()) {
                dialect = new OracleDialect();
            } else if (DbUtil.isDb2()) {
                dialect = new Db2Dialect();
            } else if (DbUtil.isSqlServer()) {
                dialect = new SqlServerDialect();
            } else {    // mysql is default
                dialect = new MySqlDialect();
            }

            retStr = dialect.getPageSql(retStr, pageSize, pageNo);
            return retStr;
        }

        if (limit != null && DbUtil.isMySql()) {
            sql.append(" limit ").append(limit);
        }

        if (forUpdate) {
            sql.append(" for update ");
        }

        return sql.toString();
    }

    @Override
    public String toString() {
        return getSql();
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setWindow(String window){
        this.window = window;
    }

    public void setQuery(String query, SearchParam param) {
        if (StringUtils.isBlank(query) || param == null) {
            return;
        }

        Map<String, Object> filters = param.getFilters();
        // process the replacement (#{}) in the sql
        Map<String, Object> filterMap = new LinkedHashMap<>();
        // [variable]
        Matcher matcher = pattern.matcher(query);
        int uniqueKey = 1;
        while (matcher.find()) {
            String var = matcher.group();
            var = var.substring(1, var.length() - 1);
            Object val = filters.get(var);
            if (val == null) {
                throw new BaseRuntimeException(String.format("The variable [%s] is not set in query parameters.", var));
            }

            filterMap.put(String.valueOf(uniqueKey), val);
            uniqueKey++;
        }

        // set query sql
        setQuery(matcher.replaceAll("?"));

        // set query parameters
        if (filterMap.size() > 0) {
            setWhereValues(new ArrayList<>(filterMap.values()));
        } else {
            setWhereValues(new ArrayList<>(filters.values()));
        }
        // set order by
        if (!param.isSortInMemory()) {
            String orderByColumn = param.getSortField();
            if (StringUtils.isNotBlank(orderByColumn)) {
                if ("DESC".equalsIgnoreCase(param.getSortType())) {
                    addOrderByDescending(orderByColumn);
                } else {
                    addOrderByAscending(orderByColumn);
                }
            }
        }
        // set paging
        pageSize = param.getPageSize();
        pageNo = param.getPageNo();
    }

    public void setQuery(String query, Object... params) {
        // set query sql
        setQuery(query);

        // set query parameters
        setWhereValues(Arrays.asList(params));
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public RowMapper<T> getRowMapper() {
        return rowMapper;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void forUpdate() {
        this.forUpdate = true;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
