package com.adminpro.core.base.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlParserUtil {
    private HtmlParserUtil() {
    }

    public static String getId(String html) {
        Document doc = Jsoup.parse(html);

        Elements elements = doc.getElementsByTag("a");
        for (int i = 0; i < elements.size(); i++) {
            Element ele = elements.get(i);
            String value = ele.attr("href");
            if (value.indexOf("id=") > 0) {
                return value.substring(value.indexOf("id=") + 3);
            }
        }
        return null;
    }

    public static List<String> getCheckIds(String html) {
        Document doc = Jsoup.parse(html);
        List<String> list = new ArrayList<String>();

        Elements elements = doc.getElementsByTag("a");
        for (int i = 0; i < elements.size(); i++) {
            Element ele = elements.get(i);
            String value = ele.attr("href");
            if (value.indexOf("id=") > 0) {
                list.add(value.substring(value.indexOf("id=") + 3));
            }
        }
        return list;
    }

    public static List<String> getIds(String html) {
        Document doc = Jsoup.parse(html);
        List<String> list = new ArrayList<String>();

        Elements elements = doc.getElementsByTag("a");
        for (int i = 0; i < elements.size(); i++) {
            Element ele = elements.get(i);
            String value = ele.attr("href");
            if (value.indexOf("id=") > 0 && value.indexOf("confirm") > 0) {
                list.add(value.substring(value.indexOf("id=") + 3, value.indexOf("id=") + 35));
            }
        }
        return list;
    }

    public static Map<String, String> getHiddenNodes(String html) {
        Document doc = Jsoup.parse(html);
        HashMap<String, String> map = new HashMap<String, String>();
        Elements elements = doc.getElementsByTag("input");
        for (int i = 0; i < elements.size(); i++) {
            Element ele = elements.get(i);
            String type = ele.attr("type");
            if (StringUtils.equals(type, "hidden")) {
                map.put(ele.attr("name"), ele.attr("value"));
            }
        }
        return map;
    }

    public static Map<String, String> getSelectTagDatas(String html) {
        Document doc = Jsoup.parse(html);
        HashMap<String, String> map = new HashMap<String, String>();
        Elements selects = doc.getElementsByTag("select");
        for (int i = 0; i < selects.size(); i++) {
            Element ele = selects.get(i);
            String name = ele.attr("name");
            String tagdata = ele.attr("tagdata");
            map.put(name, tagdata);
        }
        return map;
    }

    public static Map<String, String> getInputNodes(String html) {
        Document doc = Jsoup.parse(html);
        HashMap<String, String> map = new HashMap<String, String>();
        Elements elements = doc.getElementsByTag("input");
        for (int i = 0; i < elements.size(); i++) {
            Element ele = elements.get(i);
            String type = ele.attr("type");
            if (isValidInput(type)) {
                map.put(ele.attr("name"), ele.attr("value"));
            }
        }

        Elements textareas = doc.getElementsByTag("textarea");
        for (int i = 0; i < textareas.size(); i++) {
            Element ele = textareas.get(i);
            map.put(ele.attr("name"), ele.attr("value"));

        }

        Elements selects = doc.getElementsByTag("select");
        for (int i = 0; i < selects.size(); i++) {
            Element ele = selects.get(i);
            String name = ele.attr("name");
            List<Element> optList = ele.select("option");
            for (Element option : optList) {
                if (option.hasAttr("selected")) {
                    map.put(name, ele.attr(option.attr("value")));
                    break;
                }
            }
        }
        return map;
    }

    private static boolean isValidInput(String type) {
        if ("hidden".equals(type)) {
            return true;
        }
        if ("text".equals(type)) {
            return true;
        }
        return false;
    }
}
