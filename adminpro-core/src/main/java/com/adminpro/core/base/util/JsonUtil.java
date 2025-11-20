package com.adminpro.core.base.util;

import com.google.gson.*;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * utility class for json processing
 *
 * @author simon
 */
public final class JsonUtil {
    private static final String DATE_FORMAT = "dd/MM/yyyy hh:mm:ss a";

    private static final GsonBuilder defaultBuilder = new GsonBuilder()
            .registerTypeAdapter(int.class, new IntegerDeserializer())
            .registerTypeAdapter(long.class, new LongDeserializer())
            .registerTypeAdapter(double.class, new DoubleDeserializer());
    private static final Gson gson = defaultBuilder.setDateFormat(DATE_FORMAT).create();

    /**
     * to create the type, please find code below for reference.
     * Type collectionType = new TypeToken<Collection<Integer>>(){}.getType();
     *
     * @param jsonStr
     * @param type
     * @return
     */
    public static <T> T fromJson(String jsonStr, Type type) {
        return gson.fromJson(jsonStr, type);
    }

    public static <T> T fromJson(String jsonStr, Type type, String dateFormat) {
        if (StringUtil.isNotBlank(dateFormat)) {
            Gson aGson = defaultBuilder.setDateFormat(dateFormat).create();
            return aGson.fromJson(jsonStr, type);
        }
        return gson.fromJson(jsonStr, type);
    }

    public <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Type type) {
        return gson.fromJson(json, type);
    }

    /**
     * convert the json string to java object.
     *
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        return gson.fromJson(jsonStr, clazz);
    }

    public static <T> T fromJson(String jsonStr, Class<T> clazz, String dateFormat) {
        if (StringUtil.isNotBlank(dateFormat)) {
            Gson aGson = defaultBuilder.setDateFormat(dateFormat).create();
            return aGson.fromJson(jsonStr, clazz);
        }
        return gson.fromJson(jsonStr, clazz);
    }

    /**
     * convert the java object to a json string.
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static String toJson(Object obj, String dateFormat) {
        if (StringUtil.isNotBlank(dateFormat)) {
            Gson aGson = defaultBuilder.setDateFormat(dateFormat).create();
            return aGson.toJson(obj);
        }
        return gson.toJson(obj);
    }

    static class IntegerDeserializer implements JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonElement json, Type typeOfSrc, JsonDeserializationContext context) {
            String value = json.getAsJsonPrimitive().getAsString();
            try {
                return Integer.valueOf(value);
            } catch (Exception ex) {
                return 0;
            }
        }
    }

    static class LongDeserializer implements JsonDeserializer<Long> {
        @Override
        public Long deserialize(JsonElement json, Type typeOfSrc, JsonDeserializationContext context) {
            String value = json.getAsJsonPrimitive().getAsString();
            try {
                return Long.valueOf(value);
            } catch (Exception ex) {
                return 0L;
            }
        }
    }

    static class DoubleDeserializer implements JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonElement json, Type typeOfSrc, JsonDeserializationContext context) {
            String value = json.getAsJsonPrimitive().getAsString();
            try {
                return Double.valueOf(value);
            } catch (Exception ex) {
                return 0d;
            }
        }
    }

    private JsonUtil() {
    }
}
