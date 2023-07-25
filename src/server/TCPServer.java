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
    }

    // Starts server, listens for clients and creates a thread for each client
    public void startServer()
    {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP server listening on port " + port + ".");

            while(true)
            {
                Socket client = serverSocket.accept();
                Runnable serverThread = new ServerThread(client);
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
}
