/*
 * 	Screen.java
 * 	Description: Implements JPanel to create a window users can interact with.
 * 				 Contains events to track the local player's mouse controls.
 * 				 Draws elements onto the screen from paintComponent function.
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
		
		//should probably add these 3 lines to its own method to initialize the game
		localPlayer = new Player(playerCount, 30);
		players.add(localPlayer);
		createGrid();
		
		timer.start();
	}
	
	//method that runs according to the timer.
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if(receiveStream.ready()) {
				parseMessage(receiveStream.readLine());
				//System.out.println("client received message: " + parsedMessage.get(0));
				switch(parsedMessage.get(0)) {
					case "IsDrawing":
						//int playerNum = Integer.parseInt(parsedMessage.get(1));
	                    int x = Integer.parseInt(parsedMessage.get(1));
	                    int y = Integer.parseInt(parsedMessage.get(2));
	                    Square square = board[x][y];
	                    square.setCanBeDrawn(false);
	                    break;
	                    
					case "Occupied":
						int playerNum = Integer.parseInt(parsedMessage.get(1));
	                    x = Integer.parseInt(parsedMessage.get(2));
	                    y = Integer.parseInt(parsedMessage.get(3));
						square = board[x][y];
	                    square.lockSquare(localPlayer);
	                    break;
				}
			}
			
			
			
		} catch (Exception err) {
			System.out.println("Error receiving stream");
			err.printStackTrace();
		}
		
		//draw screen
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
	
    //------------------------------------------------------CALL BACKS--------------------------------------
	//Callback events for mouse listener
	
	//On mouse press
	@Override
	public void mousePressed(MouseEvent e) {
		isDrawing = true;
		currSquare = findCurrSquare(mousePosX, mousePosY);
		
		if(currSquare != null && currSquare.getCanBeDrawn() && !currSquare.getLocked()) {
			//currSquare.setOwner(localPlayer);
			sendStream.println("CanDraw " + playerCount + " " + currSquareIndex[0] + " " + currSquareIndex[1]);
		}
	}
	
	//On mouse release
	@Override
	public void mouseReleased(MouseEvent e) {
		isDrawing = false;
		
		if(currSquare != null) {
			currSquare.addDot(new Dot(localPlayer, mousePosX, mousePosY));
			
			if(currSquare.checkDotsArea()) {
				currSquare.lockSquare(localPlayer);
				sendStream.println("ClaimSquare " + playerCount + " " + currSquareIndex[0] + " " + currSquareIndex[1]);
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
				if(currSquare.checkDotsArea()) {
					currSquare.lockSquare(localPlayer);
					sendStream.println("ClaimSquare " + playerCount + " " + currSquareIndex[0] + " " + currSquareIndex[1]);
					
				} else {
					currSquare.clearSquare();
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
