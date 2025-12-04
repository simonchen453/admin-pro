package com.adminpro.core.jdbc.query;

import com.adminpro.core.base.util.SpringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryResultSet<S> {
    private int currentPage;

    private int pageSize;

    private long totalCount;

    private int totalPage;

    private List<ResultOrder> orders;

    private List<S> records;

    public QueryResultSet() {
    }

    public <T> QueryResultSet<T> map(Class<? extends IModelConverter<S, T>> clazz) {
        QueryResultSet<T> rs = new QueryResultSet<>();
        rs.setPageSize(getPageSize());
        rs.setTotalPage(getTotalPage());
        rs.setCurrentPage(getCurrentPage());
        rs.setOrders(getOrders());
        rs.setTotalCount(getTotalCount());
        List<T> records = new ArrayList<T>(getRecords().size());
        IModelConverter<S, T> converter = SpringUtil.getBean(clazz);
        for (S item : getRecords()) {
            records.add(converter.convert(item));
        }

        rs.setRecords(records);
        return rs;
    }


    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<S> getRecords() {
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }

        return records;
    }

    public void setRecords(List<S> records) {
        this.records = records;
    }

    @JsonIgnore
    public ResultOrder getFirstOrder() {
        if (CollectionUtils.isEmpty(getOrders())) {
            return null;
        }

        return orders.get(0);
    }

    public List<ResultOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<ResultOrder> orders) {
        this.orders = orders;
    }

    private String[] regularOrders(String[] orders) {
        if (orders == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        List<String> list = new ArrayList<>();
        for (String order : orders) {
            order = StringUtils.trim(order);
            if (StringUtils.isNotEmpty(order)) {
                list.add(order);
            }
        }

        return list.toArray(new String[list.size()]);
    }

}
