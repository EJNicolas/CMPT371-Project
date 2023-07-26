package classes;
import java.awt.Color;
import java.awt.Graphics;

public class Dot {
	
	int[] pos = new int[2];
	Player owner;
	Color color;
	int radius;
	
	public Dot(Player owner, int x, int y){
		this.owner = owner;
		pos[0] = x;
		pos[1] = y;
		color = owner.getColor();
		radius = 10;
	}
	
	public void draw(Graphics g) {
		Color oldColor = g.getColor();
		
		g.setColor(color);
		g.fillOval(pos[0]-radius/2, pos[1]-radius/2, radius, radius);
		
		g.setColor(oldColor);
	}
	
	
}
