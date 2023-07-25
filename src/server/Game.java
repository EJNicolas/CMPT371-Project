/*
 *  This class is implemented as a singleton
 *  Each element in the array represents a square on the game board.
 *  0 = empty square
 *  -n = a player with ID n is currently drawing on this square
 *  n = a player with ID n of the player who owns this square
*/

package server;

import java.util.ArrayList;

public class Game 
{
    // Board configurations
    public static final int BOARD_ROWS = 8;
    public static final int BOARD_COLS = 18;
    public static final int BOARD_TOTAL = BOARD_ROWS * BOARD_COLS;
    private int free = BOARD_TOTAL;
    private int[][] board;

    // Player configurations
    private int playerCount = 0;
    private ArrayList<Integer> playerScores = new ArrayList<Integer>();

    // Setup
    private static Game instance = null;

    // Constructor (Singletons have a private constructor that creates a global instance on get_instance())
    private Game()
    {
        initializeBoard();
    }

    // Getters
    public int getPlayerCount() { return playerCount; }

    // Gets game data instance, creates one if it doesn't exist
    public static Game getInstance()
    {
        if (instance == null) { instance = new Game(); }
        return instance;
    }

    // Initialize board data
    private void initializeBoard()
    {
        board = new int[BOARD_ROWS][BOARD_COLS];
        for(int i = 0; i < BOARD_ROWS; i++)
        {
            for(int j = 0; j < BOARD_COLS; j++)
            {
                board[i][j] = 0;
            }
        }
    }

    // Debug: Print current gameboard state
    public void printGameBoard()
    {
        int occupied = 0;
        int free = 0;

        System.out.println("=======================================");
        for(int i = 0; i < BOARD_ROWS; i++)
        {
            System.out.print("| ");
            for(int j = 0; j < BOARD_COLS; j++)
            {
                if(board[i][j] == 0) { free++; }
                else { occupied++; }
                System.out.print(board[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("=======================================");
        System.out.println("Occupied squares: " + occupied + ", Free squares: " + free);
    }
}
