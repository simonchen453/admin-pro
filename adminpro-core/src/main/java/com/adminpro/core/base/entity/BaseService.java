package com.adminpro.core.base.entity;

import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.jdbc.annotation.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 单表service公共父类，放一些公用的方法
 *
 * @author simon
 */
public abstract class BaseService<T extends BaseEntity, ID extends Serializable> implements IService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected BaseDao<T, ID> baseDao;
    private boolean hasTableAnnotation;

    protected BaseService(BaseDao<T, ID> dao) {
        this.baseDao = dao;
        Class<T> tClass = indicateEntityClass();
        Table tbl = tClass.getAnnotation(Table.class);
        if (tbl == null) {
            hasTableAnnotation = false;
        } else {
            hasTableAnnotation = true;
        }
    }

    protected Class<T> indicateEntityClass() {
        Type superclass = getClass().getGenericSuperclass();
        if (!(superclass.getTypeName().startsWith(BaseService.class.getTypeName() + "<"))) {
            throw new BaseRuntimeException("This class must extends [" + BaseService.class + "] directly.");
        }

        ParameterizedType pType = (ParameterizedType) superclass;
        Type[] argTypes = pType.getActualTypeArguments();
        Type entityType = argTypes[0];
        return (Class<T>) entityType;
    }

    @Transactional
    public void create(T entity) {
        baseDao.create(entity);
    }

    @Transactional
    public void update(T entity) {
        baseDao.update(entity);
    }

    @Transactional
    public void update(T entity, String... columns) {
        baseDao.update(entity, columns);
    }

    @Transactional
    public void delete(ID id) {
        baseDao.delete(id);
    }

    public boolean exists(ID id) {
        T t = baseDao.retrieve(id);
        return t != null;
    }

    public T findById(ID id) {
        if (id == null || (id instanceof String && id == "")) {
            return null;
        }

        if (!hasTableAnnotation) {
            return baseDao.findById(id);
        } else {
            return baseDao.retrieve(id);
        }
    }

    public T retrieve(ID id) {
        return baseDao.retrieve(id);
    }

    public List<T> findAll() {
        return baseDao.retrieveAll();
    }
}
