package com.adminpro.core.jdbc.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SetUtil {
    private SetUtil() {
    }

    public static boolean isEmpty(Object[] array) throws Exception {
        return array == null || array.length <= 0;
    }

    public static boolean isEmpty(List<?> list) throws Exception {
        return list == null || list.size() <= 0;
    }

    public static boolean isEmpty(Map<?, ?> map) throws Exception {
        return map == null || map.size() <= 0;
    }

    public static boolean isEmpty(Set<?> set) throws Exception {
        return set == null || set.size() <= 0;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(T... array) throws Exception {
        if (isEmpty(array)) {
            return null;
        }

        List<T> list = new ArrayList<T>();
        for (T t : array) {
            if (t != null) {
                list.add(t);
            }
        }
        if (isEmpty(list)) {
            return null;
        }

        return list.toArray((T[]) Array.newInstance(list.get(0).getClass(), list.size()));
    }

    @SafeVarargs
    public static <T> List<T> combine(List<T>... lists) throws Exception {
        if (isEmpty(lists)) {
            return null;
        }

        List<T> all = new ArrayList<T>();
        for (List<T> list : lists) {
            if (isEmpty(list)) {
                continue;
            }
            all.addAll(list);
        }
        return isEmpty(all) ? null : all;
    }
}
