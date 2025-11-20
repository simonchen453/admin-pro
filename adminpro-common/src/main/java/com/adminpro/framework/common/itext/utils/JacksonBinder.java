package com.adminpro.framework.common.itext.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class JacksonBinder {

    private static Logger logger = LoggerFactory.getLogger(JacksonBinder.class);

    private ObjectMapper mapper;

    public JacksonBinder(JsonInclude.Include include) {
        this.mapper = new ObjectMapper();
        // 设置输出包含的属性
        this.mapper.setSerializationInclusion(include);
        // 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 创建输出全部属性到Json字符串的Binder.
     */
    public static JacksonBinder buildNormalBinder() {
        return new JacksonBinder(JsonInclude.Include.ALWAYS);
    }

    /**
     * 创建只输出非空属性到Json字符串的Binder.
     */
    public static JacksonBinder buildNonNullBinder() {
        return new JacksonBinder(JsonInclude.Include.NON_NULL);
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Binder.
     */
    public static JacksonBinder buildNonDefaultBinder() {
        return new JacksonBinder(JsonInclude.Include.NON_DEFAULT);
    }

    /**
     * 如果JSON字符串为Null或"null"字符串,返回Null. 如果JSON字符串为"[]",返回空集合.
     * <p>
     * 如需读取集合如List/Map,且不是List<String>这种简单类型时使用如下语句: List<MyBean> beanList =
     * binder.getMapper().readValue(listString, new
     * com.fasterxml.jackson.core.type.TypeReference<List<MyBean>>() {});
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (ObjectUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            return this.mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            logger.error("parse json string error:" + jsonString, e);
            return null;
        }
    }


    /**
     * 源数据对象 转换 成目标对象
     *
     * @param fromValue   源数据对象
     * @param toValueType 目标对象类型
     */
    public <T> T convertValue(Object fromValue, Class<T> toValueType) {
        if (ObjectUtils.isEmpty(fromValue)) {
            return null;
        }
        return this.mapper.convertValue(fromValue, toValueType);
    }

    public <T> T fromJsonFile(File file, Class<T> clazz) {
        if (ObjectUtils.isEmpty(file)) {
            return null;
        }

        try {
            return this.mapper.readValue(file, clazz);
        } catch (IOException e) {
            logger.error("parse json string error:" + file, e);
            return null;
        }
    }

    /**
     * 如果对象为Null,返回"null". 如果集合为空集合,返回"[]".
     */
    public String toJson(Object object) {

        try {
            return this.mapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("write to json string error:" + object, e);
            return null;
        }
    }

    /**
     * 设置转换日期类型的format pattern,如果不设置默认打印Timestamp毫秒数.
     */
    public void setDateFormat(String pattern) {
        if (ObjectUtils.isNotBlank(pattern)) {
            DateFormat df = new SimpleDateFormat(pattern);
            this.mapper.setDateFormat(df);
        }
    }

    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    public ObjectMapper getMapper() {
        return this.mapper;
    }
}
