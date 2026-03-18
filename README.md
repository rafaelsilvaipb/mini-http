# mini-http

Vou criar um servidor de aplicação completo, no estilo Tomcat, mas usando o mínimo possível de recursos, para que seja fácil de entender e modificar. Ele vai ser escrito em Java, e vai implementar o protocolo HTTP/1.1.

- [x] 1 - Conexão TCP e leitura de requisições HTTP 
- [x] 2 - Resposta HTTP em json e rotas dinâmicas (mini Jackson)
- [ ] 3 - GET, POST, PUT, DELETE
- [ ] 4 - Path Variable (/user/{path-variable})
- [ ] 5 - Request Param (/user?fu=ba)

## 1 - Conexão TCP e leitura de requisições HTTP
 Toda a comunicação HTTP é feita sobre TCP, então o primeiro passo é criar um servidor TCP que escute em uma porta específica (por exemplo, 8080) e aceite conexões de clientes.
 Implementei o protocolo HTTP/1.1, ele é quem vai se comunicar com o navegador, mas o caminho é fornecido pelo ServerSocket.


Exemplo do protocolo http/1.1, somente isso que o navegador entende.
```
GET / HTTP/1.1
Host: localhost:8080
content-type:  application/json  

{"fu": "ba"}
```

## 2 - Resposta HTTP em json e rotas dinâmicas (mini Jackson)
Nesse aqui eu fiz um Mini Jackson, que converte uma classe em JSON para o nosso retorno. Também implementei o método GET recebendo uma rota do navegador.

No navegador você digita
```
http://localhost:8080/user
```
e tem o retorno
```
{
  "id": 1,
  "name": "Rafael",
  "email": "rafa@email.com"
}
```
no console aparece isso aqui pra facilitar (sim, /favicon.ico é chamado pelo navegador ao chamar a primera requisição, legal, né?)
```
Servidor rodando na porta 8080...
Request: GET /user HTTP/1.1
Request: GET /favicon.ico HTTP/1.1
```
