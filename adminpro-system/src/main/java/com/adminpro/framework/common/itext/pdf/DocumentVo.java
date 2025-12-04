package com.adminpro.framework.common.itext.pdf;

import java.util.Map;

public interface DocumentVo {
    public String findPrimaryKey();

    public Map<String, Object> fillDataMap();
}
