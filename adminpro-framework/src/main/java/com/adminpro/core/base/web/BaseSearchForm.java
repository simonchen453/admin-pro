package com.adminpro.core.base.web;

import com.adminpro.core.jdbc.annotation.Column;
import com.adminpro.core.jdbc.utils.ClassUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author simon
 */
public class BaseSearchForm<E> implements Serializable {
    private int pageNo = 1;
    private int pageSize = 10;
    private String sortBy;
    private String sortDir;
    private Class<E> genericType;

    public BaseSearchForm() {
        try {
            genericType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception e) {

        }
    }

    public Class<E> getGenericType() {
        return genericType;
    }

    @JsonIgnore
    public Map<String, String> getFiledColumnMap(){
        Map<String, String> fieldMap = new HashMap<String, String>();
        if (genericType != null) {
            try {
                Field[] fields = ClassUtil.getAllFields(genericType);
                if (fields != null && fields.length > 0) {
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Column.class)) {
                            field.setAccessible(true);
                            Column col = field.getAnnotation(Column.class);
                            if (col != null) {
                                fieldMap.put(field.getName(), col.name());
                            }
                        }
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return fieldMap;
    }
    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }


}
