/**
 * Created by paypal on 5/2/2017.
 */
import java.io.*;
import java.net.*;

public class ChatClient1 {

    final int DEFAULT_PORT = 50000;

    Socket clientSocket;
    PrintWriter pWriter;
    BufferedReader bReader;
    String myUsername;
    String server;
    BufferedReader stdInReader;

    ChatClient1()
    {

    }

    void init()
    {
        try {
            //Obtain the client socket for communication with server
            clientSocket = new Socket(server, DEFAULT_PORT);

            //Store reader/writer for this socket
            pWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //Store standard input reader
            stdInReader = new BufferedReader(new InputStreamReader(System.in));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + server);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + server);
            System.exit(1);
        }
    }

    protected void finalize()
    {
        try {
            //Close all resources
            pWriter.close();
            bReader.close();
            stdInReader.close();
            clientSocket.close();

        } catch (IOException e) {
            System.err.println("Could not close client socket or I/O Reader/Writer");
            System.exit(1);
        }
    }


    public static void main(String[] args) throws IOException {

        //Create and initialize chat client
        ChatClient1 chatClient1 = new ChatClient1();
        chatClient1.init();

        String fromServer;
        String userName;
        //Take the username from user
        if ((fromServer = chatClient1.bReader.readLine()) != null)
        {
            System.out.println(fromServer);

            if (fromServer.equalsIgnoreCase("Enter your username")) {
                if ((userName = chatClient1.stdInReader.readLine()) != null)
                {
                    //Send the username to the server. Server stores it
                    chatClient1.pWriter.println(userName);
                    //Store the username locally
                    chatClient1.myUsername = userName;
                }
            }
        }

        String fromUser;
        System.out.println("Press l/L to see who is online");

        if ((fromUser = chatClient1.stdInReader.readLine()) != null)//Read l/L
        {
            //Send l/L to server
            chatClient1.pWriter.println(fromUser);
        }

        if ((fromServer = chatClient1.bReader.readLine()) != null)//Read number of usernames
        {
            Integer clientCount = Integer.parseInt(fromServer);
            //Print the usernames
            for (int i = 0; i<clientCount; i++)
            {
                if ((fromServer = chatClient1.bReader.readLine()) != null)
                {
                    System.out.println(fromServer);
                }
            }
        }
        //Choose a user from the list to chat with
        System.out.println("Select the user u want to chat with");
        if ((fromUser = chatClient1.stdInReader.readLine()) != null)
        {
            //Send chosen chat peer name to server
            chatClient1.pWriter.println(fromUser);
        }

        String otherId = fromUser;
        boolean isByeServer = false;
        boolean isByeUser = false;

        //Exchange messages while no one has said bye
        while(isByeServer == false || isByeUser == false)
        {
            if ((fromServer = chatClient1.bReader.readLine()) != null)
            {
                System.out.println(otherId + " : " + fromServer);

                if (fromServer.equalsIgnoreCase("bye")) {
                    isByeServer = true;
                    if (isByeUser == true)
                    {
                        break;
                    }
                }
            }

            System.out.print(chatClient1.myUsername + " / me: ");

            if ((fromUser = chatClient1.stdInReader.readLine()) != null)
            {
                chatClient1.pWriter.println(fromUser);
                if (fromUser.equalsIgnoreCase("bye")) {
                    isByeUser = true;
                }
            }
        }
    }
}
