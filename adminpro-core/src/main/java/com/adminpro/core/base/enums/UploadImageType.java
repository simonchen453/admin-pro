package com.adminpro.core.base.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 上传图片类型： 原图 还是 缩略图
 *
 * @author simon
 * @date 2019/2/26
 */
public enum UploadImageType {
    /**
     * 原图
     */
    ORIGINAL("original", "原图"),
    /**
     * 缩略图
     */
    THUMBNAIL("thumbnail", "缩略图");

    private String code;
    private String desc;

    UploadImageType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (UploadImageType type : EnumSet.allOf(UploadImageType.class)) {
            if (StringUtils.equals(type.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (UploadImageType status : EnumSet.allOf(UploadImageType.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (UploadImageType status : EnumSet.allOf(UploadImageType.class)) {
            map.put(status.getCode(), status.getDesc());
        }
        return map;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
