package crud;

import java.io.*;

import java.net.ServerSocket;

import java.net.Socket;

import java.nio.charset.StandardCharsets;

public class MiniHttpServer {

    private static User savedUser = new User(1, "Rafael", "rafa@email.com");

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Servidor rodando na porta 8080...");

        while (true) {
            Socket client = serverSocket.accept();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8)
            );
            OutputStream out = client.getOutputStream();
            String requestLine = in.readLine();
            System.out.println("Request: " + requestLine);

            if (requestLine == null || requestLine.isEmpty()) {
                client.close();
                continue;
            }

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];
            int contentLength = 0;
            String headerLine;

            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                System.out.println("Header: " + headerLine);

                if (headerLine.startsWith("Content-Length:")) {
                    String value = headerLine.substring("Content-Length:".length()).trim();
                    contentLength = Integer.parseInt(value);
                }
            }

            String requestBody = "";

            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                in.read(bodyChars, 0, contentLength);
                requestBody = new String(bodyChars);
                System.out.println("Body: " + requestBody);
            }

            if (path.equals("/favicon.ico")) {
                out.write(getResponse("204 No Content", "")
                        .getBytes(StandardCharsets.UTF_8));
            } else if (method.equals("GET") && path.equals("/user")) {
                if (savedUser == null) {
                    out.write(getResponse("404 Not Found", "Usuário não encontrado")
                            .getBytes(StandardCharsets.UTF_8));
                } else {
                    String json = JsonParser.toJson(savedUser);
                    out.write(getResponse("200 OK", json).getBytes(StandardCharsets.UTF_8));
                }
            } else if (method.equals("POST") && path.equals("/user")) {
                savedUser = JsonParser.fromJson(requestBody, User.class);
                out.write(getResponse("201 Created", JsonParser.toJson(savedUser))
                        .getBytes(StandardCharsets.UTF_8));
            } else if (method.equals("PUT") && path.equals("/user")) {
                savedUser = JsonParser.fromJson(requestBody, User.class);
                out.write(getResponse("200 OK", JsonParser.toJson(savedUser))
                        .getBytes(StandardCharsets.UTF_8));
            } else if (method.equals("DELETE") && path.equals("/user")) {
                savedUser = null;
                String response = getResponse("200 OK", "Usuário removido com sucesso");
                out.write(response.getBytes(StandardCharsets.UTF_8));
            } else {
                String response = getResponse("404 Not Found", "Rota não encontrada");
                out.write(response.getBytes(StandardCharsets.UTF_8));
            }

            out.flush();
            client.close();
        }

    }

    private static String getResponse(String status, String body) {
        return "HTTP/1.1 " + status + "\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" +
                body;
    }
}