import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;


public class Screen extends JPanel implements ActionListener {
	
	public final static int SCREEN_WIDTH = 1200;
	public final static int SCREEN_HEIGHT= 800;
	
	Screen(){
		setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setLayout(null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void paintComponent(Graphics g) {
		
	}
	
	

}
