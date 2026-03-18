package jackson_get;

import java.lang.reflect.Field;

// Classe responsável por converter objetos Java em JSON.
// É uma versão bem pequena de algo parecido com o Jackson.
class JsonSerializerComment {

    // Método estático: pode ser chamado sem criar objeto da classe.
    // Recebe qualquer objeto e devolve sua representação em JSON.
    public static String toJson(Object obj) throws IllegalAccessException {

        // Obtém a classe real do objeto recebido.
        // Exemplo: se obj for um User, clazz será User.class.
        Class<?> clazz = obj.getClass();

        // Cria um StringBuilder para montar o JSON aos poucos.
        // StringBuilder é melhor que concatenar String em loop.
        StringBuilder json = new StringBuilder();

        // Começa o JSON com chave de abertura.
        json.append("{");

        // Pega todos os campos declarados na classe.
        // Exemplo para User:
        // id, name, email
        Field[] fields = clazz.getDeclaredFields();

        // Percorre todos os campos do objeto.
        for (int i = 0; i < fields.length; i++) {
            // Pega o campo atual.
            Field field = fields[i];

            // Permite acessar o valor do campo mesmo se ele for private.
            // Sem isso, reflection poderia falhar em campos não públicos.
            field.setAccessible(true);

            // Obtém o nome do campo.
            // Exemplo: "id", "name", "email"
            String name = field.getName();

            // Obtém o valor desse campo dentro do objeto recebido.
            // Exemplo: 1, "Rafael", "rafa@email.com"
            Object value = field.get(obj);

            // Escreve o nome da propriedade no JSON.
            // Exemplo:
            // "id":
            json.append("\"").append(name).append("\":");

            // Verifica se o valor do campo é String.
            if (value instanceof String) {
                // Se for String, coloca aspas no valor.
                // Exemplo:
                // "name":"Rafael"
                json.append("\"").append(value).append("\"");
            } else {
                // Se não for String, escreve direto.
                // Exemplo:
                // "id":1
                json.append(value);
            }

            // Se não for o último campo, adiciona vírgula.
            // Isso separa uma propriedade JSON da próxima.
            if (i < fields.length - 1) {
                json.append(",");
            }
        }

        // Fecha o objeto JSON com chave final.
        json.append("}");

        // Converte o StringBuilder para String e retorna.
        return json.toString();
    }
}
