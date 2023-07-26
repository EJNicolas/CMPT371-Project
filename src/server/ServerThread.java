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

public class ServerThread implements Runnable
{
    private Socket client;
    private OutputStream os;
    private InputStream is;
    private PrintWriter out;
    private BufferedReader in;
    private boolean interrupt = false;

    // Constructor
    public ServerThread(Socket client)
    {
        this.client = client;
    }

    // Thread run method
    @Override
    public void run()
    {
        System.out.println("TCP client connected.");
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
                    switch(in.readLine())
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

    public void interrupt()
    {
        interrupt = true;
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
