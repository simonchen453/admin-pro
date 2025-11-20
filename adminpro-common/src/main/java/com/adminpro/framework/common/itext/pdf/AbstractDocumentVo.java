package com.adminpro.framework.common.itext.pdf;

import com.adminpro.framework.common.itext.utils.JacksonBinder;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板中需要的数据视图 抽象类
 *
 * @author simon
 */
public abstract class AbstractDocumentVo implements DocumentVo {
    @Override
    public Map<String, Object> fillDataMap() {
        return JacksonBinder.buildNonDefaultBinder().convertValue(this.getDocumentVo(), HashMap.class);
    }

    private DocumentVo getDocumentVo() {
        return this;
    }

}
