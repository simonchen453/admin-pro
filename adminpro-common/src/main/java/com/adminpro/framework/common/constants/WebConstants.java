package com.adminpro.framework.common.constants;

import com.adminpro.core.base.IConstants;
import com.adminpro.framework.common.helper.ConfigHelper;

/**
 * @author simon
 */
public class WebConstants extends IConstants {
    //服务器地址
    public static final String SERVER_ADDRESS = ConfigHelper.getString("app.server.address");

    public static final String ENCODING = "UTF8";
    public static final String DOMAIN_SYSTEM = "system";

    public static final String SYSTEM_ADMIN = "admin";

    public static final int DEFAULT_PAGE_SIZE = 10;

    public static final int[] PAGE_SIZES = {10, 20, 30, 50};

    public static final String PARAM_REDIRECT_URL = "rdctUrl";
    public static final String PARAM_ACTION = "action";

    public static final String PARAM_ACTION_VALUE = "actionValue";
    public static final String PARAM_PAGE_SIZE = "pageSize";
    public static final String PARAM_PAGE_NO = "pageNo";

    public static final String PARAM_SORTING = "sorting";

    public static final String ATTR_MESSAGE_BUNDLE = "bundle";

    public static final String COOKIE_CUR_MENU = "app-menu";
    public static final String INVALID_AUTH_TOKEN_EXCEPTION = "Invalid Auth Token";
    public static final String INVALID_USER_SESSION_EXCEPTION = "用户登录超时";

    public static final String NOT_ACCEPTABLE_EXCEPTION = "Access Denied";

    public static final String BASE_PROCESS_CLASS_PARAM = "szyh.web.bpc";
    public static final String USER_PENDING_ACTIVE = "该账户等待激活";
    public static final String USER_LOCKED = "该账户锁定";
    public static final String USER_HAS_NO_PRIVILEGE = "您没有权限登陆";

    public static final String USER_NOT_MATCHED = "用户密码不匹配";
    public static final String CODE_REGISTER = "register";
    public static final String CODE_RESETPWD = "resetpwd";
    //我的-设置-忘记支付密码-验证码类型
    public static final String CODE_RESET_PAY_PWD = "resetpaypwd";

    //我的-个人信息-更换手机号
    public static final String CODE_CHANGE_MOBILE = "changemobile";
    public static final String[] CODE_TYPES = {CODE_REGISTER, CODE_RESETPWD, CODE_RESET_PAY_PWD, CODE_CHANGE_MOBILE};
    public static final String UPLOAD_AVATAR_PATH = ConfigHelper.getString("app.user.avatar.dir", "/avatar/");
}
