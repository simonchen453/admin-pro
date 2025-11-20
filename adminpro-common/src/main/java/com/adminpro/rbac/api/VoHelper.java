package com.adminpro.rbac.api;

import com.adminpro.core.base.entity.IVO;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.jdbc.query.IModelConverter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;


public class VoHelper {
    /**
     * 转换数据库对象为页面对象
     */
    public static <S, T extends IVO> T convert(S s, Class<? extends IModelConverter<S, T>> converterClass) {
        IModelConverter converter = SpringUtil.getBean(converterClass);
        return (T) converter.convert(s);
    }


    /**
     * 规范网页上对象里的String，去除字符串两边空格，把空字符串转换为null
     */
    public static void regulateProperties(IVO obj) {
        Map<String, Object> describe = null;
        try {
            describe = PropertyUtils.describe(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, Object> entry : describe.entrySet()) {
            String value = null;
            if (entry.getValue() instanceof String) {
                value = (String) entry.getValue();
            } else {
                continue;
            }

            if (null != value) {
                try {
                    PropertyUtils.setProperty(obj, entry.getKey(), StringUtils.trimToNull(value));
                } catch (Exception e) {
                    throw new BaseRuntimeException(e);
                }
            }
        }
    }
}
