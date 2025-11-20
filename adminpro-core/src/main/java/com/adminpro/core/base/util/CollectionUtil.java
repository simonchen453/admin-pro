package com.adminpro.core.base.util;

import com.adminpro.core.exceptions.BaseRuntimeException;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * @author simon
 */
public final class CollectionUtil {
    private static final int CAPACITY_SMALL = 20;
    private static final int CAPACITY_MEDIUM = 50;
    private static final int CAPACITY_LARGE = 100;

    public static List getSmallList() {
        return getList(CAPACITY_SMALL);
    }

    public static List getMediumList() {
        return getList(CAPACITY_MEDIUM);
    }

    public static List getLargeList() {
        return getList(CAPACITY_LARGE);
    }

    public static List getList(int capacity) {
        return new ArrayList<>(capacity);
    }

    public static Map getSmallMap() {
        return getMap(CAPACITY_SMALL);
    }

    public static Map getMediumMap() {
        return getMap(CAPACITY_MEDIUM);
    }

    public static Map getLargeMap() {
        return getMap(CAPACITY_LARGE);
    }

    public static Map getMap(int capacity) {
        return new HashMap<>(capacity);
    }

    public static boolean isNotEmpty(int[] ints) {
        return !isEmpty(ints);
    }

    public static boolean isNotEmpty(long[] longs) {
        return !isEmpty(longs);
    }

    public static boolean isNotEmpty(Object[] objs) {
        return !isEmpty(objs);
    }

    public static boolean isNotEmpty(List list) {
        return !isEmpty(list);
    }

    public static boolean isEmpty(int[] ints) {
        return ints == null || ints.length == 0;
    }

    public static boolean isEmpty(long[] longs) {
        return longs == null || longs.length == 0;
    }

    public static boolean isEmpty(Object[] objs) {
        return objs == null || objs.length == 0 || objs[0] == null || StringUtil.isBlank(objs[0].toString());
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean exists(List<String> list, String str) {
        if (list != null && !list.isEmpty()) {
            for (String item : list) {
                if (item.equals(str)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean exists(String[] array, String str) {
        if (array != null && array.length > 0) {
            for (String item : array) {
                if (item.equals(str)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int[] toIntArray(List<Integer> list) {
        if (isEmpty(list)) {
            return null;
        }

        int[] ints = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ints[i] = list.get(i);
        }
        return ints;
    }

    public static long[] toLongArray(List<Long> list) {
        if (isEmpty(list)) {
            return new long[0];
        }

        long[] longs = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            longs[i] = list.get(i);
        }
        return longs;
    }

    public static <T> T[] toArray(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }

        return list.toArray((T[]) java.lang.reflect.Array.newInstance(list.get(0).getClass(), list.size()));
    }

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        String[] objects = toArray(list);
        System.out.println(objects.length);
    }

    /**
     * -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
     * comp(1234, null) == -1
     * comp(null, null) == 0
     * comp(null, 1234) == 1
     *
     * @param list
     * @param sortField
     * @param sortType  'asc' / 'desc'
     */
    public static void sortItems(List list, String sortField, String sortType) {
        if (list == null || StringUtil.isBlank(sortField)) {
            return;
        }

        list.sort((lo, ro) -> {
            Object lObj = ReflectUtil.getFieldValue(lo, sortField);
            Object rObj = ReflectUtil.getFieldValue(ro, sortField);
            int ascResult = 0;
            if (lObj != null && rObj != null) {
                if (lObj instanceof Integer) {
                    Integer lv = (Integer) lObj;
                    Integer rv = (Integer) rObj;
                    if (lv.intValue() > rv.intValue()) {
                        ascResult = 1;
                    } else if (lv.intValue() < rv.intValue()) {
                        ascResult = -1;
                    }
                } else if (lObj instanceof Double) {
                    Double lv = (Double) lObj;
                    Double rv = (Double) rObj;
                    if (lv.doubleValue() > rv.doubleValue()) {
                        ascResult = 1;
                    } else if (lv.doubleValue() < rv.doubleValue()) {
                        ascResult = -1;
                    }
                } else if (lObj instanceof String) {
                    String lv = (String) lObj;
                    String rv = (String) rObj;
                    ascResult = lv.compareTo(rv);
                } else if (lObj instanceof Date) {
                    Date lv = (Date) lObj;
                    Date rv = (Date) rObj;
                    ascResult = lv.compareTo(rv);
                } else {
                    throw new BaseRuntimeException("Type is not supported.");
                }
            } else if (lObj != null && rObj == null) {
                ascResult = -1;
            } else if (lObj == null && rObj != null) {
                ascResult = 1;
            }

            boolean isAsc = true;
            if ("desc".equals(sortType)) {
                isAsc = false;
            }
            if (!isAsc) {
                ascResult = 0 - ascResult;
            }
            return ascResult;
        });
    }

    public static <T> T[] subArray(T[] array, int pageSize, int pageNo) {
        if (array == null || array.length <= 0) {
            return null;
        }

        if (pageNo > 0 && pageSize > 0) {
            int from = (pageNo - 1) * pageSize;
            int to = pageNo * pageSize;
            int size = array.length;
            if (to > size) {
                to = size;
            }
            return ArrayUtils.subarray(array, from, to);
        }

        return array;
    }

    public static <T> List<T> subList(List<T> list, int pageSize, int pageNo) {
        if (list == null) {
            return null;
        }

        if (pageNo > 0 && pageSize > 0) {
            int from = (pageNo - 1) * pageSize;
            int to = pageNo * pageSize;
            int size = list.size();
            if (to > size) {
                to = size;
            }
            return list.subList(from, to);
        }

        return list;
    }

    private CollectionUtil() {
    }
}
