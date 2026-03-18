package jackson_get;

import java.lang.reflect.Field;

public class JsonSerializer {
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
}
