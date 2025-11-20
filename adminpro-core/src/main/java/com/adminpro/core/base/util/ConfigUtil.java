package com.adminpro.core.base.util;

import com.adminpro.core.exceptions.BaseRuntimeException;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * 配置的帮助类
 *
 * @author simon
 */
public class ConfigUtil {
    private ConfigUtil() {
    }

    private static Configuration config = initConfiguration();

    private static Configuration initConfiguration() {
        CompositeConfiguration cc = new CompositeConfiguration();
        try {
            cc.addConfiguration(new PropertiesConfiguration("app-system.properties"));
        } catch (ConfigurationException e) {
            throw new BaseRuntimeException(e);
        }

        return cc;
    }

    public static Configuration subset(String s) {
        return config.subset(s);
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Boolean.valueOf(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Boolean.valueOf(config);
            }
        }

        return config.getBoolean(key, defaultValue);
    }

    public static short getShort(String key, short defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Short.parseShort(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Short.parseShort(config);
            }
        }

        return config.getShort(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Long.parseLong(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Long.parseLong(config);
            }
        }

        return config.getLong(key, defaultValue);
    }

    public static Integer getInteger(String key, Integer defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Integer.parseInt(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Integer.parseInt(config);
            }
        }

        return config.getInteger(key, defaultValue);
    }

    public static String[] getStringArray(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            String[] array = envValue.split(",");
            return array;
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                String[] array = config.split(",");
                return array;
            }
        }

        return config.getStringArray(key);
    }

    public static BigInteger getBigInteger(String key, BigInteger defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return new BigInteger(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return new BigInteger(config);
            }
        }

        return config.getBigInteger(key, defaultValue);
    }

    public static boolean containsKey(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return true;
        }
        return config.containsKey(key);
    }

    public static float getFloat(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Float.parseFloat(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Float.parseFloat(config);
            }
        }

        return config.getFloat(key);
    }

    public static BigDecimal getBigDecimal(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return new BigDecimal(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return new BigDecimal(config);
            }
        }

        return config.getBigDecimal(key);
    }

    public static int getInt(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Integer.parseInt(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Integer.parseInt(config);
            }
        }

        return config.getInt(key);
    }

    public static Iterator<String> getKeys() {
        return config.getKeys();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Boolean.valueOf(envValue).booleanValue();
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Boolean.valueOf(config).booleanValue();
            }
        }

        return config.getBoolean(key, defaultValue);
    }

    public static BigInteger getBigInteger(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return new BigInteger(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return new BigInteger(config);
            }
        }

        return config.getBigInteger(key);
    }

    public static String getString(String key, String defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return StringUtils.trimToNull(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return StringUtils.trimToNull(config);
            }
        }

        return StringUtils.trimToNull(config.getString(key, defaultValue));
    }

    public static float getFloat(String key, float defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Float.valueOf(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Float.valueOf(config);
            }
        }

        return config.getFloat(key, defaultValue);
    }

    public static void addProperty(String key, Object value) {
        config.addProperty(key, value);
    }

    public static Byte getByte(String key, Byte defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return envValue.getBytes()[0];
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return config.getBytes()[0];
            }
        }

        return config.getByte(key, defaultValue);
    }

    public static List<Object> getList(String key) {
        return config.getList(key);
    }

    public static Properties getProperties(String key) {
        return config.getProperties(key);
    }

    public static Long getLong(String key, Long defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Long.valueOf(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Long.valueOf(config);
            }
        }

        return config.getLong(key, defaultValue);
    }

    public static long getLong(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Long.valueOf(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Long.valueOf(config);
            }
        }

        return config.getLong(key);
    }

    public static List<Object> getList(String key, List<?> defaultValue) {
        return config.getList(key, defaultValue);
    }

    public static Short getShort(String key, Short defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Short.parseShort(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Short.valueOf(config);
            }
        }

        return config.getShort(key, defaultValue);
    }

    public static byte getByte(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return envValue.getBytes()[0];
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return envValue.getBytes()[0];
            }
        }

        return config.getByte(key);
    }

    public static Float getFloat(String key, Float defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Float.parseFloat(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Float.valueOf(config);
            }
        }

        return config.getFloat(key, defaultValue);
    }

    public static void setProperty(String key, Object value) {
        config.setProperty(key, value);
    }

    public static void clearProperty(String key) {
        config.clearProperty(key);
    }

    public static void clear() {
        config.clear();
    }

    public static double getDouble(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Double.parseDouble(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Double.valueOf(config);
            }
        }

        return config.getDouble(key);
    }

    public static int getInt(String key, int defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Integer.parseInt(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Integer.valueOf(config);
            }
        }

        return config.getInt(key, defaultValue);
    }

    public static BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return new BigDecimal(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return new BigDecimal(config);
            }
        }

        return config.getBigDecimal(key, defaultValue);
    }

    public static String getString(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return StringUtils.trimToNull(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return StringUtils.trimToNull(config);
            }
        }

        String string = config.getString(key);
        return StringUtils.trimToNull(string);
    }

    public static double getDouble(String key, double defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Double.parseDouble(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Double.valueOf(config);
            }
        }

        return config.getDouble(key, defaultValue);
    }

    public static Iterator<String> getKeys(String prefix) {
        return config.getKeys(prefix);
    }

    public static boolean isEmpty() {
        return config.isEmpty();
    }

    public static Object getProperty(String key) {
        return config.getProperty(key);
    }

    public static short getShort(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Short.parseShort(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Short.valueOf(config);
            }
        }

        return config.getShort(key);
    }

    public static boolean getBoolean(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Boolean.valueOf(envValue).booleanValue();
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Boolean.valueOf(config).booleanValue();
            }
        }

        return config.getBoolean(key);
    }

    public static Double getDouble(String key, Double defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Double.parseDouble(envValue);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringUtils.isEmpty(config)) {
                return Double.valueOf(config);
            }
        }

        return config.getDouble(key, defaultValue);
    }

    private static String getConfigFromSpringEnv(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Environment springEnv = getSpringEnv();
        if (springEnv != null) {
            return springEnv.getProperty(key);
        }
        return null;
    }

    private static Environment getSpringEnv() {
        if (SpringUtil.getContext() != null) {
            return SpringUtil.getContext().getEnvironment();
        }
        return null;
    }
}
