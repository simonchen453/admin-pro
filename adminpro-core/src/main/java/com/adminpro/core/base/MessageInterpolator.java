package com.adminpro.core.base;

import com.adminpro.core.base.util.CommonUtil;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 更改默认的消息读取来源,并且从给定的Locale进行国际化。
 */
public class MessageInterpolator implements jakarta.validation.MessageInterpolator {
    private final jakarta.validation.MessageInterpolator defaultInterpolator;
    private final java.util.List<String> bundleNames;

    public MessageInterpolator() {
        this.defaultInterpolator = jakarta.validation.Validation.byDefaultProvider()
                .configure()
                .getDefaultMessageInterpolator();
        this.bundleNames = Arrays.asList("i18n_message");
    }

    @Override
    public String interpolate(String message, Context context) {
        return interpolate(message, context, CommonUtil.getLocale());
    }

    @Override
    public String interpolate(String message, Context context, Locale locale) {
        // 首先尝试从自定义资源包获取消息
        for (String bundleName : bundleNames) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
                if (bundle.containsKey(message)) {
                    String customMessage = bundle.getString(message);
                    if (customMessage != null && !customMessage.isEmpty()) {
                        return customMessage;
                    }
                }
            } catch (Exception e) {
                // Ignore missing bundles
            }
        }
        // 如果找不到自定义消息，使用默认的插值器
        return defaultInterpolator.interpolate(message, context, locale);
    }
}
