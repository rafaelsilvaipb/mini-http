package jackson_get;

import java.io.*;

// Importa ServerSocket.
// Ele representa o servidor escutando uma porta TCP.
import java.net.ServerSocket;

// Importa Socket.
// Ele representa a conexão com um cliente específico.
import java.net.Socket;

public class MiniHttpServerComment {

    // Método principal da aplicação.
    // "throws Exception" simplifica o exemplo, evitando try/catch em toda parte.
    public static void main(String[] args) throws Exception {

        // Cria um servidor TCP ouvindo a porta 8080.
        // O sistema operacional entrega para esse processo as conexões feitas nessa porta.
        ServerSocket serverSocket = new ServerSocket(8080);

        // Mostra no console que o servidor está no ar.
        System.out.println("Servidor rodando na porta 8080...");

        // Loop infinito: o servidor ficará aceitando conexões continuamente.
        while (true) {
            // Espera um cliente se conectar.
            // Quando alguém acessa localhost:8080, esse accept() retorna.
            Socket client = serverSocket.accept();

            // Cria um leitor de texto em cima do InputStream do socket.
            // O InputStream recebe bytes vindos do cliente.
            // O InputStreamReader converte esses bytes em caracteres.
            // O BufferedReader permite ler linha por linha.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );

            // Obtém o fluxo de saída da conexão.
            // É por ele que vamos enviar a resposta HTTP ao cliente.
            OutputStream out = client.getOutputStream();

            // Lê a primeira linha da requisição HTTP.
            // Exemplo: "GET /user HTTP/1.1"
            String line = in.readLine();

            // Imprime a linha recebida para depuração.
            System.out.println("Request: " + line);

            // Verifica se a linha veio nula ou vazia.
            // Isso pode acontecer se o cliente abriu e fechou sem mandar nada.
            if (line == null || line.isEmpty()) {

                // Fecha a conexão atual.
                client.close();

                // Pula para a próxima iteração do loop.
                continue;
            }

            // Divide a primeira linha da request por espaços.
            // Exemplo:
            // "GET /user HTTP/1.1"
            // vira:
            // [0] = GET
            // [1] = /user
            // [2] = HTTP/1.1
            String method = line.split(" ")[0];

            // Pega o caminho da URL.
            // Exemplo: "/user"
            String path = line.split(" ")[1];

            // Se o método for GET e o path for /user...
            if (method.equals("GET") && path.equals("/user")) {

                // Cria um objeto Java simples que será devolvido para o cliente.
                User user = new User(1, "Rafael", "rafa@email.com");

                // Converte o objeto para JSON usando nosso serializer manual.
                String json = JsonSerializer.toJson(user);

                // Monta a resposta HTTP completa.
                // Linha 1: status HTTP
                // Linha 2: header dizendo que o conteúdo é JSON
                // Linha 3: tamanho do corpo em bytes
                // Linha em branco: separa cabeçalhos do corpo
                // Depois: o JSON propriamente dito
                String response =
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: application/json\r\n" +
                                "Content-Length: " + json.getBytes().length + "\r\n" +
                                "\r\n" +
                                json;

                // Escreve a resposta no socket.
                out.write(response.getBytes());

                // Se o navegador pedir o favicon...
            } else if (path.equals("/favicon.ico")) {

                // Monta uma resposta 204 No Content.
                // Isso significa: não há conteúdo para retornar.
                String response =
                        "HTTP/1.1 204 No Content\r\n\r\n";

                // Envia essa resposta ao navegador.
                out.write(response.getBytes());

            } else {

                // Corpo da resposta para rotas inexistentes.
                String body = "Rota não encontrada";

                // Monta uma resposta HTTP 404.
                // Informa que a rota não existe.
                String response =
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Type: text/plain\r\n" +
                                "Content-Length: " + body.length() + "\r\n" +
                                "\r\n" +
                                body;

                // Envia a resposta 404.
                out.write(response.getBytes());
            }

            // Garante que tudo que foi escrito seja realmente enviado.
            out.flush();

            // Fecha a conexão com o cliente.
            // Nesse modelo simples, cada request usa uma conexão e depois fecha.
            client.close();
        }
    }
}
