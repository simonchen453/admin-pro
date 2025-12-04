package com.adminpro.framework.jdbc;

public interface SqlSymbol {
    String LIKE = " like ? ";
    String EQ = " = ? ";
    String PERCENT = "%";
    String GT = " > ? ";
    String GTE = " >= ? ";
    String LT = " < ? ";
    String LTE = " <= ? ";
}
