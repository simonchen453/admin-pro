package com.adminpro.core.jdbc.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class ResultOrder {
    public static final String SORT_ASC = "asc";
    public static final String SORT_DESC = "desc";

    private String field;
    private boolean asc;

    private ResultOrder() {

    }

    public ResultOrder(String field) {
        field = org.apache.commons.lang.StringUtils.trim(field);
        org.apache.commons.lang.Validate.notEmpty(field);

        String[] strs = org.apache.commons.lang.StringUtils.split(field, " ");
        setField(strs[0]);

        if (strs.length > 1) {
            setAsc(!strs[1].startsWith(SORT_DESC));

        } else {
            setAsc(true);
        }
    }

    public ResultOrder(String field, boolean isAsc) {
        setField(field);
        setAsc(isAsc);
    }

    public static ResultOrder parse(String sortBy) {
        ResultOrder order = new ResultOrder();
        sortBy = StringUtils.trim(sortBy);
        Validate.notEmpty(sortBy);

        String[] strs = StringUtils.split(sortBy, " ");
        order.setField(strs[0]);

        if (strs.length > 1) {
            order.setAsc(!strs[1].startsWith(SORT_DESC));

        } else {
            order.setAsc(true);
        }

        return order;
    }

//
//    public ResultOrder(String sortBy) {
//        sortBy = StringUtils.trim(sortBy);
//        Validate.notEmpty(sortBy);
//
//        String[] strs = StringUtils.split(sortBy, " ");
//        setField(strs[0]);
//
//        if (strs.length > 1) {
//            setAsc(!strs[1].startsWith(SORT_DESC));
//
//        } else {
//            setAsc(true);
//        }
//    }

    public static String[] toStringOrders(List<ResultOrder> orders) {
        List<String> list = new ArrayList<>();
        for (ResultOrder item : orders) {
            list.add(item.toOrderString());
        }

        return list.toArray(new String[list.size()]);
    }

    public String toOrderString() {
        return field + " " + (asc ? SORT_ASC : SORT_DESC);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

}
