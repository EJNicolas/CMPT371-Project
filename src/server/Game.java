/* 
 *  Game.java
 * 
 *  Description: This class is implemented as a singleton, and handles server side game logic.
 *               Functions provided below are designed to be called from ServerThread class.
 *               Each element in the array represents a square on the game board.
 *               0 = empty square
 *               -n = a player with ID n is currently drawing on this square
 *               n = a player with ID n of the player who owns this square
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
    private int[][] gameboard;

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

    // Initialize board data
    private void initializeBoard()
    {
        gameboard = new int[BOARD_ROWS][BOARD_COLS];
        for(int i = 0; i < BOARD_ROWS; i++)
        {
            for(int j = 0; j < BOARD_COLS; j++)
            {
                gameboard[i][j] = 0;
            }
        }
    }

    // Getters
    public int getPlayerCount() { return playerCount; }

    // Gets game data instance, creates one if it doesn't exist
    public static Game getInstance()
    {
        if (instance == null) { instance = new Game(); }
        return instance;
    }

    // Add a player given playerID and returns the total number of players in game
    public int addPlayer(int playerID)
    {
        playerCount++;
        playerScores.add(playerID, 0);
        return playerCount;
    }

    // Check if a player can draw on square
    // Returns 1 if player can draw, 0 if the square is already occupied, 
    // -1 if square is being drawn on by another player
    public int canDraw(int row, int column)
    {
        if(gameboard[row][column] == 0) 
            return 1; 
        else if(gameboard[row][column] < 0) 
            return -1;
        else 
            return 0;
    }

    // Set a square to be drawn on by a player
    public void startDrawing(int row, int column, int playerID)
    {
        gameboard[row][column] = -playerID;
    }

    // Claim a square for a player, returns true if successful, false if the square invalid
    public boolean claimSquare(int row, int column, int playerID)
    {
        if(gameboard[row][column] == -playerID)
        {
            gameboard[row][column] = playerID;
            playerScores.set(playerID, playerScores.get(playerID) + 1);
            this.free--;
            return true;
        }
        return false;
    }

    // Check if a player has won the game, returns the playerID if they have, -1 if not
    public int checkWin()
    {
        int playerID = -1;
        int max = 0;

        if(this.free == 0)
        {
            for(int i = 0; i < this.playerCount; i++)
            {
                if(playerScores.get(i) > max)
                {
                    max = playerScores.get(i);
                    playerID = i;
                }
            }
        }
        return playerID;
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
                if(gameboard[i][j] == 0) { free++; }
                else { occupied++; }
                System.out.print(gameboard[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("=======================================");
        System.out.println("Occupied squares: " + occupied + ", Free squares: " + free);
    }
}
