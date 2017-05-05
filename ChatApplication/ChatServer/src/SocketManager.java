/**
 * Created by paypal on 5/2/2017.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

/* Socket Manager is used for all socket related communication. This thread continuously
 * waits for new connections from clients */
public class SocketManager extends Thread {

    final int DEFAULT_PORT = 50000;

    private ServerSocket serverSocket;
    private Thread t;
    private String threadName = "Incoming Connections Thread";
    CopyOnWriteArrayList<Connection> clientList;

    //Store a reference to the client list
    SocketManager(CopyOnWriteArrayList<Connection> clientList)
    {
        this.clientList = clientList;
    }

    int getClientCount()
    {
        return clientList.size();
    }

    //Creates a connection object for a client. Spawns a new thread for a client
    void createConnection(Socket clientSocket)
    {
        try {
            //Create new connection object
            Connection conn = new Connection();
            //Store socket, I/O Reader Writer for this client
            conn.clientSocket = clientSocket;
            conn.pWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            conn.bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //Get the username for this client and store in connection object
            conn.pWriter.println("Enter your username");
            String username;
            if ((username = conn.bReader.readLine()) != null) {
                conn.username = username;
            }

            //Add this client to client list
            clientList.add(conn);

            System.out.println("**Adding connection** " + clientList.size() + " for " + conn.username);

            //Start a new thread for this client
            SessionManager sessionManager = new SessionManager(conn.username, clientList);
            sessionManager.init();
        }
        catch (IOException e) {
            System.err.println("Could not get Print Writer or Buffered Reader for client socket");
            System.exit(1);
        }
    }

    public void run()
    {
        while (true) {
            try {
                //Wait for new connections
                Socket clientSocket = serverSocket.accept();

                //Client has connected. Create a new connection object
                createConnection(clientSocket);

            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
    }

    void init()
    {
        //Initialize the server listening socket
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + DEFAULT_PORT);
            System.exit(1);
        }

        System.out.println("Listening on port " + DEFAULT_PORT);

        t = new Thread(this, threadName);
        t.start();
    }

    protected void finalize()
    {
        //Close the server socket
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Could not close server socket on port: " + DEFAULT_PORT);
            System.exit(1);
        }
    }
}

