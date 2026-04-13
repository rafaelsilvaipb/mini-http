package path_variable;

// Importa a classe HashMap.
// HashMap é uma implementação da interface Map.
// Ele guarda dados no formato chave -> valor.
//
// Exemplo:
// "id"    -> "10"
// "name"  -> "Rafael"
// "email" -> "rafa@email.com"
//
// Vamos usar isso para armazenar os parâmetros da query string.
import java.util.HashMap;

// Importa a interface Map.
// Map representa uma estrutura de dados onde cada chave aponta para um valor.
//
// Estamos usando Map<String, String>, ou seja:
// chave do tipo String
// valor do tipo String
//
// Exemplo real:
// "id" -> "10"
import java.util.Map;

// Declara a classe RequestParser.
// O objetivo dessa classe é concentrar a lógica de "parse",
// ou seja, de interpretação de uma string da URL.
//
// Exemplo de entrada:
// id=10&name=Rafael&email=rafa@email.com
//
// Exemplo de saída:
// um Map com:
// "id" -> "10"
// "name" -> "Rafael"
// "email" -> "rafa@email.com"
//
// Essa classe é utilitária: ela não representa um objeto do negócio,
// como User ou Product.
// Ela existe só para ajudar a transformar texto em uma estrutura útil.
public class RequestParserComment {

    // Declara um método public e static chamado parseQueryParams.
    //
    // "public" significa que ele pode ser chamado por outras classes.
    //
    // "static" significa que não precisamos criar um objeto RequestParser
    // para usar esse método.
    // Podemos chamar assim:
    // RequestParser.parseQueryParams("id=10&name=Rafael");
    //
    // O método recebe uma String chamada query.
    // Exemplo:
    // "id=10&name=Rafael&email=rafa@email.com"
    //
    // O retorno é um Map<String, String>.
    // Ou seja, ele pega uma string da URL e transforma em pares chave/valor.
    public static Map<String, String> parseQueryParams(String query) {

        // Cria um HashMap vazio chamado params.
        //
        // Esse mapa vai armazenar os parâmetros encontrados na query string.
        //
        // Exemplo final esperado:
        // params.put("id", "10");
        // params.put("name", "Rafael");
        //
        // Começamos com ele vazio porque ainda não processamos nada.
        Map<String, String> params = new HashMap<>();

        // Verifica se a query é null ou está vazia.
        //
        // query == null:
        // significa que nem existe query string.
        //
        // query.isEmpty():
        // significa que a string existe, mas está vazia.
        //
        // Exemplo de situação:
        // /user
        //
        // Nesse caso, não há nada após o ?,
        // então não temos parâmetros para extrair.
        if (query == null || query.isEmpty()) {

            // Se não há query para processar,
            // devolvemos o mapa vazio.
            //
            // Isso é melhor do que devolver null,
            // porque quem chama o método pode usar o resultado
            // sem precisar ficar testando null toda hora.
            //
            // Exemplo:
            // Map<String, String> params = parseQueryParams(null);
            // params.get("id"); // não quebra
            return params;
        }

        // Divide a query string pelo caractere "&".
        //
        // O "&" é o separador padrão entre parâmetros de URL.
        //
        // Exemplo:
        // "id=10&name=Rafael&email=rafa@email.com"
        //
        // Depois do split("&"), teremos:
        // pairs[0] = "id=10"
        // pairs[1] = "name=Rafael"
        // pairs[2] = "email=rafa@email.com"
        //
        // Ou seja: agora temos cada parâmetro separado.
        String[] pairs = query.split("&");

        // Percorre cada item do array "pairs".
        //
        // Cada "pair" representa um parâmetro individual,
        // normalmente no formato chave=valor.
        //
        // Exemplo:
        // pair = "id=10"
        // depois:
        // pair = "name=Rafael"
        for (String pair : pairs) {

            // Divide cada parâmetro em no máximo 2 partes usando "=".
            //
            // Exemplo:
            // "id=10" -> ["id", "10"]
            // "name=Rafael" -> ["name", "Rafael"]
            //
            // O número 2 significa:
            // "quebre no máximo em 2 pedaços"
            //
            // Isso é importante porque evita quebrar demais
            // se o valor tiver o caractere "=" dentro dele.
            //
            // Exemplo:
            // "token=abc=123"
            //
            // Com split("=", 2), o resultado seria:
            // ["token", "abc=123"]
            //
            // Sem o 2, poderia virar mais pedaços do que queremos.
            String[] keyValue = pair.split("=", 2);

            // Pega a primeira parte como chave.
            //
            // Exemplo:
            // para "id=10", key será "id"
            // para "name=Rafael", key será "name"
            //
            // Assumimos aqui que toda string tem pelo menos a chave.
            String key = keyValue[0];

            // Define o valor do parâmetro.
            //
            // Se existir a segunda posição no array,
            // então usamos keyValue[1] como valor.
            //
            // Exemplo:
            // "id=10" -> value = "10"
            //
            // Se não existir a segunda posição,
            // usamos string vazia "".
            //
            // Isso cobre casos como:
            // "name="
            // ou até "name"
            //
            // Nesses cenários, a chave existe,
            // mas o valor está vazio.
            //
            // Esse código usa operador ternário:
            // condição ? valorSeVerdadeiro : valorSeFalso
            String value = keyValue.length > 1 ? keyValue[1] : "";

            // Coloca a chave e o valor dentro do Map.
            //
            // Exemplo:
            // params.put("id", "10");
            // params.put("name", "Rafael");
            //
            // Depois disso, podemos recuperar o valor assim:
            // params.get("id") -> "10"
            params.put(key, value);
        }

        // Depois de processar todos os parâmetros,
        // devolvemos o mapa preenchido.
        return params;
    }
}