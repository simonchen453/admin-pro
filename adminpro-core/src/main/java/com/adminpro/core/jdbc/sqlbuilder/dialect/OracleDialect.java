package com.adminpro.core.jdbc.sqlbuilder.dialect;

/**
 * @author simon
 */
public class OracleDialect extends PageDialect {

    @Override
    public String getPageSql(String rawSql, int pageSize, int pageNo) {
        int from = (pageNo - 1) * pageSize + 1;
        int to = pageNo * pageSize;

        StringBuilder sb = new StringBuilder();
        /*
        sb.append("SELECT * FROM (SELECT T.*, ROWNUM AS RN FROM (");
        sb.append(rawSql);
        sb.append(") T WHERE ROWNUM <= ").append(to);
        sb.append(") TT WHERE RN >= ").append(from);
        */
        sb.append("SELECT * FROM (SELECT T.*, ROWNUM AS RN FROM (");
        sb.append(rawSql);
        sb.append(") T");
        sb.append(") TT WHERE RN <= ").append(to);
        sb.append(" AND RN >= ").append(from);
        return sb.toString();
    }
}
