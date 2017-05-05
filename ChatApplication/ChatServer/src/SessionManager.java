/**
 * Created by paypal on 5/3/2017.
 */
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/* This thread is created for every client that connects. Manages user interaction and
 * chat session for that client */
public class SessionManager extends Thread {

    private Thread t;
    //Threadname is client's username
    private String threadName;
    //List of connected clients
    CopyOnWriteArrayList<Connection> clientList;

    SessionManager(String username, CopyOnWriteArrayList<Connection> clientList)
    {
       threadName = username;
        //Store a reference to the client list
       this.clientList = clientList;
    }

    int getClientCount()
    {
        return clientList.size();
    }

    public void run() {
        Connection conn = null;
        //Find the connection object for this client from client list
        for (int i = 0; i < getClientCount(); i++) {
            conn = clientList.get(i);
            if (conn.username == threadName) {
                break;
            }
        }

        try {
            //while (true) {
                String fromUser;
                if ((fromUser = conn.bReader.readLine()) != null) {//Read l/L
                    if (fromUser.equalsIgnoreCase("l")) {
                        //First send the number of usernames to client
                        conn.pWriter.println(getClientCount());
                        //Send all the connected users' names to client
                        for (Connection conn1 : clientList) {
                            conn.pWriter.println(conn1.username);
                        }
                        //Receive the chat peer user name (user selected for chatting with)
                        if ((fromUser = conn.bReader.readLine()) != null) {
                            //Start chat session between these two users
                            createChatSession(conn.username, fromUser);
                        }
                    }
                }
            //}
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to get I/O for myConnection object");
            System.exit(1);
        }
    }

    void createChatSession(String usernameA, String usernameB)
    {
        try {

            Connection connA = null;
            for (int i = 0; i < getClientCount(); i++) {
                connA = clientList.get(i);
                if (connA.username == usernameA) {
                    break;
                }
            }

            Connection connB = null;
            for (int i = 0; i < getClientCount(); i++) {
                connB = clientList.get(i);
                if (connB.username == usernameB) {
                    break;
                }
            }

            PrintWriter pWriterA = connA.pWriter;
            BufferedReader brA = connA.bReader;

            PrintWriter pWriterB = connB.pWriter;
            BufferedReader brB = connB.bReader;

            String fromClientA = null;
            String fromClientB = null;

            String outputLine;
            outputLine = "Yes, I am here... Let us chat!!!";

            pWriterA.println(outputLine);
            pWriterB.println(outputLine);

            boolean isByeA = false;
            boolean isByeB = false;

            //Exchange messages between the two users
            while (isByeA == false || isByeB == false) {
                if ((fromClientA = brA.readLine()) != null) {
                    System.out.println("Message from A - " + fromClientA);
                    pWriterB.println(fromClientA);
                    if (fromClientA.equalsIgnoreCase("bye")) {
                        isByeA = true;
                    }
                }
                if ((fromClientB = brB.readLine()) != null) {
                    System.out.println("Message from B - " + fromClientB);
                    pWriterA.println(fromClientB);
                    if (fromClientB.equalsIgnoreCase("bye")) {
                        isByeB = true;
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to get I/O for socket");
            System.exit(1);
        }
    }

    //Start the thread in init
    void init()
    {
        t = new Thread(this, threadName);
        t.start();
    }
}
