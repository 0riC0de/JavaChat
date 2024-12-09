import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String name;

    public ClientHandler(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.name = reader.readLine();
            clientHandlers.add(this);
            writer.println(name + " has entered the chat!  :)");
            broadCastMessage(" has entered the chat!  :)");
        } catch (IOException e) {
            closeEveryThing(clientSocket , reader , writer);
        }
    }
    @Override
    public void run() {
        String messageFromClient;
        while(clientSocket.isConnected()) {
            try {
                messageFromClient = reader.readLine();
                broadCastMessage(messageFromClient);
            } catch (IOException e) {
                closeEveryThing(clientSocket , reader , writer);
                break;
            }
        }
    }
    public void broadCastMessage(String message) {
        for(ClientHandler clientHandler : clientHandlers) {
            try{
                if (!clientHandler.name.equals(this.name)) {
                    clientHandler.writer.println(this.name + " : " + message);
                    clientHandler.writer.println();
                    clientHandler.writer.flush();
                }
            } catch (Exception e) {
                closeEveryThing(clientSocket , reader , writer);
            }
        }
    }
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadCastMessage(this.name + " has left the chat!  :(");
    }
    public void closeEveryThing(Socket clientSocket , BufferedReader reader , PrintWriter writer) {
        removeClientHandler();
        try {
            if(clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if(reader != null) {
                reader.close();
            }
            if(writer != null) {
                writer.close();
            }
            removeClientHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
