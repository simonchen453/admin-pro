package com.adminpro.core.jdbc.sqlbuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class DeleteBuilder extends WhereClause {
    private String table;

    public DeleteBuilder(String table) {
        this.table = table;
        where = "";
        whereValues = new ArrayList<Object>();
    }

    public void addValues(Object... objs) {
        whereValues.add(objs);
    }

    public String getSql() {
        final StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(table);
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
        return whereValues.toArray();
    }

}
