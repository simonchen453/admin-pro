package com.adminpro.web;

import com.adminpro.framework.common.helper.ConfigHelper;

/**
 * @author simon
 */
public class BaseConstants {

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    public static final String DEFAULT_ENCODING = UTF8;

    public static final String DEFAULT_LOGIN_URL_KEY = "login.url";
    public static final String PLATFORM_NAME_KEY = "app.platform.name";
    public static final String PLATFORM_SHORT_NAME_KEY = "app.platform.short.name";
    public static final String SERVER_IP_KEY = "app.server.ip";
    public static final String SERVER_PORT_KEY = "app.server.port";
    public static final String SERVER_BUILD_VERSION_KEY = "app.version.build";
    public static final String SERVER_RELEASE_VERSION_KEY = "app.version.release";

    public static final String HEADER_CACHE_ENABLE_KEY = "app.header.cache.enable";

    public static final String THIRD_PARTY_ENCRYPT_PWD_ENABLE_KEY = "app.third.party.pwd.encrypt.enable";
    public static final String THIRD_PARTY_ENCRYPT_PWD_KEY = "app.third.party.pwd.encrypt";

    public static final String ADDRESS_ENABLED_KEY = "app.address.enabled";

    public static final String URL_SESSION_DOES_NOT_EXIST_KEY = "app.session.does.not.exist";

    public static final String SESSION_FILTER_WHITE_LIST_KEY = "app.session.filter.white.list";
    public static final String COPY_RIGHT_KEY = "app.copy.right";
    public static final String APP_UEDITOR_FILE_STORE_TYPE = "app.ueditor.file.store.type";
    public static final String APP_UEDITOR_FILE_STORE_THUMBNAIL = "app.ueditor.file.store.thumbnail";

    public static final String LOCK_MSG = ConfigHelper.getString("app.lock.block.message", "系统繁忙，请稍后再试");
}
