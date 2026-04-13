package path_variable;

// Declara a classe PathUtils.
// O nome "Utils" normalmente é usado para classes utilitárias,
// ou seja, classes que agrupam métodos pequenos e reaproveitáveis.
//
// Essa classe não representa uma entidade do negócio, como User ou Product.
// Ela existe apenas para guardar uma lógica auxiliar:
// neste caso, extrair o ID que vem dentro de uma URL.
//
// Exemplo de URL:
// /user/10
//
// A ideia é pegar o "10".
public class PathUtilsComment {

    // Declara um método static chamado extractIdFromPath.
    //
    // "public" significa que ele pode ser chamado de qualquer lugar.
    //
    // "static" significa que não precisamos criar um objeto da classe
    // para usar esse método.
    // Exemplo:
    // PathUtils.extractIdFromPath("/user/10");
    //
    // Ele recebe uma String chamada path.
    // Exemplo de valor recebido:
    // "/user/10"
    //
    // Ele devolve uma String.
    // Se conseguir extrair o ID, devolve o valor.
    // Se não conseguir, devolve null.
    public static String extractIdFromPath(String path) {

        // Divide a string path usando "/" como separador.
        //
        // Exemplo:
        // path = "/user/10"
        //
        // Quando fazemos:
        // path.split("/")
        //
        // o Java quebra a string em partes.
        //
        // Resultado:
        // parts[0] = ""
        // parts[1] = "user"
        // parts[2] = "10"
        //
        // O primeiro elemento fica vazio porque a string começa com "/".
        // Então existe "algo antes da primeira barra"? Não.
        // Por isso o primeiro item vira uma string vazia.
        String[] parts = path.split("/");

        // Verifica se o array possui pelo menos 3 posições.
        //
        // Por que 3?
        //
        // Porque esperamos algo nesse formato:
        // /user/10
        //
        // Depois do split, isso gera:
        // [0] ""
        // [1] "user"
        // [2] "10"
        //
        // Então precisamos garantir que a posição [2] realmente existe.
        //
        // Se não fizermos essa verificação e tentarmos acessar parts[2]
        // quando a URL for, por exemplo, só "/user",
        // o programa lançará um erro:
        // ArrayIndexOutOfBoundsException
        if (parts.length >= 3) {

            // Retorna o terceiro item do array, ou seja, o valor que vem
            // depois de "/user/".
            //
            // Exemplo:
            // "/user/10" -> retorna "10"
            // "/user/25" -> retorna "25"
            //
            // Repare que ele retorna String, não int.
            // A conversão para número normalmente é feita depois,
            // em outro lugar do código.
            return parts[2];
        }

        // Se não houver pelo menos 3 partes,
        // significa que não foi possível encontrar um ID no caminho.
        //
        // Exemplos:
        // "/user" -> não tem parts[2]
        // "/" -> não tem parts[2]
        //
        // Nesse caso, devolvemos null.
        //
        // Null aqui significa:
        // "não encontrei nenhum valor válido"
        return null;
    }
}