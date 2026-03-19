package crud;

import java.lang.reflect.Field;

public class JsonParser {
    public static String toJson(Object obj) throws IllegalAccessException {

        Class<?> clazz = obj.getClass();

        StringBuilder json = new StringBuilder();

        json.append("{");

        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {

            Field field = fields[i];

            field.setAccessible(true);

            String name = field.getName();

            Object value = field.get(obj);

            json.append("\"").append(name).append("\":");

            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(value);
            }

            if (i < fields.length - 1) {
                json.append(",");
            }
        }

        json.append("}");

        return json.toString();
    }
    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        json = json.trim();

        if (json.startsWith("{")) {
            json = json.substring(1);
        }

        if (json.endsWith("}")) {
            json = json.substring(0, json.length() - 1);
        }

        T instance = clazz.getDeclaredConstructor().newInstance();

        if (json.isBlank()) {
            return instance;
        }

        String[] pairs = json.split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);

            String key = clean(keyValue[0]);

            String rawValue = keyValue[1].trim();

            Field field = clazz.getDeclaredField(key);

            field.setAccessible(true);

            Object convertedValue = convertValue(rawValue, field.getType());

            field.set(instance, convertedValue);
        }

        return instance;
    }

    private static String clean(String text) {
        text = text.trim();

        if (text.startsWith("\"")) {
            text = text.substring(1);
        }

        if (text.endsWith("\"")) {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }

    private static Object convertValue(String rawValue, Class<?> type) {
        rawValue = rawValue.trim();

        if (rawValue.equals("null")) {
            return null;
        }

        if (type == String.class) {
            return clean(rawValue);
        }

        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(rawValue);
        }

        if (type == long.class || type == Long.class) {
            return Long.parseLong(rawValue);
        }

        if (type == double.class || type == Double.class) {
            return Double.parseDouble(rawValue);
        }

        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(rawValue);
        }

        throw new IllegalArgumentException("Tipo não suportado: " + type.getName());
    }
}