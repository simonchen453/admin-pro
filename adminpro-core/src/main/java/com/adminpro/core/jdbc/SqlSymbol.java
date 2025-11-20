package com.adminpro.core.jdbc;

public interface SqlSymbol {
    String LIKE = " like ? ";
    String EQ = " = ? ";
    String PERCENT = "%";
    String GT = " > ? ";
    String GTE = " >= ? ";
    String LT = " < ? ";
    String LTE = " <= ? ";
}
