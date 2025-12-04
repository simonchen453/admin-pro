package com.adminpro.core.jdbc.sqlbuilder.dialect;

public final class MySqlDialect extends PageDialect {
    @Override
    public String getPageSql(String rawSql, int pageSize, int pageNo) {
        int from = (pageNo - 1) * pageSize;
        int to = pageSize;

        StringBuilder sb = new StringBuilder();
        sb.append(rawSql);
        sb.append(" LIMIT ");
        sb.append(from).append(", ").append(to);
        return sb.toString();
    }
}
