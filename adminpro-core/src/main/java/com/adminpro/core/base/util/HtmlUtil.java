package com.adminpro.core.base.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author simon
 * @date 2016/12/16
 */
public class HtmlUtil {
    private HtmlUtil() {
    }

    public static String delHTMLTag(String htmlStr) {
        if (StringUtils.isEmpty(htmlStr)) {
            return "";
        }
        //定义script的正则表达式
        String regExScript = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式
        String regExStyle = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式
        String regExHtml = "<[^>]+>";

        Pattern pScript = Pattern.compile(regExScript, Pattern.CASE_INSENSITIVE);
        Matcher mScript = pScript.matcher(htmlStr);
        //过滤script标签
        htmlStr = mScript.replaceAll("");

        Pattern pStyle = Pattern.compile(regExStyle, Pattern.CASE_INSENSITIVE);
        Matcher mStyle = pStyle.matcher(htmlStr);
        //过滤style标签
        htmlStr = mStyle.replaceAll("");

        Pattern pHtml = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
        Matcher mHtml = pHtml.matcher(htmlStr);
        //过滤html标签
        htmlStr = mHtml.replaceAll("");

        //返回文本字符串
        return htmlStr.trim();
    }
}
