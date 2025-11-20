package com.adminpro.core.base.entity;

import com.adminpro.core.jdbc.SqlExecutor;
import com.adminpro.core.jdbc.SqlSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * DAO基础类
 *
 * @author simon
 */
public abstract class BaseDao<T extends IEntity, ID extends Serializable> extends SqlExecutor<T, ID> implements SqlSymbol {
    protected transient Logger logger = LoggerFactory.getLogger(getClass());

    public void update(T entity) {
        super.update(entity);
    }

    public void update(T entity, String... columns) {
        super.update(entity, columns);
    }

    public T findById(ID id) {
        return super.retrieve(id);
    }

    public List<T> findAll() {
        return super.retrieveAll();
    }
}
