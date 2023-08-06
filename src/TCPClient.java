import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class TCPClient implements Screen.GameOverListener {
    private Socket clientSocket;

    public static void main(String[] args) {
        new TCPClient().start();
    }

    public void start() {
        try {
            // Connect to the server (replace "localhost" and 5000 with your server's IP and port)
            clientSocket = new Socket("localhost", 5000);
            System.out.println("Connected to server.");

            // Initialize input and output streams
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Read the client ID from the server
            String clientID = in.readLine();
            System.out.println("Received client ID from server: " + clientID);

            JFrame frame = new JFrame("CMPT 371 Project");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Screen s = new Screen(Integer.parseInt(clientID), out, in);

            JLabel clientIdLabel = new JLabel("Client ID: " + clientID);
            clientIdLabel.setBounds(10, 10, 200, 20);
            s.add(clientIdLabel);

            frame.add(s);
            frame.pack();
            frame.setVisible(true);

            // Send a message back to the server (optional)
            out.println("Thank you for the client ID!");

            // Set the game over listener to the TCPClient instance
            s.setGameOverListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGameOver() {
        closeSocket();
    }

    private void closeSocket() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Socket closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
