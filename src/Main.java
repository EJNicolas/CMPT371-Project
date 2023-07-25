import javax.swing.JFrame;

import server.*;

public class Main {
    public static final String HOST = "localhost";
    public static final int PORT = 5000;

	public static void main(String[] args) {
		JFrame frame = new JFrame("CMPT 371 Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Screen s = new Screen();
        frame.add(s);
        frame.pack();
        frame.setVisible(true);

        new TCPServer(PORT).startServer();
	}

}
