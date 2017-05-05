/**
 * Created by paypal on 5/2/2017.
 */
import java.net.*;
import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;

class Connection
{
    Socket clientSocket;
    PrintWriter pWriter;
    BufferedReader bReader;
    String username;
}

public class ChatServer {

    //Socket Manager for managing the socket communication part
    private SocketManager socketManager;

    //List of connected clients
    CopyOnWriteArrayList<Connection> clientList;

    ChatServer()
    {
        clientList = new CopyOnWriteArrayList<Connection>();

        //Create and initialize the socket manager
        socketManager = new SocketManager(clientList);
        socketManager.init();

    }

    protected void finalize()
    {
        try {
            //Close all resources for all clients in client list
            for (Connection conn : clientList)
            {
                conn.bReader.close();
                conn.pWriter.close();
                conn.clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Could not close client socket or I/O Reader/Writer");
            System.exit(1);
        }
    }


    public static void main(String[] args) {

        //Create and initialize chat server
        ChatServer chatServer = new ChatServer();

    }
}
