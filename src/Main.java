import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame("CMPT 371 Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Screen s = new Screen();
        frame.add(s);
        frame.pack();
        frame.setVisible(true);

	}

}
