package com.adminpro.core.jdbc.sqlbuilder.dialect;

/**
 * @author simon
 */
public class Db2Dialect extends PageDialect {

    @Override
    public String getPageSql(String rawSql, int pageSize, int pageNo) {
        int from = (pageNo - 1) * pageSize + 1;
        int to = pageNo * pageSize;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM (SELECT B.*, ROWNUMBER() OVER() AS RN FROM ( ");
        sb.append(rawSql);
        sb.append(" ) B) A WHERE A.RN BETWEEN ");
        sb.append(from).append(" AND ").append(to);
        return sb.toString();
    }
}
