import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serSocket = new ServerSocket(9999);
        System.out.println("Waiting for client ..");
        Socket sock = serSocket.accept();

        System.out.println("Client connected ..");

        InputStream in = sock.getInputStream();
        OutputStream out = sock.getOutputStream();

        byte buffer[] = new byte[1024];
        in.read(buffer);

        System.out.println("Received from client - " + new String(buffer).trim());
        out.write("Hello from Server ...".getBytes());

        sock.close();
        serSocket.close();
    }
}
