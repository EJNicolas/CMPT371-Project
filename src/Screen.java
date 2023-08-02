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

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import classes.*;

public class Screen extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
	private Timer timer;
	public final static int SCREEN_WIDTH = 1600;
	public final static int SCREEN_HEIGHT= 900;
	
	int playerCount = 1;
	Player localPlayer;
	ArrayList<Player> players = new ArrayList<Player>();
	boolean isDrawing = false;
	Square currSquare = null;
	int mousePosX, mousePosY;
	
	
	public static final int GRID_ROWS = 8;
	public static final int GRID_COLUMNS = 15;
	public static final int SQUARE_LENGTH = 100;
	
	ArrayList<Square> squares = new ArrayList<Square>();
	
	//constructor for screen
	Screen(int clientID){
		playerCount = clientID;
		timer = new Timer(5,this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setLayout(null);
		
		//should probably add these 3 lines to its own method to initialize the game
		localPlayer = new Player(playerCount, 30, Color.red);
		players.add(localPlayer);
		createGrid();
		
		timer.start();
	}
	
	//method that runs according to the timer. Practically keeps track of the mouse movements
	@Override
	public void actionPerformed(ActionEvent e) {
	
		//draw screen
		repaint();
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for(int i=0; i<players.size();i++) {
			players.get(i).draw(g);
		}
		
		for(int i=0; i<squares.size();i++) {
			squares.get(i).draw(g);
		}
	}
	
	//creates a grid of squares
	void createGrid() {
		for(int i=100; i<SQUARE_LENGTH * GRID_COLUMNS; i+=SQUARE_LENGTH) {
			for(int q=100; q<SQUARE_LENGTH * GRID_ROWS; q+=SQUARE_LENGTH) {
				squares.add(new Square(i,q,SQUARE_LENGTH));
			}
		}
	}
	
	//returns the square the given position is in. Null if in no square
	Square findCurrSquare(int x, int y) {
		for(int i=0; i<squares.size(); i++) {
			if(squares.get(i).checkCollision(x, y)) {
				return squares.get(i);
			}
		}
		
		return null;
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
				if(currSquare.checkDotsArea()) currSquare.lockSquare(localPlayer);
				else currSquare.unlockSquare();
			}
		}
	
		localPlayer.setPosition(mousePosX, mousePosY);
	}
	
	//Callback events for mouse listener
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	//On mouse press
	@Override
	public void mousePressed(MouseEvent e) {
		isDrawing = true;
		currSquare = findCurrSquare(mousePosX, mousePosY);
		
		if(currSquare != null) {
			currSquare.setOwner(localPlayer);
		}
	}
	
	//On mouse release
	@Override
	public void mouseReleased(MouseEvent e) {
		isDrawing = false;
		
		if(currSquare != null) {
			currSquare.addDot(new Dot(localPlayer, mousePosX, mousePosY));
			
			if(currSquare.checkDotsArea()) currSquare.lockSquare(localPlayer);
			else currSquare.unlockSquare();
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
	

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}



	
	

}
