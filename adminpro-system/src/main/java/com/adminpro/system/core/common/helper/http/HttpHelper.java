package com.adminpro.system.core.common.helper.http;

import jakarta.servlet.ServletRequest;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * HTTP请求辅助工具类
 * <p>
 * 本类提供HTTP请求处理相关的工具方法，主要用于读取请求体内容
 * <p>
 * 主要功能：
 * <ul>
 * <li>读取ServletRequest的请求体内容</li>
 * <li>自动处理字符编码（UTF-8）</li>
 * <li>自动管理资源关闭</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 * <li>读取POST请求的JSON数据</li>
 * <li>记录请求日志</li>
 * <li>请求参数验证</li>
 * </ul>
 * <p>
 * 注意：读取请求体后，请求体的流会被消耗，不能再次读取
 *
 * @author simon
 */
public class HttpHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class);

    /**
     * 获取请求体字符串内容
     * <p>
     * 从ServletRequest中读取请求体的内容并返回字符串。
     * 使用UTF-8编码读取，自动处理资源关闭
     * <p>
     * 使用场景：
     * <ul>
     * <li>读取POST请求的JSON/XML数据</li>
     * <li>记录完整的请求内容到日志</li>
     * <li>拦截器中获取请求数据</li>
     * </ul>
     * <p>
     * 注意：
     * <ul>
     * <li>此方法会消耗请求体的流，调用后不能再次读取</li>
     * <li>如果发生异常，会记录警告日志并返回空字符串</li>
     * </ul>
     *
     * @param request ServletRequest对象，不能为空
     * @return 请求体内容字符串，如果发生异常则返回空字符串
     */
    public static String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try (InputStream inputStream = request.getInputStream()) {
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LOGGER.warn("getBodyString出现问题！");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error(ExceptionUtils.getFullStackTrace(e));
                }
            }
        }
        return sb.toString();
    }
}
