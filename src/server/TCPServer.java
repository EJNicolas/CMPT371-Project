/* 
 *  TCPServer.java
 * 
 *  Description: Responsible for setting up a TCP server socket on the given port.
 *               Creates a new thread (ServerThread) for each client that connects.
 *               Use close() to close the server socket and all client threads.
*/

package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPServer
{
    private int port;
    private ServerSocket serverSocket;
    private ArrayList<ServerThread> threads;

    // Constructor, initializes port and thread list
    public TCPServer(int port) 
    {
        this.port = port;
        threads = new ArrayList<ServerThread>();
        Game.getInstance().addPlayer(0);
    }

    // Starts server, listens for clients and creates a thread for each client
    public void startServer()
    {
        int clientID = 0;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP server listening on port " + port + ".");

            while(true)
            {
                clientID++;
                Socket client = serverSocket.accept();
                Runnable serverThread = new ServerThread(client, clientID, this);  
                threads.add((ServerThread)serverThread);
                new Thread(serverThread).start();
                
            }
        } 
        catch (IOException e){
            System.out.println("Error setting up TCP server socket.");
        }
    }

    // Closes server socket and all client threads
    public void close()
    {
        try {
            serverSocket.close();

            for(ServerThread thread : threads) 
            { 
                thread.interrupt();
            }
        } 
        catch (IOException e){
            System.out.println("Error closing TCP server socket.");
        }
    }

    // Broadcasts a message to all connected clients (except the client that sent the message)
    public void broadcast(String message, int clientID)
    {
        for(ServerThread thread : threads) 
        { 
            if(thread.getClientID() != clientID) 
            { 
                thread.sendMessage(message); 
            } 
        }
    }
    
    // Broadcasts a message to all connected clients (except the client that sent the message)
    public void request(String message, int clientID)
    {
        for(ServerThread thread : threads) 
        { 
            if(thread.getClientID() == clientID) 
            { 
                thread.sendMessage(message); 
            } 
        }
    }

    // Remove a client thread from the thread list
    public void removeThread(ServerThread thread)
    {
        threads.remove(thread);
    }
}
