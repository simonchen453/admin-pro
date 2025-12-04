package com.adminpro.core.base.entity;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 定义一些ID生成需要的常量
 */
public class BaseEntity implements IEntity {
    private static final long serialVersionUID = 5419738720360145404L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
