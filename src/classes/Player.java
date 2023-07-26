/*
 * 	Player.java
 * 	Description: Keeps track of a player's fields
 * 
 */

package classes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

public class Player {
	
	int[] pos = new int[2];
	int radius;
	int playerNum;
	Color color;
	int score;
	Image pencilImage;
	static Color lavender = new Color(147, 122, 219);
	// Define player colors
	private static final String[] playerColors = {"red", "lavender", "green", "plum", "skyblue", "yellow"};
	private static final Color[] playerColorObjects = {Color.RED, lavender, Color.GREEN, Color.PINK, Color.BLUE, Color.YELLOW};
	
	public Player(int playerNum, int radius, Color color){
		this.playerNum = playerNum;
		this.radius = radius;
		this.color = playerColorObjects[playerNum-1];
		pos[0] = 0;
		pos[1] = 0;
		score = 0;

		try {
			// Use playerNum to select the appropriate color for the pencil image
			String colorName = playerColors[playerNum-1];
			pencilImage = ImageIO.read(new File("../CMPT371-Project/res/pencil-" + colorName + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics g) {
		if (pencilImage != null) {
			int shift = 20;  
			int scaledRadius = radius * 2;  
			g.drawImage(pencilImage, pos[0] - scaledRadius/2 + shift, pos[1] - scaledRadius/2 - shift, scaledRadius, scaledRadius, null);
		}
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
