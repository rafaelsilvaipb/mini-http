# mini-http

Vou criar um servidor de aplicação completo, no estilo Tomcat, mas usando o mínimo possível de recursos, para que seja fácil de entender e modificar. Ele vai ser escrito em Java, e vai implementar o protocolo HTTP/1.1.

- [x] 1 - Conexão TCP e leitura de requisições HTTP 
- [x] 2 - Resposta HTTP em json e rotas dinâmicas (mini Jackson)
- [x] 3 - GET, POST, PUT, DELETE
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

## 3 - GET, POST, PUT, DELETE
CRUD "completo", anda falta um  monte de coisa, mas temos as quatro operações mais famosas do HTTP.

GET
```
curl -i http://localhost:8080/user
```
POST
```
curl -i -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d "{\"id\":2,\"name\":\"Maria\",\"email\":\"maria@email.com\"}"
```
PUT
```
curl -i -X PUT http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d "{\"id\":3,\"name\":\"João\",\"email\":\"joao@email.com\"}"
```
E O DELETE
```
curl -i -X DELETE http://localhost:8080/user
```

<img width="584" height="610" alt="image" src="https://github.com/user-attachments/assets/f825493a-ac35-44aa-9810-df3b2165c065" />

<img width="1096" height="551" alt="image" src="https://github.com/user-attachments/assets/c4a68924-b4b1-41de-bf75-4834f379c7ca" />




