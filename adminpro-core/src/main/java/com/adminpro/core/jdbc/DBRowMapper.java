package com.adminpro.core.jdbc;

import com.adminpro.core.base.entity.BaseAuditEntity;
import com.adminpro.core.base.util.ReflectUtil;
import com.adminpro.core.base.util.StringUtil;
import com.adminpro.core.jdbc.utils.ClassUtil;
import com.adminpro.core.jdbc.annotation.Column;

import org.springframework.jdbc.core.RowMapper;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author simon
 */
public class DBRowMapper<E> implements RowMapper<E> {
    private Class<E> genericType;

    public DBRowMapper(Class<E> genericType) {
        this.genericType = genericType;
    }

    public DBRowMapper() {
        genericType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<E> getGenericType() {
        return genericType;
    }

    @Override
    public E mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (genericType == null) {
            return null;
        }

        E entity = null;
        try {
            entity = genericType.newInstance();
            Field[] fields = ClassUtil.getAllFields(genericType);
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Column.class)) {
                        field.setAccessible(true);
                        Column col = field.getAnnotation(Column.class);
                        if (col != null) {
                            String colField = col.field();
                            Object obj = null;
                            if (StringUtil.isNotBlank(colField)) {
                                obj = field.getType().newInstance();
                            }
                            Column.Type type = col.type();
                            if (Column.Type.DATE == type) {
                                field.set(entity, rs.getDate(col.name()));
                            } else if (Column.Type.TIME == type) {
                                field.set(entity, rs.getTime(col.name()));
                            } else if (Column.Type.DATETIME == type) {
                                field.set(entity, rs.getTimestamp(col.name()));
                            } else if (Column.Type.INT == type) {
                                int val = rs.getInt(col.name());
                                if (obj != null) {
                                    ReflectUtil.setFieldValue(obj, colField, val);
                                    field.set(entity, obj);
                                } else {
                                    field.set(entity, val);
                                }
                            } else if (Column.Type.LONG == type) {
                                field.set(entity, rs.getLong(col.name()));
                            } else if (Column.Type.DOUBLE == type) {
                                field.set(entity, rs.getDouble(col.name()));
                            } else if (Column.Type.FLOAT == type) {
                                field.set(entity, rs.getFloat(col.name()));
                            } else if (Column.Type.BLOB == type) {
                                field.set(entity, rs.getBytes(col.name()));
                            } else if (Column.Type.BOOLEAN == type) {
                                field.set(entity, rs.getBoolean(col.name()));
                            } else if (Column.Type.BIGDECIMAL == type) {
                                field.set(entity, rs.getBigDecimal(col.name()));
                            } else {
                                String val = rs.getString(col.name());
                                if (obj != null) {
                                    ReflectUtil.setFieldValue(obj, colField, val);
                                    field.set(entity, obj);
                                } else {
                                    field.set(entity, val);
                                }
                            }
                        }
                    }
                }
            }
            if (entity != null && rs != null && entity instanceof BaseAuditEntity) {
                BaseAuditEntity ae = (BaseAuditEntity) entity;
                ae.setCreatedByUserId(rs.getString(BaseAuditEntity.COL_CREATED_BY_USER_ID));
                ae.setCreatedByUserDomain(rs.getString(BaseAuditEntity.COL_CREATED_BY_USER_DOMAIN));
                ae.setCreatedDate(rs.getTimestamp(BaseAuditEntity.COL_CREATED_DATE));
                ae.setUpdatedByUserId(rs.getString(BaseAuditEntity.COL_UPDATED_BY_USER_ID));
                ae.setUpdatedByUserDomain(rs.getString(BaseAuditEntity.COL_UPDATED_BY_USER_DOMAIN));
                ae.setUpdatedDate(rs.getTimestamp(BaseAuditEntity.COL_UPDATED_DATE));
            }
        } catch (Exception x) {
            throw new SQLException(String.format("Failed to create map row for type [%s].", entity.getClass()), x);
        }
        return entity;
    }
}
