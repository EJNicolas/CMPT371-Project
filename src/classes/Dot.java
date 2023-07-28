/*
 * 	Dot.java
 * 	Description: Keeps track of a dot's fields.
 * 				 Keeps track of the player who owns the dot and uses its color.
 * 				 Draws the dot.
 * 
 */

package classes;
import java.awt.Color;
import java.awt.Graphics;

public class Dot {
	
	int[] pos = new int[2];
	Player owner;
	Color color;
	int radius;
	
	public Dot(Player owner, double x, double y){	// changed x y to double for new checkDotsArea()
		this.owner = owner;
		pos[0] = (int)x;
		pos[1] = (int)y;
		color = owner.getColor();
		radius = 10;
	}
	
	public void draw(Graphics g) {
		Color oldColor = g.getColor();
		
		g.setColor(color);
		g.fillOval(pos[0]-radius/2, pos[1]-radius/2, radius, radius);
		
		g.setColor(oldColor);
	}
	
	public int[] getPos() {
		return pos;
	}
	
	
}
