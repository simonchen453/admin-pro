package com.adminpro.framework.common.helper.ip;

import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.http.HttpUtils;
import com.adminpro.web.BaseConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取地址类
 *
 * @author simon
 */
public class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // IP地址查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip) {
        String address = UNKNOWN;
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            return "内网IP";
        }
        boolean showAddress = ConfigHelper.getBoolean(BaseConstants.ADDRESS_ENABLED_KEY, false);
        if (showAddress) {
            try {
                String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", BaseConstants.GBK);
                if (StringHelper.isEmpty(rspStr)) {
                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                JsonObject obj = JsonParser.parseString(rspStr).getAsJsonObject();
                String region = obj.has("pro") ? obj.get("pro").getAsString() : "";
                String city = obj.has("city") ? obj.get("city").getAsString() : "";
                return String.format("%s %s", region, city);
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return address;
    }
}
