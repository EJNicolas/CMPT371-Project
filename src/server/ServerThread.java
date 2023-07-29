/* 
 *  ServerThread.java
 * 
 *  Description: This class implements Runnable so that it can be run as a thread.
 *               Each thread is responsible for handling a single client, with its own input and output streams.
 *               Each thread also listens for connected client input and sends a response.
 *               Use interrupt() to close the client socket and streams.
 */

package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerThread implements Runnable
{
    // Sockets and streams
    private Socket client;
    private TCPServer server;
    private OutputStream os;
    private InputStream is;
    private PrintWriter out;
    private BufferedReader in;
    private boolean interrupt = false;

    // Client data
    private int clientID;
    private ArrayList<String> parsedMessage;

    // Constructor
    public ServerThread(Socket client, int clientID, TCPServer server) {
        this.client = client;
        this.clientID = clientID;
        this.server = server;
        this.parsedMessage = new ArrayList<String>();

        setupStreams();
        sendClientID();

        // Debug message, remove later
        String clientMessage = "I am player " + clientID + " and I am connected.";
        this.server.broadcast(clientMessage, clientID);
    }

    // Getters
    public int getClientID() { return clientID; }

    // Thread run method
    @Override
    public void run()
    {
        System.out.println("TCP client connected with ID " + clientID + " connected.");
        setupStreams();

        while(true) 
        { 
            if(interrupt) 
            { 
                close(); 
                break; 
            }
            readToken(); 
        }
    }

    // Setups input and output streams
    private void setupStreams()
    {
        try {
            os = client.getOutputStream();
            is = client.getInputStream();
            out = new PrintWriter(os, true);
            in = new BufferedReader(new InputStreamReader(is));
        } 
        catch (IOException e){
            System.out.println("Error setting up TCP server streams.");
        }
    }

    // Parses input from client and sends response
    public void readToken()
    {
        while(true) {
            try {
                if(in.ready()) 
                {
                    parseMessage(in.readLine());
                    switch(parsedMessage.get(0))
                    {
                        case "Hello":
                            out.println("Back at you");
                            break;
                    }
                }
            } 
            catch (IOException e){
                System.out.println("Error reading from TCP server socket.");
            }
        }
    }

    // Parses message fromt client
    private void parseMessage(String message)
    {
        parsedMessage.clear();
        String[] tokens = message.split(" ");

        for(String token : tokens) 
        { 
            parsedMessage.add(token); 
        }
    }

    // Sends given string to client
    public void sendMessage(String message)
    {
        out.println(message);
    }

    // Stop thread
    public void interrupt()
    {
        interrupt = true;
    }
    
    // Send the clientID as a String
    private void sendClientID() {
        out.println(clientID);
    }

    // Closes client socket and streams
    private void close()
    {
        try {
            out.close();
            in.close();
            os.close();
            is.close();
            client.close();
        } catch (IOException e){
            System.out.println("Error closing TCP server thread.");
        }
    }
}
