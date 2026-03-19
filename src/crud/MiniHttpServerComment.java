package crud;

// Importa classes de entrada e saída.
// Vamos usar para ler a requisição e escrever a resposta.


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// Classe principal do servidor HTTP.
public class MiniHttpServerComment {

    // Guarda um "banco em memória" com um único usuário.
    // Como é static, ele fica disponível durante toda a execução do programa.
    private static User savedUser = new User(1, "Rafael", "rafa@email.com");

    // Método principal da aplicação.
    public static void main(String[] args) throws Exception {

        // Cria um servidor TCP escutando na porta 8080.
        ServerSocket serverSocket = new ServerSocket(8080);

        // Log de inicialização.
        System.out.println("Servidor rodando na porta 8080...");

        // Loop infinito para aceitar conexões continuamente.
        while (true) {

            // Aguarda um cliente se conectar.
            Socket client = serverSocket.accept();

            // Cria um leitor de caracteres a partir do fluxo de entrada do socket.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8)
            );

            // Obtém o fluxo de saída para enviar a resposta ao cliente.
            OutputStream out = client.getOutputStream();

            // Lê a primeira linha da requisição HTTP.
            // Exemplo:
            // GET /user HTTP/1.1
            // POST /user HTTP/1.1
            String requestLine = in.readLine();

            // Exibe a primeira linha no console.
            System.out.println("Request: " + requestLine);

            // Se o cliente não enviou nada, fecha a conexão e vai para a próxima.
            if (requestLine == null || requestLine.isEmpty()) {
                client.close();
                continue;
            }

            // Divide a linha inicial por espaços.
            // Exemplo:
            // [0] = GET
            // [1] = /user
            // [2] = HTTP/1.1
            String[] parts = requestLine.split(" ");

            // Extrai o método HTTP.
            String method = parts[0];

            // Extrai o caminho da URL.
            String path = parts[1];

            // Variável para armazenar o tamanho do body.
            // Em HTTP, isso vem normalmente no header Content-Length.
            int contentLength = 0;

            // Variável auxiliar para armazenar cada header lido.
            String headerLine;

            // Lê os headers até encontrar uma linha vazia.
            // Em HTTP, a linha vazia separa cabeçalhos do corpo.
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {

                // Exibe cada header no console para facilitar depuração.
                System.out.println("Header: " + headerLine);

                // Se o header começar com Content-Length,
                // extraímos o valor numérico que indica o tamanho do body.
                if (headerLine.startsWith("Content-Length:")) {

                    // Pega apenas a parte depois dos dois pontos.
                    String value = headerLine.substring("Content-Length:".length()).trim();

                    // Converte o valor para inteiro.
                    contentLength = Integer.parseInt(value);
                }
            }

            // Variável que guardará o corpo da requisição.
            String requestBody = "";

            // Se houver corpo, lemos exatamente a quantidade indicada em Content-Length.
            if (contentLength > 0) {

                // Cria um buffer de chars com o tamanho exato do body.
                char[] bodyChars = new char[contentLength];

                // Lê os caracteres do body para dentro do buffer.
                in.read(bodyChars, 0, contentLength);

                // Converte o array de chars para String.
                requestBody = new String(bodyChars);

                // Exibe o body no console.
                System.out.println("Body: " + requestBody);
            }

            // Trata a requisição do favicon do navegador.
            if (path.equals("/favicon.ico")) {

                // Responde 204 para dizer que não há conteúdo.
                String response =
                        "HTTP/1.1 204 No Content\r\n" +
                                "Content-Length: 0\r\n" +
                                "\r\n";

                // Escreve a resposta.
                out.write(response.getBytes(StandardCharsets.UTF_8));

                // Trata GET /user
            } else if (method.equals("GET") && path.equals("/user")) {

                // Se não houver usuário salvo, devolve 404.
                if (savedUser == null) {
                    String body = "Usuário não encontrado";

                    String response =
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Type: text/plain; charset=UTF-8\r\n" +
                                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                                    "\r\n" +
                                    body;

                    out.write(response.getBytes(StandardCharsets.UTF_8));
                } else {

                    // Converte o usuário salvo para JSON.
                    String json = JsonParser.toJson(savedUser);

                    // Monta a resposta HTTP com conteúdo JSON.
                    String response =
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: application/json; charset=UTF-8\r\n" +
                                    "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                                    "\r\n" +
                                    json;

                    // Envia a resposta.
                    out.write(response.getBytes(StandardCharsets.UTF_8));
                }

                // Trata POST /user
            } else if (method.equals("POST") && path.equals("/user")) {

                // Converte o JSON recebido em um objeto User.
                User newUser = JsonParser.fromJson(requestBody, User.class);

                // Salva o novo usuário em memória.
                savedUser = newUser;

                // Converte o usuário salvo para JSON.
                String json = JsonParser.toJson(savedUser);

                // Monta a resposta 201 Created.
                String response =
                        "HTTP/1.1 201 Created\r\n" +
                                "Content-Type: application/json; charset=UTF-8\r\n" +
                                "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                                "\r\n" +
                                json;

                // Envia a resposta.
                out.write(response.getBytes(StandardCharsets.UTF_8));

                // Trata PUT /user
            } else if (method.equals("PUT") && path.equals("/user")) {

                // Se não existir usuário salvo, aqui vamos criar/substituir mesmo assim.
                User updatedUser = JsonParser.fromJson(requestBody, User.class);

                // Atualiza o "registro" em memória.
                savedUser = updatedUser;

                // Serializa o objeto atualizado.
                String json = JsonParser.toJson(savedUser);

                // Monta a resposta 200 OK.
                String response =
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: application/json; charset=UTF-8\r\n" +
                                "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                                "\r\n" +
                                json;

                // Envia a resposta.
                out.write(response.getBytes(StandardCharsets.UTF_8));

                // Trata DELETE /user
            } else if (method.equals("DELETE") && path.equals("/user")) {

                // Remove o usuário da memória.
                savedUser = null;

                // Corpo simples da resposta.
                String body = "Usuário removido com sucesso";

                // Monta a resposta HTTP.
                String response =
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: text/plain; charset=UTF-8\r\n" +
                                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                                "\r\n" +
                                body;

                // Envia a resposta.
                out.write(response.getBytes(StandardCharsets.UTF_8));

            } else {

                // Corpo da resposta para rotas não encontradas.
                String body = "Rota não encontrada";

                // Monta a resposta 404.
                String response =
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Type: text/plain; charset=UTF-8\r\n" +
                                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                                "\r\n" +
                                body;

                // Envia a resposta 404.
                out.write(response.getBytes(StandardCharsets.UTF_8));
            }

            // Garante que tudo seja realmente enviado ao cliente.
            out.flush();

            // Fecha a conexão.
            client.close();
        }
    }
}