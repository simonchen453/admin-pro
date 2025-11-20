package com.adminpro.core.base.util;

import com.adminpro.core.exceptions.BaseRuntimeException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author simon
 */
public final class ReflectUtil {
    private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    public static Object newInstance(String className) {
        return newInstance(className, false);
    }

    public static Object newInstance(String className, boolean returnNull) {
        Object instance = null;
        if (StringUtils.isEmpty(className)) {
            if (!returnNull) {
                throw new IllegalArgumentException("newInstance: className cannot be empty!");
            } else {
                return instance;
            }
        } else {
            try {
                Class<?> clazz = Class.forName(className);
                instance = newInstance(clazz);
            } catch (Throwable var5) {
                RuntimeException ex = new RuntimeException("newInstance for " + className + " failed!", var5);
                if (!returnNull) {
                    throw ex;
                }
            }

            return instance;
        }
    }

    public static Object newInstance(String className, Class<?>[] paramTypes, Object[] paramValues) {
        return newInstance(className, paramTypes, paramValues, false);
    }

    public static Object newInstance(String className, Class<?>[] paramTypes, Object[] paramValues, boolean returnNull) {
        Object instance = null;
        if (StringUtils.isEmpty(className)) {
            if (!returnNull) {
                throw new IllegalArgumentException("newInstance: className cannot be empty!");
            } else {
                return instance;
            }
        } else {
            try {
                Class<?> clazz = Class.forName(className);
                instance = newInstance(clazz, paramTypes, paramValues);
            } catch (Throwable var7) {
                RuntimeException ex = new RuntimeException("newInstance for " + className + " failed!", var7);
                if (!returnNull) {
                    throw ex;
                }
            }

            return instance;
        }
    }

    public static Object newInstance(Class<?> clazz) {
        return newInstance(clazz, false);
    }

    public static Object newInstance(Class<?> clazz, boolean returnNull) {
        Object instance = null;
        if (clazz == null) {
            if (!returnNull) {
                throw new IllegalArgumentException("newInstance: clazz cannot be null!");
            } else {
                return instance;
            }
        } else {
            try {
                instance = clazz.getDeclaredConstructor().newInstance();
            } catch (Throwable var5) {
                RuntimeException ex = new RuntimeException("newInstance for " + clazz.getName() + " failed!", var5);
                if (!returnNull) {
                    throw ex;
                }
            }

            return instance;
        }
    }

    public static Object newInstance(Class<?> clazz, Class<?>[] paramTypes, Object[] paramValues) {
        return newInstance(clazz, paramTypes, paramValues, false);
    }

    public static Object newInstance(Class<?> clazz, Class<?>[] paramTypes, Object[] paramValues, boolean returnNull) {
        Object instance = null;
        if (clazz == null) {
            if (!returnNull) {
                throw new IllegalArgumentException("newInstance: clazz cannot be null!");
            } else {
                return instance;
            }
        } else {
            try {
                Constructor<?> ctor = clazz.getDeclaredConstructor(paramTypes);
                instance = ctor.newInstance(paramValues);
            } catch (Throwable var7) {
                RuntimeException ex = new RuntimeException("newInstance for " + clazz.getName() + " failed!", var7);
                if (!returnNull) {
                    throw ex;
                }
            }

            return instance;
        }
    }

    /**
     * get the field type for the given field names with dot. e.g. contactDetails.address.streetName
     *
     * @param clazz
     * @param fieldNamePath
     * @return
     */
    public static Class<?> getFieldType(Class<?> clazz, String fieldNamePath) {
        int idx = fieldNamePath.indexOf('.');
        try {
            if (idx == -1) {
                return clazz.getDeclaredField(fieldNamePath).getType();
            } else {
                String fieldName = fieldNamePath.substring(0, idx);
                Class<?> fieldType = clazz.getDeclaredField(fieldName).getType();
                return getFieldType(fieldType, fieldNamePath.substring(idx + 1));
            }
        } catch (Exception x) {
            logger.error("The field [{}] is undefined in class [{}].", fieldNamePath, clazz.getName());
            return null;
        }
    }

    /**
     * get all field of class including parent class
     *
     * @param type
     * @return
     */
    public static Field[] getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * read the field value from an object, e.g. contactDetails.address.streetName
     *
     * @param target
     * @param fieldNamePath
     * @return
     */
    public static Object getFieldValue(Object target, String fieldNamePath) {
        if (target == null) {
            return null;
        }

        try {
            return BeanUtils.getProperty(target, fieldNamePath);
        } catch (Exception ex) {
            throw new BaseRuntimeException("Failed to get the field value.", ex);
        }
    }

    /**
     * write the field value to an object. e.g. contactDetails.address.streetName
     *
     * @param target
     * @param fieldNamePath
     * @param value
     */
    public static void setFieldValue(Object target, String fieldNamePath, Object value) {
        if (target == null) {
            return;
        }

        try {
            BeanUtils.setProperty(target, fieldNamePath, value);
        } catch (Exception ex) {
            throw new BaseRuntimeException("Failed to set the field value.", ex);
        }
    }

    /**
     * invoke the constructor of a class
     *
     * @param className
     * @param args
     * @return
     */
    public static Object invokeConstructor(String className, Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            return ConstructorUtils.invokeConstructor(clazz, args);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ex) {
            throw new BaseRuntimeException("Error occurred on invoke class constructor.", ex);
        }
    }

    public static Object invokeMethod(Object obj, String methodName, Object... args) {
        Class<?>[] argTypes = {};
        if (args != null && args.length > 0) {
            argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
        }

        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, argTypes);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(obj, args);
            }
        } catch (Exception x) {
            throw new BaseRuntimeException("Error occurred on invoke method.", x);
        }
        return null;
    }

    private ReflectUtil() {
    }
}
