package com.adminpro.system.rbac.api;

import com.adminpro.framework.base.entity.IVO;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.exceptions.BaseRuntimeException;
import com.adminpro.framework.jdbc.query.IModelConverter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;


/**
 * 视图对象（VO）辅助工具类
 * <p>
 * 本类提供VO对象相关的转换和处理工具方法，包括：
 * <ul>
 * <li>数据库实体对象到VO对象的转换</li>
 * <li>VO对象属性的规范化处理</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 * <li>Controller层返回数据时将实体转换为VO</li>
 * <li>接收前端表单数据时规范化字符串属性</li>
 * <li>数据传输对象（DTO）与VO之间的转换</li>
 * </ul>
 * <p>
 * 注意：所有方法都是静态方法，可以直接通过类名调用
 */
public class VoHelper {
    /**
     * 转换数据库对象为页面对象
     * <p>
     * 通过Spring容器获取指定类型的转换器（IModelConverter），
     * 将源对象（通常是数据库实体）转换为目标VO对象
     * <p>
     * 使用场景：
     * <ul>
     * <li>Service层返回数据到Controller时转换为VO</li>
     * <li>数据库实体到前端展示对象的转换</li>
     * <li>不同层之间的数据对象转换</li>
     * </ul>
     *
     * @param <S>             源对象类型，通常是数据库实体类型
     * @param <T>             目标VO对象类型，必须继承IVO接口
     * @param s               源对象实例，不能为空
     * @param converterClass  转换器类型，必须是IModelConverter的实现类，不能为空
     * @return 转换后的VO对象实例
     * @throws BaseRuntimeException 如果转换过程中发生异常
     */
    public static <S, T extends IVO> T convert(S s, Class<? extends IModelConverter<S, T>> converterClass) {
        IModelConverter converter = SpringUtil.getBean(converterClass);
        return (T) converter.convert(s);
    }


    /**
     * 规范页面对象中的字符串属性
     * <p>
     * 遍历IVO对象的所有属性，对String类型的属性执行以下处理：
     * <ul>
     * <li>去除字符串两边的空格（trim）</li>
     * <li>将空字符串转换为null</li>
     * </ul>
     * <p>
     * 使用场景：
     * <ul>
     * <li>接收前端表单提交数据后的预处理</li>
     * <li>防止前端传递空白字符串导致的数据问题</li>
     * <li>统一处理null和空字符串的语义</li>
     * </ul>
     *
     * @param obj 需要规范化的IVO对象，不能为空
     * @throws BaseRuntimeException 如果获取或设置属性时发生异常
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
