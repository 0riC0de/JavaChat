import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client{
    static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 2545;
    String name;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public Client(Socket socket, String name) {
        try{
            this.socket = socket;
            this.name = name;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(socket, reader, writer);

        }
    }
    public void sendMessage() {
        try {
            writer.write(name);
            writer.newLine();
            writer.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                writer.write(message);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }
    }
    public void listenForMessages() {
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                String messageFromServer;
                while(socket.isConnected()) {
                    try {
                        messageFromServer = reader.readLine();
                        System.out.println(messageFromServer);
                    } catch (IOException e) {
                        closeEverything(socket, reader, writer);
                        break;
                    }
                }
            }
        }).start();
    }
    public void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name: ");
        String name = scanner.nextLine();
        try {
            Socket socket = new Socket(SERVER_IP, PORT);
            Client client = new Client(socket, name);
            client.listenForMessages();
            client.sendMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}