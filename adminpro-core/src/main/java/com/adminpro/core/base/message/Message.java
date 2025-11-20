package com.adminpro.core.base.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class Message {

    // error message
    public static final String TYPE_ERROR = "E";
    // successful message
    public static final String TYPE_INFO = "I";
    // warning message
    public static final String TYPE_WARNING = "W";

    private static final String DEFAULT_TYPE = TYPE_INFO;

    private String field;
    private String message;
    private String type;

    Message(String type, String field, String message) {
        Validate.notNull(message);
        Validate.notNull(type);

        this.type = type;
        this.field = StringUtils.trimToEmpty(field);
        this.message = message;
    }

    public String getType() {
        return type == null ? DEFAULT_TYPE : type;
    }

    void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    @JsonIgnore
    public boolean isTypeError() {
        return StringUtils.equals(type, TYPE_ERROR);
    }

    @JsonIgnore
    public boolean isTypeInfo() {
        return StringUtils.equals(type, TYPE_INFO);
    }

    @JsonIgnore
    public boolean isTypeWarn() {
        return StringUtils.equals(type, TYPE_WARNING);
    }

}
