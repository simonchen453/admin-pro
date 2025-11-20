package com.adminpro.core.jdbc.sqlbuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author simon
 */
public class WhereClause {
    protected String where;
    protected List<Object> whereValues;
    protected Map<String, Object> whereValuesMap = new HashMap<>();

    public void setWhereClause(String where) {
        this.where = where;
    }

    public void setWhereClause(String where, Object... objs) {
        this.where = where;
        whereValues.addAll(Arrays.asList(objs));
    }

    public void addWhereAnd(String and, Object... objs) {
        if (!StringUtils.isEmpty(where)) {
            where += " AND";
        }
        where += " " + and;
        if (objs != null) {
            whereValues.addAll(Arrays.asList(objs));
        }
    }

    public void addWhereOr(String or, Object... objs) {
        if (!StringUtils.isEmpty(where)) {
            where += " OR ";
        }
        where += " " + or;
        if (objs != null) {
            whereValues.addAll(Arrays.asList(objs));
        }
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public void setWhereValues(List<Object> whereValues) {
        this.whereValues = whereValues;
    }

    public void setWhereValuesMap(Map<String, Object> whereValues) {
        this.whereValuesMap = whereValues;
    }

    public Map<String, Object> getWhereValuesMap() {
        return whereValuesMap;
    }

    public String getWhere() {
        return where;
    }

    public List<Object> getWhereValues() {
        return whereValues;
    }
}
