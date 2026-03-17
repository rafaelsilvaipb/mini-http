# mini-http

### Vou criar um servidor de aplicação completo, no estilo Tomcat, mas usando o mínimo possível de recursos, para que seja fácil de entender e modificar. Ele vai ser escrito em Java, e vai implementar o protocolo HTTP/1.1.

- [x] 1 - Conexão TCP e leitura de requisições HTTP 
- [ ] 2 - Resposta HTTP em json e rotas dinâmicas
- [ ] 3 - GET, POST, PUT, DELETE

## 1 - Conexão TCP e leitura de requisições HTTP
### Toda a comunicação HTTP é feita sobre TCP, então o primeiro passo é criar um servidor TCP que escute em uma porta específica (por exemplo, 8080) e aceite conexões de clientes.
### Implementei o protocolo HTTP/1.1, ele é quem vai se comunicar com o navegador, mas o caminho é fornecido pelo ServerSocket.


Exemplo do protocolo http/1.1, somente isso que o navegador entende.
```
GET / HTTP/1.1
Host: localhost:8080
content-type:  application/json  

{"fu": "ba"}
``` 
