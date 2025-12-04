package com.adminpro.framework.batchjob;

import java.util.*;

/**
 * @author simon
 */

public enum ScheduleStatus {
    /**
     * 正常
     */
    NORMAL(0, "正常"),
    /**
     * 暂停
     */
    PAUSE(1, "暂停");

    private Integer value;
    private String display;

    private ScheduleStatus(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    public int getValue() {
        return value;
    }

    public String getDisplay() {
        return display;
    }

    public static String getDisplay(int value) {
        for (ScheduleStatus status : EnumSet.allOf(ScheduleStatus.class)) {
            if (status.getValue() == value) {
                return status.getDisplay();
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getConfigList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ScheduleStatus status : EnumSet.allOf(ScheduleStatus.class)) {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put("key", status.getValue());
            tmp.put("value", status.getDisplay());
            list.add(tmp);
        }
        return list;
    }

    public static Map<Integer, String> getConfigMap() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (ScheduleStatus status : EnumSet.allOf(ScheduleStatus.class)) {
            map.put(status.getValue(), status.getDisplay());
        }
        return map;
    }
}
