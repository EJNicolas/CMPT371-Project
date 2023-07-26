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
import java.awt.Color;
import java.awt.Graphics;

public class Square {
	int[] pos = new int[2];
	int l;
	boolean locked;
	Player owner;
	ArrayList<Dot> dots = new ArrayList<Dot>();
	double fillPercentageReq = 0.7;
	
	public Square(int x, int y, int l){
		pos[0] = x;
		pos[1] = y;
		this.l = l;
		locked = false;
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
	
	public void lockSquare(Player player) {
		locked = true;
		owner = player;
	}
	
	public void unlockSquare() {
		locked = false;
		owner = null;
		dots.clear();
	}
	
	public void addDot(Dot d) {
		dots.add(d);
	}
	
	public boolean checkCollision(int x, int y) {
		if(pos[0] < x && x < pos[0] + l && pos[1] < y && y < pos[1] + l) return true;
		else return false;
	}
	
	/*
	 * this is checking the average position of all of the dots in the square and finding the dot farthest away from the center of the square. 
	 * with this, it calculates an area of a circle using that avg dot as a center, and the distance from the farthest dot the radius
	 * 
	 * if you want to easily take a square, make a line across the square
	 */
	public boolean checkDotsArea() {
		int xSum = 0; 
		int ySum = 0;
		
		double radius = 0;
		
		for(int i=0; i<dots.size();i++) {
			int[] posD = dots.get(i).getPos();
			xSum += posD[0];
			ySum += posD[1];
			
			double newRadius = Math.hypot(posD[0] - (pos[0] + l/2), posD[1] - (pos[1] + l/2));
			if(newRadius > radius) radius = newRadius;
		}
		
		int avgX = xSum / dots.size();
		int avgY = ySum / dots.size();
		double dotsArea =  Math.PI * radius * radius;
		
		if(dotsArea > l * l * fillPercentageReq) return true;
		else return false;
	}
	
	

}
