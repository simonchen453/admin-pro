package com.adminpro.core.jdbc.sqlbuilder.dialect;

public abstract class PageDialect {
    /**
     * get the new SQL for pagination.
     *
     * @param rawSql
     * @param pageSize
     * @param pageNo
     * @return
     */
    public abstract String getPageSql(String rawSql, int pageSize, int pageNo);

    /**
     * get the new SQL for total records
     *
     * @param rawSql
     * @return
     */
    public String getCountSql(String rawSql) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) FROM ( ");
        sb.append(rawSql);
        sb.append(" ) TT");
        return sb.toString();
    }
}
