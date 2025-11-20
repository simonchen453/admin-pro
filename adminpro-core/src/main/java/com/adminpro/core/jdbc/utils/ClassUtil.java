package com.adminpro.core.jdbc.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.*;

public class ClassUtil {

    private static final Map<Class, Field[]> map = new HashMap<>();

    /**
     * 获取类中包含父类的所有属性，
     *
     * @param genericType
     * @param <E>
     * @return
     */
    public static <E> Field[] getAllFields(Class<E> genericType) {
        Field[] fields = map.get(genericType);
        if (!ArrayUtils.isEmpty(fields)) {
            return fields;
        } else {
            Class<? super E> tempClass = genericType;
            List<Field> fieldList = new ArrayList<>();
            while (tempClass != null) {
                fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
                tempClass = tempClass.getSuperclass();
            }

            fields = fieldList.toArray(new Field[fieldList.size()]);
            map.put(genericType, fields);

            return fields;
        }
    }
}
