package com.adminpro.core.jdbc.sqlbuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UpdateBuilder extends WhereClause {
    private String table;
    private StringBuilder columns;
    private List<Object> values;

    public UpdateBuilder(String table) {
        this.table = table;
        columns = new StringBuilder();
        values = new ArrayList<>();
        where = "";
        whereValues = new ArrayList<Object>();
    }

    public void addColumnValue(String column, Object value) {
        columns.append(column).append(" = ?, ");
        values.add(value);
    }

    public void addColumnColumn(String col1, String col2) {
        columns.append(col1).append(" = ").append(col2).append(", ");
    }

    public void addColumn(String column) {
        columns.append(column).append(" = ?, ");
    }

    @Override
    public void setWhereClause(String where) {
        this.where = where;
    }

    public void addValues(Object... objs) {
        values.add(objs);
    }

    public String getSql() {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(table).append(" SET ");
        sql.append(columns.toString().substring(0, columns.lastIndexOf(", ")));
        if (!StringUtils.isEmpty(where)) {
            sql.append(" WHERE ").append(where);
        }
        return sql.toString();
    }

    @Override
    public String toString() {
        return getSql();
    }

    public Object[] getParamValues() {
        List<Object> pvs = new ArrayList<>();
        if (values != null && !values.isEmpty()) {
            pvs.addAll(values);
        }
        if (whereValues != null && !whereValues.isEmpty()) {
            pvs.addAll(whereValues);
        }
        return pvs.toArray(new Object[pvs.size()]);
    }

    public List<Object> getValues() {
        return values;
    }
}
