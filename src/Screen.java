/*
 *  Screen.java
 *  Description: Implements JPanel to create a window users can interact with.
 *               Contains events to track the local player's mouse controls.
 *               Draws elements onto the screen from paintComponent function.
 *
 */


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import classes.*;
import server.Game;


public class Screen extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    private static Screen instance = null;
   
    private Timer timer;
    public final static int SCREEN_WIDTH = 1600;
    public final static int SCREEN_HEIGHT= 900;
   
    int playerCount = 1;
    Player localPlayer;
    ArrayList<Player> players = new ArrayList<Player>();
    boolean isDrawing = false;
    Square currSquare = null;
    int mousePosX, mousePosY;
   
    private static final int THROTTLE_THRESHOLD = 5;
    private int lastSentX, lastSentY;
   
    public static final int GRID_ROWS = 7;
    public static final int GRID_COLUMNS = 14;
    public static final int SQUARE_LENGTH = 100;
   
    ArrayList<Square> squares = new ArrayList<Square>();
    Square board[][];
    int[] currSquareIndex = new int[2];
   
    PrintWriter sendStream;
    BufferedReader receiveStream;
   
    ArrayList<String> parsedMessage;
   
    //constructor for screen
    Screen(int clientID, PrintWriter send, BufferedReader receive){
        playerCount = clientID;
        sendStream = send;
        receiveStream = receive;
        parsedMessage = new ArrayList<String>();
        timer = new Timer(5,this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setLayout(null);
       
        //should probably add these lines to its own method to initialize the game
        localPlayer = new Player(playerCount, 30);
        players.add(localPlayer);
        createGrid();
       
        //ask server to get list of existing players for players joining mid game
        sendStream.println("RequestPlayerList " + playerCount);
       
       
        timer.start();
    }
   
    //method that runs according to the timer.
    @Override
    public void actionPerformed(ActionEvent e) {
       
        // Only send update if mouse has moved more than the threshold
        if (Math.abs(mousePosX - lastSentX) > THROTTLE_THRESHOLD ||
            Math.abs(mousePosY - lastSentY) > THROTTLE_THRESHOLD) {
            sendStream.println("UpdatePosition " + playerCount + " " + mousePosX + " " + mousePosY);
            lastSentX = mousePosX;
            lastSentY = mousePosY;
            
            if(isDrawing && currSquare != null && currSquare.getCanBeDrawn() && !currSquare.getLocked()) {
            	sendStream.println("DrawDot " + playerCount + " " + mousePosX + " " + mousePosY + " " + currSquareIndex[0] + " " +currSquareIndex[1]);
            }
        }

        new Thread(() -> {
            try {
                if(receiveStream.ready()) {
                    parseMessage(receiveStream.readLine());
                //System.out.println("client received message: " + parsedMessage.get(0));
                switch(parsedMessage.get(0)) {
                    case "IsDrawing":
                        //the square on this x, y position in the 2d array will not be allowed to be drawn on
                        int x = Integer.parseInt(parsedMessage.get(1));
                        int y = Integer.parseInt(parsedMessage.get(2));
                        Square square = board[x][y];
                        square.setCanBeDrawn(false);
                        break;
                       
                    case "Occupied":
                        //the square on this x, y position in the 2d array will not be locked by playerNum
                        int playerNum = Integer.parseInt(parsedMessage.get(1));
                        x = Integer.parseInt(parsedMessage.get(2));
                        y = Integer.parseInt(parsedMessage.get(3));
                        square = board[x][y];
                        Player player = getPlayerFromNumber(playerNum);
                        if(player != null) square.lockSquare(player);
                        break;
                       
                    case "PlayerJoined":
                        //Add the new player to the list of players (for people already in the game)
                        playerNum = Integer.parseInt(parsedMessage.get(1));
                        players.add(new Player(playerNum, 30));
                        break;
                       
                    case "GetPlayerList":
                        //Get the server's player list and update this player's list
                        int playerNumbers = Integer.parseInt(parsedMessage.get(1));
                        for(int i=1; i<playerNumbers; i++) {
                            if(i != playerCount) {
                                players.add(new Player(i, 30));
                            }
                        }
                        break;
                       
                    case "MovePlayer":
                        //moves a player to x and y position
                        playerNum = Integer.parseInt(parsedMessage.get(1));
                        x = Integer.parseInt(parsedMessage.get(2));
                        y = Integer.parseInt(parsedMessage.get(3));
                        for(int i=1; i<players.size(); i++) {
                            if(players.get(i).getPlayerNum() == playerNum) {
                                players.get(i).setPosition(x,y);
                            }
                        }
                        break;
                        
                    case "ShowDot":
                    	playerNum = Integer.parseInt(parsedMessage.get(1));
                    	x = Integer.parseInt(parsedMessage.get(2));
                        y = Integer.parseInt(parsedMessage.get(3));
                        int squareX = Integer.parseInt(parsedMessage.get(4));
                        int squareY = Integer.parseInt(parsedMessage.get(5));
                        square = board[squareX][squareY];
                        player = getPlayerFromNumber(playerNum);
                        if(player != null) square.addDot(new Dot(player, x, y));
                        break;
                        
                    case "StoppedDrawing":
                    	x = Integer.parseInt(parsedMessage.get(1));
                        y = Integer.parseInt(parsedMessage.get(2));
                        square = board[x][y];
                        square.clearSquare();
                    	break;
                }
            }
        } catch (Exception err) {
            System.out.println("Error receiving stream");
            err.printStackTrace();
        }
    }).start();




    // Draw screen
    repaint();
}

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
       
        for(int i = 0; i < GRID_ROWS; i++) {
            for(int j = 0; j < GRID_COLUMNS; j++) {
                board[i][j].draw(g);
            }
        }
       
        for(int i=0; i<players.size();i++) {
            players.get(i).draw(g);
        }
       
    }
   
    //creates a grid of squares
    void createGrid() {
        board = new Square[GRID_ROWS][GRID_COLUMNS];
        for(int i = 0; i < GRID_ROWS; i++) {
            for(int j = 0; j < GRID_COLUMNS; j++) {
                board[i][j] = new Square(100 + j*SQUARE_LENGTH, 100 + i*SQUARE_LENGTH, SQUARE_LENGTH);
            }
        }
    }
   
    //returns the square the given position is in. Null if in no square
    Square findCurrSquare(int x, int y) {
        for(int i = 0; i < GRID_ROWS; i++) {
            for(int j = 0; j < GRID_COLUMNS; j++) {
                if(board[i][j].checkCollision(x,y)) {
                    currSquareIndex[0] = i;
                    currSquareIndex[1] = j;
                    return board[i][j];
                }
            }
        }
        return null;
    }
   


   
    public void addPlayer(int num) {
        players.add(new Player(num, 30));
    }
   
    void parseMessage(String message)
    {
        parsedMessage.clear();
        String[] tokens = message.split(" ");


        for(String token : tokens)
        {
            parsedMessage.add(token);
        }
    }
    
    Player getPlayerFromNumber(int n) {
    	for(int i=1; i<players.size(); i++) {
            if(players.get(i).getPlayerNum() == n) {
            	return players.get(i);
            }
        }
    	
    	return null;
    }
   
    //------------------------------------------------------CALL BACKS--------------------------------------
    //Callback events for mouse listener
   
    //On mouse press
    @Override
    public void mousePressed(MouseEvent e) {
        isDrawing = true;
        //on mouse click, find what square you are on
        currSquare = findCurrSquare(mousePosX, mousePosY);
       
        //tell server if it can draw on this square. Square can be drawn on and isnt owned by anyone
        if(currSquare != null && currSquare.getCanBeDrawn() && !currSquare.getLocked()) {
            sendStream.println("CanDraw " + playerCount + " " + currSquareIndex[0] + " " + currSquareIndex[1]);
        }
    }
   
    //On mouse release
    @Override
    public void mouseReleased(MouseEvent e) {
        isDrawing = false;
       
        if(currSquare != null) {
            currSquare.addDot(new Dot(localPlayer, mousePosX, mousePosY));
           
            //if dots fill enough of the square, give the square to this player. Tell the server about it too
            if(currSquare.checkDotsArea() && !currSquare.getLocked()) {
                currSquare.lockSquare(localPlayer);
                sendStream.println("ClaimSquare " + playerCount + " " + currSquareIndex[0] + " " + currSquareIndex[1]);
            } else if(currSquare.getCanBeDrawn()) { //prevents players from drawing on already claimed squares
                currSquare.clearSquare();
                sendStream.println("DrawFail " + playerCount + " " + currSquareIndex[0] + " " + currSquareIndex[1]);
            }
        }
    }
   
    @Override
    public void mouseDragged(MouseEvent e) {
        trackMouse(e);
    }


    @Override
    public void mouseMoved(MouseEvent e) {
        trackMouse(e);
    }
   
    void trackMouse(MouseEvent e) {
        mousePosX = e.getX();
        mousePosY = e.getY();
   
        if (isDrawing) {
            if (currSquare != null) {
                currSquare.addDot(new Dot(localPlayer, mousePosX, mousePosY));
            }
           
            //if the player's mouse moves out of the current box, check if they can claim the square
            if(currSquare != findCurrSquare(mousePosX, mousePosY)) {    
                if(currSquare.checkDotsArea() && !currSquare.getLocked()) {
                    currSquare.lockSquare(localPlayer);
                    sendStream.println("ClaimSquare " + playerCount + " " + currSquareIndex[0] + " " + currSquareIndex[1]);
                } else if(currSquare.getCanBeDrawn()){  //prevents the player from clearing a claimed square
                    currSquare.clearSquare();
                    sendStream.println("DrawFail " + playerCount + " " + currSquareIndex[0] + " " + currSquareIndex[1]);
                    isDrawing = false;
                }
               
            }
        }
   
        localPlayer.setPosition(mousePosX, mousePosY);
    }
   
    @Override
    public void mouseClicked(MouseEvent e) {}


    @Override
    public void mouseEntered(MouseEvent e) {}


    @Override
    public void mouseExited(MouseEvent e) {}






   
   


}
