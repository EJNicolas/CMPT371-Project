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
	
	

}
