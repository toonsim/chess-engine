package Game;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Command-line interface for the chess game.
 * Handles all user interaction and delegates game logic to the Game class.
 *
 */
public class ChessCLI {
    
    public static void main(String[] args) {
        ChessCLI cli = new ChessCLI();
        cli.run();
    }
    
    private final Scanner scanner = new Scanner(System.in);
    
    /**
     * Main entry point for the CLI chess application.
     */
    public void run() {
        System.out.print(System.lineSeparator());
        Game game = createGame();
        long startTime = System.nanoTime();
        playGame(game);
        long duration = System.nanoTime() - startTime;
        handleGameEnd(game, duration);
    }
    
    /**
     * Create a new game with user-specified settings.
     */
    private Game createGame() {
        System.out.print("White controlled? ");
        boolean whiteControlled = scanner.nextBoolean();
        System.out.print("Black controlled? ");
        boolean blackControlled = scanner.nextBoolean();
        
        int whiteDifficulty = 3;
        int blackDifficulty = 3;
        
        if (!whiteControlled) {
            System.out.print("White difficulty? ");
            whiteDifficulty = scanner.nextInt();
        }
        if (!blackControlled) {
            System.out.print("Black difficulty? ");
            blackDifficulty = scanner.nextInt();
        }
        
        return new Game(whiteControlled, blackControlled, whiteDifficulty, blackDifficulty);
    }
    
    /**
     * Play the game until completion.
     */
    private void playGame(Game game) {
        while (!game.isGameOver()) {
            if (game.getPlayerAtPlay().isControlled()) {
                handleHumanTurn(game);
            } else {
                game.playATurn();
            }
            if (game.getLastPlayedOperation() != null) {
                System.out.println(game.getLastPlayedOperation());
            }
        }
    }
    
    private void handleHumanTurn(Game game) {
        boolean validMove = false;
        while (!validMove) {
            System.out.print("Enter starting position: ");
            String start = scanner.next().toUpperCase();
            
            if (start.equals("UNDO")) {
                if (game.undoMove()) {
                    validMove = true;
                } else {
                    System.out.println("Cannot undo move.");
                }
            } else if (start.equals("RESIGN")) {
                if (game.resignPlayer()) {
                    validMove = true;
                } else {
                    System.out.println("Cannot resign.");
                }
            } else {
                System.out.print("Enter ending position: ");
                String end = scanner.next();
                if (game.makeMove(start, end)) {
                    validMove = true;
                } else {
                    System.out.println("Wrong input, try again!");
                }
            }
        }
    }
    
    /**
     * Handle game end - display results and offer to save.
     */
    private void handleGameEnd(Game game, long durationNanos) {
        long minutes = TimeUnit.NANOSECONDS.toMinutes(durationNanos);
        long seconds = TimeUnit.NANOSECONDS.toSeconds(durationNanos) - minutes * 60;
        String duration = minutes + "m" + seconds + "s";
        
        System.out.println(duration);
        if (game.getGameEndMessage() != null) {
            System.out.println(game.getGameEndMessage());
        }
        
        System.out.print("Do you want to save this match? (y/n): ");
        String response = scanner.next().toLowerCase();
        if (response.equals("y") || response.equals("yes")) {
            GameSaver.saveGame(game, duration);
        }
    }
    
    
    
}