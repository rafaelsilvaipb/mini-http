package crud;

import java.lang.reflect.Field;

// Classe responsável por converter JSON em objeto Java.
// Essa versão é genérica: funciona para qualquer classe simples.
public class JsonParserComment {

    // Método genérico.
    // <T> significa que ele pode retornar qualquer tipo.
    // Exemplo de uso:
    // User user = JsonParser.fromJson(json, User.class);
    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {

        // Remove espaços extras do começo e do fim.
        json = json.trim();

        // Se o JSON começar com "{", remove a chave inicial.
        if (json.startsWith("{")) {
            json = json.substring(1);
        }

        // Se o JSON terminar com "}", remove a chave final.
        if (json.endsWith("}")) {
            json = json.substring(0, json.length() - 1);
        }

        // Cria uma instância vazia da classe recebida.
        // Isso exige que a classe tenha construtor sem argumentos.
        T instance = clazz.getDeclaredConstructor().newInstance();

        // Se o JSON estiver vazio, retorna o objeto vazio.
        if (json.isBlank()) {
            return instance;
        }

        // Divide o JSON em pares separados por vírgula.
        // Exemplo:
        // "id":1,"name":"Rafael","email":"rafa@email.com"
        // vira 3 pedaços.
        String[] pairs = json.split(",");

        // Percorre cada par chave:valor.
        for (String pair : pairs) {

            // Divide em duas partes no primeiro ":" encontrado.
            // Exemplo:
            // "\"name\":\"Rafael\""
            // vira:
            // [0] = "\"name\""
            // [1] = "\"Rafael\""
            String[] keyValue = pair.split(":", 2);

            // Limpa a chave removendo espaços e aspas.
            String key = clean(keyValue[0]);

            // Pega o valor bruto.
            String rawValue = keyValue[1].trim();

            // Procura, na classe, o campo com o mesmo nome da chave JSON.
            // Exemplo: "name" -> campo name da classe User.
            Field field = clazz.getDeclaredField(key);

            // Permite acessar o campo mesmo se ele for private.
            field.setAccessible(true);

            // Converte o valor textual para o tipo real do campo.
            // Exemplo:
            // "1" -> int
            // "\"Rafael\"" -> String
            Object convertedValue = convertValue(rawValue, field.getType());

            // Coloca o valor convertido dentro do objeto criado.
            field.set(instance, convertedValue);
        }

        // Retorna o objeto preenchido.
        return instance;
    }

    // Método auxiliar que remove espaços e aspas
    // no começo e no fim do texto.
    private static String clean(String text) {

        // Remove espaços extras.
        text = text.trim();

        // Remove aspas iniciais, se existirem.
        if (text.startsWith("\"")) {
            text = text.substring(1);
        }

        // Remove aspas finais, se existirem.
        if (text.endsWith("\"")) {
            text = text.substring(0, text.length() - 1);
        }

        // Retorna o texto limpo.
        return text;
    }

    // Método responsável por converter o valor textual do JSON
    // para o tipo Java correto do campo.
    private static Object convertValue(String rawValue, Class<?> type) {

        // Remove espaços extras.
        rawValue = rawValue.trim();

        // Se o valor for "null", retorna null.
        if (rawValue.equals("null")) {
            return null;
        }

        // Se o tipo do campo for String...
        if (type == String.class) {

            // Remove aspas e devolve o texto.
            return clean(rawValue);
        }

        // Se o tipo for int primitivo ou Integer...
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(rawValue);
        }

        // Se o tipo for long primitivo ou Long...
        if (type == long.class || type == Long.class) {
            return Long.parseLong(rawValue);
        }

        // Se o tipo for double primitivo ou Double...
        if (type == double.class || type == Double.class) {
            return Double.parseDouble(rawValue);
        }

        // Se o tipo for boolean primitivo ou Boolean...
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(rawValue);
        }

        // Se cair aqui, significa que o tipo ainda não foi implementado.
        throw new IllegalArgumentException("Tipo não suportado: " + type.getName());
    }
}