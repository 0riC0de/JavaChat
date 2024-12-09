import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Server {

    static final int PORT = 2545;
    private static ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void StartServer()
    {

        try {
            System.out.println(" server running waiting for connections");
            while(!serverSocket.isClosed())
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new Client Has Entered!");
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void closeServerSocket() {
        try {
            if(serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args)
    {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Server server = new Server(serverSocket);
            StartServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}