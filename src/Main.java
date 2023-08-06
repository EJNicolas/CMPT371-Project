import javax.swing.JFrame;

import server.*;

public class Main {
    public static final String HOST = "localhost";
    public static final int PORT = 5000;

	public static void main(String[] args) {

        new TCPServer(PORT).startServer();
	}

}
