package path_variable;

// Importa a classe BufferedReader.
// Ela serve para ler texto de forma eficiente.
// No nosso caso, vamos usá-la para ler a requisição HTTP que chega pelo socket.
import java.io.BufferedReader;

// Importa a classe InputStreamReader.
// Ela converte um fluxo de bytes (InputStream) em um leitor de caracteres.
// Isso é importante porque o socket entrega bytes, mas queremos ler texto.
import java.io.InputStreamReader;

// Importa a classe OutputStream.
// Ela representa um fluxo de saída de bytes.
// Vamos usá-la para escrever a resposta HTTP de volta para o cliente.
import java.io.OutputStream;

// Importa ServerSocket.
// Essa é a classe que "abre a porta" do servidor e fica aguardando conexões.
import java.net.ServerSocket;

// Importa Socket.
// Quando um cliente se conecta ao servidor, essa conexão é representada por um Socket.
import java.net.Socket;

// Importa StandardCharsets.
// Aqui usamos UTF-8 explicitamente para converter texto em bytes e evitar problemas de encoding.
import java.nio.charset.StandardCharsets;

// Importa Map.
// Vamos usar Map<String, String> para guardar os query params,
// por exemplo: /user?id=10&name=Rafael
import java.util.Map;

// Declara a classe principal do programa.
// Essa classe representa um servidor HTTP muito simples.
public class MiniHttpServerComment {

    // Método principal: ponto de entrada da aplicação Java.
    // "throws Exception" foi usado para simplificar o exemplo,
    // evitando tratar manualmente cada exceção com try/catch.
    public static void main(String[] args) throws Exception {

        // Cria um ServerSocket escutando a porta 8080.
        // Isso significa que o programa ficará aguardando conexões nessa porta.
        // Exemplo: http://localhost:8080
        ServerSocket serverSocket = new ServerSocket(8080);

        // Exibe no console que o servidor foi iniciado.
        System.out.println("Servidor rodando na porta 8080...");

        // Loop infinito para manter o servidor sempre ativo.
        // Ele vai aceitar uma conexão, processá-la, fechar,
        // e depois esperar a próxima.
        while (true) {

            // Fica bloqueado aguardando um cliente se conectar.
            // Quando alguém acessa o servidor pelo navegador ou Postman,
            // o accept() retorna um Socket representando essa conexão.
            Socket client = serverSocket.accept();

            // Obtém o fluxo de entrada do cliente.
            // client.getInputStream() devolve bytes.
            // InputStreamReader converte bytes em caracteres.
            // BufferedReader facilita a leitura de linhas de texto.
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // Obtém o fluxo de saída do cliente.
            // Vamos usar esse objeto para enviar a resposta HTTP.
            OutputStream out = client.getOutputStream();

            // Lê a primeira linha da requisição HTTP.
            // Exemplo:
            // GET /user/10?name=Rafael HTTP/1.1
            // Essa linha é chamada de "request line".
            String line = in.readLine();

            // Mostra no console a linha recebida.
            // Isso ajuda muito a entender o que o cliente enviou.
            System.out.println("Request: " + line);

            // Se a linha vier nula ou vazia,
            // significa que não conseguimos ler uma requisição válida.
            if (line == null || line.isEmpty()) {

                // Fecha a conexão com o cliente.
                client.close();

                // Continua o loop e volta a esperar a próxima conexão.
                continue;
            }

            // Divide a request line pelo espaço.
            // Exemplo:
            // "GET /user/10?name=Rafael HTTP/1.1"
            // vira:
            // [0] GET
            // [1] /user/10?name=Rafael
            // [2] HTTP/1.1
            //
            // Aqui pegamos a posição 0, que é o método HTTP.
            String method = line.split(" ")[0];

            // Aqui pegamos a posição 1, que é o caminho completo da URL.
            // Nesse momento ainda pode conter query params.
            // Exemplo: /user/10?name=Rafael
            String fullPath = line.split(" ")[1];

            // Declara a variável path.
            // Ela vai guardar apenas o caminho puro, sem query string.
            // Exemplo: /user/10
            String path;

            // Declara a variável queryString e inicializa com null.
            // Ela vai guardar só a parte depois do ?.
            // Exemplo: name=Rafael&email=x@y.com
            String queryString = null;

            // Verifica se a URL possui query params.
            // O caractere ? separa o caminho da query string.
            if (fullPath.contains("?")) {

                // Divide a string em no máximo 2 partes usando o ?.
                // O "\\?" é necessário porque o ? tem significado especial em regex.
                //
                // Exemplo:
                // /user/10?name=Rafael&email=rafa@email.com
                // vira:
                // parts[0] = /user/10
                // parts[1] = name=Rafael&email=rafa@email.com
                String[] parts = fullPath.split("\\?", 2);

                // A primeira parte é o path puro.
                path = parts[0];

                // A segunda parte é a query string.
                queryString = parts[1];
            } else {

                // Se não existe ?, então o caminho inteiro já é o path.
                // Exemplo: /user/10
                path = fullPath;
            }

            // Faz o parsing da query string para um Map.
            // Exemplo:
            // "id=10&name=Rafael"
            // vira um Map com:
            // "id" -> "10"
            // "name" -> "Rafael"
            //
            // Essa responsabilidade foi colocada em outra classe
            // para deixar o servidor mais organizado.
            Map<String, String> queryParams = RequestParser.parseQueryParams(queryString);

            // Verifica se a requisição é GET e o caminho é exatamente /user.
            // Exemplo:
            // GET /user?id=10&name=Rafael
            if (method.equals("GET") && path.equals("/user")) {

                // Pega o query param "id".
                // Se não existir, retorna null.
                String idParam = queryParams.get("id");

                // Pega o query param "name".
                String nameParam = queryParams.get("name");

                // Pega o query param "email".
                String emailParam = queryParams.get("email");

                // Define o id final.
                //
                // Se idParam não for null e não estiver vazio,
                // converte para int.
                //
                // Caso contrário, usa 1 como valor padrão.
                //
                // Isso é um operador ternário:
                // condição ? valorSeVerdadeiro : valorSeFalso
                int id = idParam != null && !idParam.isEmpty() ? Integer.parseInt(idParam) : 1;

                // Define o nome final.
                // Se o parâmetro name existir e não estiver vazio, usa ele.
                // Caso contrário, usa "Rafael".
                String name = nameParam != null && !nameParam.isEmpty() ? nameParam : "Rafael";

                // Define o email final.
                // Mesmo raciocínio do name.
                String email = emailParam != null && !emailParam.isEmpty() ? emailParam : "rafa@email.com";

                // Cria um objeto User com os dados obtidos.
                User user = new User(id, name, email);

                // Converte o User para JSON usando o JsonSerializer
                // e envia a resposta JSON para o cliente.
                sendJson(out, JsonSerializer.toJson(user));

                // Verifica se é GET e se o caminho começa com /user/
                // Exemplo:
                // /user/10
                // /user/25
                //
                // Aqui estamos simulando um path variable.
                // Em frameworks como Spring seria algo como:
                // @GetMapping("/user/{id}")
            } else if (method.equals("GET") && path.startsWith("/user/")) {

                // Extrai o id do caminho.
                // Exemplo:
                // /user/10 -> "10"
                //
                // Essa lógica foi colocada numa classe separada para organização.
                String idFromPath = PathUtils.extractIdFromPath(path);

                // Se não conseguiu extrair um id válido,
                // devolve erro 400 Bad Request.
                if (idFromPath == null || idFromPath.isEmpty()) {

                    // Envia uma resposta de texto com status 400.
                    sendText(out, 400, "Bad Request", "ID inválido");
                } else {

                    // Mesmo quando o ID vem do path,
                    // ainda podemos aceitar outros valores via query param.
                    // Exemplo:
                    // /user/10?name=Maria&email=maria@email.com
                    String nameParam = queryParams.get("name");
                    String emailParam = queryParams.get("email");

                    // Converte o id extraído do path para int.
                    int id = Integer.parseInt(idFromPath);

                    // Se vier name na query, usa ele.
                    // Caso contrário, usa o nome padrão.
                    String name = nameParam != null && !nameParam.isEmpty() ? nameParam : "Rafael";

                    // Se vier email na query, usa ele.
                    // Caso contrário, usa o email padrão.
                    String email = emailParam != null && !emailParam.isEmpty() ? emailParam : "rafa@email.com";

                    // Cria o objeto User com os dados finais.
                    User user = new User(id, name, email);

                    // Serializa o objeto em JSON e envia ao cliente.
                    sendJson(out, JsonSerializer.toJson(user));
                }

                // Navegadores geralmente fazem uma requisição automática para /favicon.ico
                // tentando buscar o ícone da aba.
                //
                // Se não tratarmos isso, pode parecer que há erro no servidor
                // quando na verdade é só o navegador pedindo o ícone.
            } else if (path.equals("/favicon.ico")) {

                // Monta uma resposta HTTP com status 204 No Content.
                // Esse status significa: "recebi sua requisição, mas não há conteúdo para retornar".
                String response = "HTTP/1.1 204 No Content\r\n\r\n";

                // Escreve a resposta no socket convertendo a string para bytes em UTF-8.
                out.write(response.getBytes(StandardCharsets.UTF_8));

            } else {

                // Se não caiu em nenhuma rota conhecida,
                // devolvemos 404 Not Found.
                sendText(out, 404, "Not Found", "Rota não encontrada");
            }

            // Garante que tudo o que foi escrito no OutputStream
            // seja realmente enviado para o cliente.
            out.flush();

            // Fecha a conexão com o cliente.
            // Neste servidor simples, cada requisição abre e fecha uma conexão.
            client.close();
        }
    }

    // Método auxiliar para enviar resposta JSON.
    // Recebe:
    // - o fluxo de saída do cliente
    // - a string JSON que será enviada no corpo da resposta
    private static void sendJson(OutputStream out, String json) throws Exception {

        // Converte o corpo JSON em bytes UTF-8.
        // Isso é importante porque o Content-Length deve ser calculado em bytes,
        // não em quantidade de caracteres.
        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        // Monta os headers da resposta HTTP.
        //
        // HTTP/1.1 200 OK       -> status da resposta
        // Content-Type          -> tipo do conteúdo
        // charset=UTF-8         -> encoding do texto
        // Content-Length        -> tamanho exato do corpo em bytes
        // linha em branco       -> separa headers do body
        String headers =
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json; charset=UTF-8\r\n" +
                        "Content-Length: " + body.length + "\r\n" +
                        "\r\n";

        // Escreve os headers no socket.
        out.write(headers.getBytes(StandardCharsets.UTF_8));

        // Escreve o corpo JSON no socket.
        out.write(body);
    }

    // Método auxiliar para enviar resposta de texto puro.
    // Recebe:
    // - OutputStream do cliente
    // - status code (ex: 404)
    // - status text  (ex: Not Found)
    // - texto do corpo da resposta
    private static void sendText(OutputStream out, int statusCode, String statusText, String bodyText) throws Exception {

        // Converte o texto do corpo em bytes UTF-8.
        byte[] body = bodyText.getBytes(StandardCharsets.UTF_8);

        // Monta os headers HTTP dinamicamente com base nos parâmetros recebidos.
        //
        // Exemplo de resultado:
        // HTTP/1.1 404 Not Found
        // Content-Type: text/plain; charset=UTF-8
        // Content-Length: 19
        //
        // Rota não encontrada
        String headers =
                "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                        "Content-Type: text/plain; charset=UTF-8\r\n" +
                        "Content-Length: " + body.length + "\r\n" +
                        "\r\n";

        // Escreve os headers no socket.
        out.write(headers.getBytes(StandardCharsets.UTF_8));

        // Escreve o corpo no socket.
        out.write(body);
    }
}
