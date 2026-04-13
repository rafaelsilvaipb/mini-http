package path_variable;

public class PathUtils {

    public static String extractIdFromPath(String path) {
        String[] parts = path.split("/");

        if (parts.length >= 3) {
            return parts[2];
        }

        return null;
    }
}
