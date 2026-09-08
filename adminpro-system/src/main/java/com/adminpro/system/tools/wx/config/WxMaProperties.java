package com.adminpro.system.tools.wx.config;

import com.adminpro.system.rbac.common.RbacConstants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMaProperties {

    private List<Config> configs;

    /**
     * 小程序登录时创建账号所在的用户域，默认 {@code internet}。
     * <p>
     * 接入方如果为 C 端用户维护了独立用户域（例如工友端的 {@code WORKER}），
     * 通过 {@code wx.miniapp.user-domain} 覆盖即可，无需修改本模块代码。
     * 保持默认值可确保既有接入方行为不变。
     * </p>
     */
    private String userDomain = RbacConstants.INTERNET_DOMAIN;

    /**
     * 解析指定 appid 应使用的用户域：优先取该小程序的独立配置，
     * 未配置时回落到全局 {@link #userDomain}。
     *
     * @param appid 小程序 appid，可为空
     * @return 用户域，永不为空
     */
    public String resolveUserDomain(String appid) {
        if (StringUtils.isNotBlank(appid) && configs != null) {
            for (Config config : configs) {
                if (StringUtils.equals(config.getAppid(), appid)
                        && StringUtils.isNotBlank(config.getUserDomain())) {
                    return config.getUserDomain();
                }
            }
        }
        return StringUtils.defaultIfBlank(userDomain, RbacConstants.INTERNET_DOMAIN);
    }

    @Data
    public static class Config {
        /**
         * 设置微信小程序的appid
         */
        private String appid;

        /**
         * 设置微信小程序的Secret
         */
        private String secret;

        /**
         * 设置微信小程序消息服务器配置的token
         */
        private String token;

        /**
         * 设置微信小程序消息服务器配置的EncodingAESKey
         */
        private String aesKey;

        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;

        /**
         * 该小程序创建账号所在的用户域，留空时使用 {@link WxMaProperties#userDomain}。
         * 多个小程序接入不同业务线时可分别指定。
         */
        private String userDomain;
    }

}
