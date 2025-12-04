package com.adminpro.framework.common.helper;

import com.adminpro.core.base.util.EnvironmentUtils;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.tools.domains.entity.config.ConfigEntity;
import com.adminpro.tools.domains.entity.config.ConfigService;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 配置的帮助类
 *
 * @author simon
 */
public class ConfigHelper {
    private static Configuration config = initConfiguration();

    private static Configuration initConfiguration() {
        CompositeConfiguration cc = new CompositeConfiguration();
        try {
            cc.addConfiguration(new PropertiesConfiguration("project.properties"));
            cc.addConfiguration(new PropertiesConfiguration("config.properties"));
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            return Boolean.valueOf(configEntity.getValue());
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            return Short.parseShort(configEntity.getValue());
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            return Long.parseLong(configEntity.getValue());
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            return Integer.parseInt(configEntity.getValue());
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String[] array = configEntity.getValue().split(",");
            return array;
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                String[] array = config.split(",");
                return array;
            }
        }

        return config.getStringArray(key);
    }

    public static Map<String, String> getStringMap(String key) {
        Map<String, String> map = new LinkedHashMap<>();
        String[] stringArray = getStringArray(key);
        if(ArrayUtils.isNotEmpty(stringArray)) {
            for (int i = 0; i < stringArray.length; i++) {
                String s = stringArray[i];
                String[] split = s.split(":");
                map.put(split[0], split[1]);
            }
        }
        return map;
    }

    public static BigInteger getBigInteger(String key, BigInteger defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return new BigInteger(envValue);
        }
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return new BigInteger(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            return true;
        }

        if (getSpringEnv() != null) {
            return getSpringEnv() == null ? false : getSpringEnv().containsProperty(key);
        }

        return config.containsKey(key);
    }

    public static float getFloat(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Float.parseFloat(envValue);
        }
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Float.parseFloat(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return new BigDecimal(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Integer.parseInt(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Boolean.valueOf(value).booleanValue();
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return new BigInteger(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return StringUtils.trimToNull(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return StringUtils.trimToNull(config);
            }
        }

        return StringUtils.trimToNull(config.getString(key, defaultValue));
    }

    public static float getFloat(String key, float defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Float.parseFloat(envValue);
        }
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Float.parseFloat(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return Float.parseFloat(config);
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return value.getBytes()[0];
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Long.valueOf(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return Long.valueOf(config);
            }
        }

        return config.getLong(key, defaultValue);
    }

    public static long getLong(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Long.parseLong(envValue);
        }
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Long.parseLong(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Short.parseShort(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return Short.parseShort(config);
            }
        }

        return config.getShort(key, defaultValue);
    }

    public static byte getByte(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return envValue.getBytes()[0];
        }
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return value.getBytes()[0];
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return config.getBytes()[0];
            }
        }

        return config.getByte(key);
    }

    public static Float getFloat(String key, Float defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Float.parseFloat(envValue);
        }
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Float.parseFloat(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return Float.parseFloat(config);
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Double.parseDouble(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return Double.parseDouble(config);
            }
        }

        return config.getDouble(key);
    }

    public static int getInt(String key, int defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Integer.parseInt(envValue);
        }
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Integer.parseInt(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return Integer.parseInt(config);
            }
        }

        return config.getInt(key, defaultValue);
    }

    public static BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return new BigDecimal(envValue);
        }
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return new BigDecimal(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return StringUtils.trimToNull(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Double.parseDouble(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return Double.parseDouble(config);
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Short.parseShort(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return Short.parseShort(config);
            }
        }

        return config.getShort(key);
    }

    public static boolean getBoolean(String key) {
        String envValue = EnvironmentUtils.getInstance().get(key);
        if (!StringUtils.isEmpty(envValue)) {
            return Boolean.valueOf(envValue).booleanValue();
        }
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Boolean.valueOf(value).booleanValue();
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
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
        ConfigEntity configEntity = ConfigService.getInstance().findByKey(key);
        if (configEntity != null && StringHelper.isNotEmpty(configEntity.getValue())) {
            String value = configEntity.getValue();
            return Double.parseDouble(value);
        }

        if (getSpringEnv() != null) {
            String config = getConfigFromSpringEnv(key);
            if (!StringHelper.isEmpty(config)) {
                return Double.parseDouble(config);
            }
        }

        return config.getDouble(key, defaultValue);
    }

    private static String getConfigFromSpringEnv(String key) {
        if (StringHelper.isEmpty(key)) {
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
