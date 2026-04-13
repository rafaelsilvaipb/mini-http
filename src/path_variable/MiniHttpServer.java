package path_variable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MiniHttpServer {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Servidor rodando na porta 8080...");

        while (true) {
            Socket client = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream();

            String line = in.readLine();

            System.out.println("Request: " + line);

            if (line == null || line.isEmpty()) {
                client.close();
                continue;
            }

            String method = line.split(" ")[0];
            String fullPath = line.split(" ")[1];

            String path;
            String queryString = null;

            if (fullPath.contains("?")) {
                String[] parts = fullPath.split("\\?", 2);
                path = parts[0];
                queryString = parts[1];
            } else {
                path = fullPath;
            }

            Map<String, String> queryParams = RequestParser.parseQueryParams(queryString);

            if (method.equals("GET") && path.equals("/user")) {
                String idParam = queryParams.get("id");
                String nameParam = queryParams.get("name");
                String emailParam = queryParams.get("email");

                int id = idParam != null && !idParam.isEmpty() ? Integer.parseInt(idParam) : 1;
                String name = nameParam != null && !nameParam.isEmpty() ? nameParam : "Rafael";
                String email = emailParam != null && !emailParam.isEmpty() ? emailParam : "rafa@email.com";

                User user = new User(id, name, email);
                sendJson(out, JsonSerializer.toJson(user));

            } else if (method.equals("GET") && path.startsWith("/user/")) {
                String idFromPath = PathUtils.extractIdFromPath(path);

                if (idFromPath == null || idFromPath.isEmpty()) {
                    sendText(out, 400, "Bad Request", "ID inválido");
                } else {
                    String nameParam = queryParams.get("name");
                    String emailParam = queryParams.get("email");

                    int id = Integer.parseInt(idFromPath);
                    String name = nameParam != null && !nameParam.isEmpty() ? nameParam : "Rafael";
                    String email = emailParam != null && !emailParam.isEmpty() ? emailParam : "rafa@email.com";

                    User user = new User(id, name, email);
                    sendJson(out, JsonSerializer.toJson(user));
                }

            } else if (path.equals("/favicon.ico")) {
                String response = "HTTP/1.1 204 No Content\r\n\r\n";
                out.write(response.getBytes(StandardCharsets.UTF_8));

            } else {
                sendText(out, 404, "Not Found", "Rota não encontrada");
            }

            out.flush();
            client.close();
        }
    }

    private static void sendJson(OutputStream out, String json) throws Exception {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        String headers =
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json; charset=UTF-8\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(body);
    }

    private static void sendText(OutputStream out, int statusCode, String statusText, String bodyText) throws Exception {
        byte[] body = bodyText.getBytes(StandardCharsets.UTF_8);

        String headers =
                "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(body);
    }
}
