package classes;
import java.awt.Color;
import java.awt.Graphics;

public class Player {
	
	int[] pos = new int[2];
	int radius;
	int playerNum;
	Color color;
	int score;
	
	public Player(int playerNum, int radius, Color color){
		this.playerNum = playerNum;
		this.radius = radius;
		this.color = color;
		pos[0] = 0;
		pos[1] = 0;
		score = 0;
	}
	
	public void draw(Graphics g) {
		Color oldColor = g.getColor();
		
		g.setColor(color);
		g.fillOval(pos[0] - radius/2, pos[1] - radius/2, radius, radius);
		
		g.setColor(oldColor);
	}
	
	public void setPosition(int x, int y){
		pos[0] = x;
		pos[1] = y;
	}
	
	public int[] getPosition() {
		return pos;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getPlayerNum(){
		return playerNum;
	}
}
