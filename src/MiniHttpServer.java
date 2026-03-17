import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MiniHttpServer {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Mini HTTP Server is running on port 8080...");

        while (true){
            Socket clientSocket = serverSocket.accept();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream outputStream = clientSocket.getOutputStream();
            String line = bufferedReader.readLine();
            System.out.println("Received request: " + line);

            String body = "Mini HTTP Server Response";

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: " + body.getBytes().length + "\r\n" +
                    "\r\n" +
                    body;

            outputStream.write(response.getBytes());
            outputStream.flush();
            clientSocket.close();

        }
    }
}