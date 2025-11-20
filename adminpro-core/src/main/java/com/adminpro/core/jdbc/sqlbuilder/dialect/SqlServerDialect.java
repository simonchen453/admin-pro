package com.adminpro.core.jdbc.sqlbuilder.dialect;

/**
 * @author simon
 */
public class SqlServerDialect extends PageDialect {

    /**
     * ORDER BY clause is required for pagination in SQL Server.
     */
    @Override
    public String getPageSql(String rawSql, int pageSize, int pageNo) {
        int from = (pageNo - 1) * pageSize;
        int to = pageSize;

        StringBuilder sb = new StringBuilder();
        sb.append(rawSql);
        sb.append(" OFFSET ").append(from).append(" ROWS FETCH NEXT ").append(to).append(" ROWS ONLY");
        return sb.toString();
    }
}
