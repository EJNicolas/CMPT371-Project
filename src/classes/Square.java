/*
 * 	Square.java
 * 	Description: Keeps track of a square's fields.
 * 				 Has an ArrayList of dots which represent the dots that are on this square.
 * 				 Keeps track of the player who owns the square if there is one.
 * 				 Draws the square and its dots on the screen.
 * 
 */

package classes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Square {
	int[] pos = new int[2];
	int l;
	boolean locked;
	boolean canBeDrawn;
	Player owner;
	ArrayList<Dot> dots = new ArrayList<Dot>();
	double fillPercentageReq = 0.7;
	
	public Square(int x, int y, int l){
		pos[0] = x;
		pos[1] = y;
		this.l = l;
		locked = false;
		canBeDrawn = true;
		owner = null;
	}
	
	public void draw(Graphics g) {
		Color oldColor = g.getColor();
		
		if(locked) {
			g.setColor(owner.getColor());
			g.fillRect(pos[0],pos[1],l,l);
			
		} else{
			g.setColor(Color.black);
			g.drawRect(pos[0],pos[1],l,l);
			
			for(int i=0; i<dots.size();i++) {
				dots.get(i).draw(g);
			}
		}
		
		g.setColor(oldColor);
	}
	
	public void setOwner(Player p) {
		owner = p;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public void setLocked(boolean b) {
		locked = b;
	}
	
	public boolean getLocked() {
		return locked;
	}
	
	public void setCanBeDrawn(boolean b) {
		canBeDrawn = b;
	}
	
	public boolean getCanBeDrawn() {
		return canBeDrawn;
	}
	
	public void lockSquare(Player player) {
		locked = true;
		owner = player;
		canBeDrawn = false;
	}
	
	public void clearSquare() {
		locked = false;
		owner = null;
		canBeDrawn = true;
		dots.clear();
	}
	
	public void addDot(Dot d) {
		dots.add(d);
	}
	
	public boolean checkCollision(int x, int y) {
		if(pos[0] < x && x < pos[0] + l && pos[1] < y && y < pos[1] + l) return true;
		else return false;
	}
	
	
    //this method checks if the dots drawn by a player occupy at least 50% of the square's area*/
	
	public boolean checkDotsArea() {
		Set<Point> uniquePoints = new HashSet<>();
	
		for(Dot dot : dots) {
			
			int[] posD = dot.getPos();
			
			if(checkCollision(posD[0], posD[1])) {  // check if dot is inside square
				
				for(int i = -dot.radius; i < dot.radius; i++) { 
					
					for(int j = -dot.radius; j < dot.radius; j++) {                
						
						if(i*i + j*j <= dot.radius * dot.radius) {  // ensures we are checking the actual circular area of a dot drawn
							
							int x = posD[0] + i;
							int y = posD[1] + j;
							if(checkCollision(x, y)) {
								uniquePoints.add(new Point(x, y));
							}
						}
					}
				}
			}
		}
		double squareArea = l * l;
		double dotsArea = uniquePoints.size(); // 1 dot = 1 point
	
		if(dotsArea > squareArea * 0.5) return true;  // check if dots cover more than 50% of the square
		else return false;
	}
	
	

}
