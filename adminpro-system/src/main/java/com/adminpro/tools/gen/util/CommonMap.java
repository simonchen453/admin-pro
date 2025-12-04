package com.adminpro.tools.gen.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用数据库映射Map数据
 */
public class CommonMap {
    /**
     * 状态编码转换
     */
    public static Map<String, String> javaTypeMap = new HashMap<String, String>();
    public static Map<String, String> javatypeMap = new HashMap<String, String>();
    public static Map<String, String> tsTypeMap = new HashMap<String, String>();

    static {
        initJavaTypeMap();
        initTsTypeMap();
    }

    /**
     * 返回状态映射
     */
    public static void initJavaTypeMap() {
        javaTypeMap.put("tinyint", "Boolean");
        javaTypeMap.put("smallint", "Integer");
        javaTypeMap.put("mediumint", "Integer");
        javaTypeMap.put("int", "Integer");
        javaTypeMap.put("integer", "Integer");
        javaTypeMap.put("bigint", "Long");
        javaTypeMap.put("float", "Float");
        javaTypeMap.put("double", "Double");
        javaTypeMap.put("decimal", "BigDecimal");
        javaTypeMap.put("bit", "Boolean");
        javaTypeMap.put("char", "String");
        javaTypeMap.put("varchar", "String");
        javaTypeMap.put("tinytext", "String");
        javaTypeMap.put("text", "String");
        javaTypeMap.put("mediumtext", "String");
        javaTypeMap.put("longtext", "String");
        javaTypeMap.put("time", "Date");
        javaTypeMap.put("date", "Date");
        javaTypeMap.put("datetime", "Date");
        javaTypeMap.put("timestamp", "Date");


        javatypeMap.put("tinyint", "Boolean");
        javatypeMap.put("smallint", "Int");
        javatypeMap.put("mediumint", "Int");
        javatypeMap.put("int", "Int");
        javatypeMap.put("integer", "Int");
        javatypeMap.put("bigint", "Long");
        javatypeMap.put("float", "Float");
        javatypeMap.put("double", "Double");
        javatypeMap.put("decimal", "BigDecimal");
        javatypeMap.put("bit", "Boolean");
        javatypeMap.put("char", "String");
        javatypeMap.put("varchar", "String");
        javatypeMap.put("tinytext", "String");
        javatypeMap.put("text", "String");
        javatypeMap.put("mediumtext", "String");
        javatypeMap.put("longtext", "String");
        javatypeMap.put("time", "Date");
        javatypeMap.put("date", "Date");
        javatypeMap.put("datetime", "Date");
        javatypeMap.put("timestamp", "Date");
    }

    /**
     * 初始化 TypeScript 类型映射
     */
    public static void initTsTypeMap() {
        tsTypeMap.put("tinyint", "boolean");
        tsTypeMap.put("smallint", "number");
        tsTypeMap.put("mediumint", "number");
        tsTypeMap.put("int", "number");
        tsTypeMap.put("integer", "number");
        tsTypeMap.put("bigint", "number");
        tsTypeMap.put("float", "number");
        tsTypeMap.put("double", "number");
        tsTypeMap.put("decimal", "number");
        tsTypeMap.put("bit", "boolean");
        tsTypeMap.put("char", "string");
        tsTypeMap.put("varchar", "string");
        tsTypeMap.put("tinytext", "string");
        tsTypeMap.put("text", "string");
        tsTypeMap.put("mediumtext", "string");
        tsTypeMap.put("longtext", "string");
        tsTypeMap.put("time", "string");
        tsTypeMap.put("date", "string");
        tsTypeMap.put("datetime", "string");
        tsTypeMap.put("timestamp", "string");
    }
}
