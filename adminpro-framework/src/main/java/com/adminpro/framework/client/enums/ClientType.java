package com.adminpro.framework.client.enums;

public enum ClientType {
    WEB("WEB", "Web浏览器"),
    ANDROID("ANDROID", "Android应用"),
    IOS("IOS", "iOS应用"),
    WECHAT_MINI_PROGRAM("WECHAT_MINI_PROGRAM", "微信小程序"),
    ALIPAY_MINI_PROGRAM("ALIPAY_MINI_PROGRAM", "支付宝小程序"),
    UNKNOWN("UNKNOWN", "未知客户端");

    private final String code;
    private final String desc;

    ClientType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ClientType fromCode(String code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (ClientType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public boolean isMobileApp() {
        return this == ANDROID || this == IOS;
    }

    public boolean isMiniProgram() {
        return this == WECHAT_MINI_PROGRAM || this == ALIPAY_MINI_PROGRAM;
    }

    public boolean isMobile() {
        return isMobileApp() || isMiniProgram();
    }
}

