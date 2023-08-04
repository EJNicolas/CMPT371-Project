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
import java.awt.Point;

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

    // Client and server data
    private int clientID;
    private ArrayList<String> parsedMessage;
    private Game game = Game.getInstance();

    // Constructor
    public ServerThread(Socket client, int clientID, TCPServer server) {
        this.client = client;
        this.clientID = clientID;
        this.server = server;
        this.parsedMessage = new ArrayList<String>();

        setupStreams();
        sendClientID();
        game.addPlayer(clientID);

        // Run rejoin protocol if game has already started
        if(game.gameStarted()) { rejoin(); }

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
        server.broadcast("PlayerJoined " + clientID , clientID);
        
        while(true) 
        { 
            if(interrupt) 
            { 
                close(); 
                break; 
            }
            readToken(); 
            checkWin();
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
                        case "CanDraw":
                            int x = Integer.parseInt(parsedMessage.get(2));
                            int y = Integer.parseInt(parsedMessage.get(3));

                            if(game.canDraw(x, y))
                            {
                                game.startDrawing(x, y, clientID);
                                out.println("true");
                                server.broadcast("IsDrawing " + x + " " + y, clientID);
                            }
                            else
                            {
                                out.println("false");
                            }
                            break;
                        case "ClaimSquare":
                            x = Integer.parseInt(parsedMessage.get(2));
                            y = Integer.parseInt(parsedMessage.get(3));

                            if(game.claimSquare(x, y, clientID)) 
                            { 
                                out.println("true"); 
                                server.broadcast("Occupied " + clientID + " " + x + " " + y, clientID);
                            }
                            else 
                            { 
                                out.println("false"); 
                            }
                            break;
                            
                        case "DrawFail":
                            x = Integer.parseInt(parsedMessage.get(2));
                            y = Integer.parseInt(parsedMessage.get(3));

                            if(game.stopDrawing(x, y, clientID))
                            {
                                out.println("true");
                                server.broadcast("StoppedDrawing " + x + " " + y, clientID);
                            }
                            else
                            {
                                out.println("false");
                            }
                            break;
                            
                        case "RequestPlayerList":
                        	server.request("GetPlayerList " + game.getPlayerCount(), clientID);
                        	break;
                            
                        case "UpdatePosition":
                        	x = Integer.parseInt(parsedMessage.get(2));
                        	y = Integer.parseInt(parsedMessage.get(3));
                        	
                        	server.broadcast("MovePlayer " + clientID + " " + x + " " + y, clientID);
                        	break;
                        	
                        case "DrawDot":
                        	x = Integer.parseInt(parsedMessage.get(2));
                        	y = Integer.parseInt(parsedMessage.get(3));
                        	int squareX = Integer.parseInt(parsedMessage.get(4));
                        	int squareY = Integer.parseInt(parsedMessage.get(5));
                        	server.broadcast("ShowDot " + clientID + " " + x + " " + y + " " + squareX + " " + squareY , clientID);
                        	break;
                        	
                        case "Disconnect":
                            game.disconnectPlayer(clientID);
                            server.broadcast("PlayerDisconnect " + clientID, clientID);
                            server.removeThread(this);
                            interrupt();
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

    // Check if a player has won
    public void checkWin() {
        int winnerID = game.checkWin();
        if(winnerID != -1)
        {
            server.broadcast("GameOver " + winnerID, clientID);
        }
    }

    // Rejoin protocol: send this client the current game state
    public void rejoin()
    {
        int[][] gameboard = game.getGameboard();
        int playerCount = game.getPlayerCount();
        ArrayList<Point> claimedSquares = new ArrayList<Point>();

        for(int playerID = 1; playerID < playerCount; playerID++)
        {
            for(int i = 0; i < Game.BOARD_ROWS; i++)
            {
                for(int j = 0; j < Game.BOARD_COLS; j++)
                {
                    if(gameboard[i][j] == playerID)
                    {
                        claimedSquares.add(new Point(i, j));
                    }
                }
            }
            String message = "Rejoin " + playerID + " ";
            for(Point square : claimedSquares)
            {
                message += square.x + " " + square.y + " ";
            }
            sendMessage(message);
            claimedSquares.clear();
        }
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
