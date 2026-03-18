package jackson_get;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
            String path = line.split(" ")[1];

            if (method.equals("GET") && path.equals("/user")) {
                User user = new User(1, "Rafael", "rafa@email.com");
                String json = JsonSerializer.toJson(user);
                String response =
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: application/json\r\n" +
                                "Content-Length: " + json.getBytes().length + "\r\n" +
                                "\r\n" +
                                json;
                out.write(response.getBytes());
            } else if (path.equals("/favicon.ico")) {
                String response =
                        "HTTP/1.1 204 No Content\r\n\r\n";
                out.write(response.getBytes());
            } else {
                String body = "Rota não encontrada";
                String response =
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Type: text/plain\r\n" +
                                "Content-Length: " + body.length() + "\r\n" +
                                "\r\n" +
                                body;
                out.write(response.getBytes());
            }

            out.flush();
            client.close();
        }
    }
}
