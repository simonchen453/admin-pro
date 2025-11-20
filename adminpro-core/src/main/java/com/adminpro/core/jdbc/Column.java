package com.adminpro.core.jdbc;

import java.io.Serializable;

public class Column implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String type;
    private transient Object value;

    public Column(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
