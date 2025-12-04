package com.adminpro.tools.gen;

/**
 * ry数据库表列信息
 *
 * @author simon
 */
public class ColumnInfo {
    /**
     * 字段名称
     */
    private String columnName;

    private String columnNameUp;

    /**
     * 字段类型
     */
    private String dataType;

    /**
     * 列描述
     */
    private String columnComment;
    /**
     * 最大长度
     */
    private Integer maxLength;
    /**
     * 是否可以为空
     */
    private String canNull;

    /**
     * Java属性类型 Integer
     */
    private String attrType;
    /**
     * Java属性类型 int
     */
    private String attrtype;
    /**
     * Java属性类型 INTEGER
     */
    private String ATTRTYPE;

    /**
     * Java属性名称(第一个字母大写)，如：user_name => UserName
     */
    private String attrName;

    /**
     * Java属性名称(第一个字母小写)，如：user_name => userName
     */
    private String attrname;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnNameUp() {
        return columnNameUp;
    }

    public void setColumnNameUp(String columnNameUp) {
        this.columnNameUp = columnNameUp;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public String getCanNull() {
        return canNull;
    }

    public void setCanNull(String canNull) {
        this.canNull = canNull;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrtype() {
        return attrtype;
    }

    public void setAttrtype(String attrtype) {
        this.attrtype = attrtype;
    }

    public String getATTRTYPE() {
        return ATTRTYPE;
    }

    public void setATTRTYPE(String ATTRTYPE) {
        this.ATTRTYPE = ATTRTYPE;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrname() {
        return attrname;
    }

    public void setAttrname(String attrname) {
        this.attrname = attrname;
    }
}
