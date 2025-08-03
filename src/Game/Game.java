package Game;

import Game.GameParts.Moves.*;
import Game.GameParts.Pieces.*;
import Game.GameParts.Player;
import Game.GameParts.Position;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * A class of chess games.
 *
 * Created by Koen on 6/11/2016.
 */
public class Game {

    public Game() {
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j)
                this.positions[i + 8*j] = new Position(new int[] {i, j});
        System.out.print("White controlled? ");
        boolean whiteControlled = (new Scanner(System.in)).nextBoolean();
        this.white = new Player(whiteControlled, true, this);
        System.out.print("Black controlled? ");
        boolean blackControlled = (new Scanner(System.in)).nextBoolean();
        this.black = new Player(blackControlled, false, this);
        this.initialiseBoard();
    }

    public Game(int difficulties) {
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j)
                this.positions[i + 8*j] = new Position(new int[] {i, j});
        this.white = new Player(true, this, difficulties);
        this.black = new Player(false, this, difficulties);
        this.initialiseBoard();
    }

    public static void main(String[] args) {
////        Game.testGame();
//        System.out.print("Autoplay? ");
//        boolean autoPlay = (new Scanner(System.in)).nextBoolean();
//        if (autoPlay) {
//            Game.autoPlayer();
//        }
//        else {
        System.out.print(System.lineSeparator());
        Game game = new Game();
        long startTime = System.nanoTime();
        Game.playGame(game);
        long minutes = TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - startTime);
        long seconds = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime) - TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - startTime) * 60;
        String duration = minutes + "m" + seconds + "s";
        System.out.println(duration);
        System.out.print("Do you want to save this match? ");
        if ((new Scanner(System.in)).nextBoolean()) {
            GameSaver.saveGame(game, duration);
        }
//        }
    }

    private static void autoPlayer() {
        System.out.println("Runtime: ");
        int max = (new Scanner(System.in)).nextInt();
        System.out.println("Player levels: ");
        int difficulties = (new Scanner(System.in)).nextInt();
        long begin = TimeUnit.NANOSECONDS.toHours(System.nanoTime());
        int number = 1;
        while (TimeUnit.NANOSECONDS.toHours(System.nanoTime()) - begin < max) {
            System.out.print(System.lineSeparator() + "------------------------------------------------------" + System.lineSeparator() +
                    System.lineSeparator() + "Game " + number + System.lineSeparator()
                    + System.lineSeparator() + "------------------------------------------------------" + System.lineSeparator());
            number++;
            System.out.println(System.lineSeparator() + "### NEW GAME ###" + System.lineSeparator());
            Game game = new Game(difficulties);
            long startTime = System.nanoTime();
            Game.playGame(game);
            long minutes = TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - startTime);
            long seconds = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime) - TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - startTime) * 60;
            String duration = minutes + "m" + seconds + "s";
            System.out.println(duration);
            GameSaver.saveGame(game, duration);
            System.out.println(System.lineSeparator() + "### GAME OVER ###" + System.lineSeparator());
        }
    }

//    private static void testGame() {
//        Game game = new Game(true);
//        Pawn testPawnB = new Pawn(game.black, game.getPosition(7, 6));
//        Pawn testPawnW = new Pawn(game.white, game.getPosition(5, 4));
//        game.update();
//        while (!game.isGameOver()) {
//            System.out.println("testPawnB positions: ");
//            testPawnB.getPossibleActions().forEach(positions1 -> System.out.println(positions1[1]));
//            System.out.println("testPawnW positions: ");
//            testPawnW.getPossibleActions().forEach(positions1 -> System.out.println(positions1[1]));
////            System.out.println("testPawnW reach: ");
////            testPawnW.getReach().forEach(System.out::println);
//            System.out.println("Possible en passant: ");
//            System.out.println(game.getPossibleEnPassant());
//            game.playATurn();
//        }
//    }
//
//    public Game(boolean test) {
//        for (int i = 0; i < 8; ++i)
//            for (int j = 0; j < 8; ++j)
//                this.positions[i + 8*j] = new Position(new int[] {i, j});
//        this.white = new Player(true, true, this);
//        this.black = new Player(true, false, this);
////        SETTING UP THE PAWNS
//        for (int i = 1; i < 4; ++i) {
//            new Pawn(this.white, this.getPosition(i, 1));
//            new Pawn(this.black, this.getPosition(i+3, 6));
//        }
////        SETTING UP THE ROOKS
//        Rook whiteRookA = new Rook(this.white, this.getPosition(0, 0));
//        Rook whiteRookH = new Rook(this.white, this.getPosition(7, 0));
//        Rook blackRookA = new Rook(this.black, this.getPosition(0, 7));
//        Rook blackRookH = new Rook(this.black, this.getPosition(7, 7));
////        SETTING UP THE KINGS
//        new King(this.white, this.getPosition(4, 0), whiteRookA, whiteRookH);
//        new King(this.black, this.getPosition(4, 7), blackRookA, blackRookH);
////        INITIALISING MOVES
////        System.out.println(testPawnW.getReach());
//    }

    private static void playGame(Game game) {
        while (!game.isGameOver()) {
            game.playATurn();
        }
    }

    private boolean isGameOver() {
        return gameOver;
    }

    private void setGameOver() {
        this.gameOver = true;
    }

    private boolean gameOver = false;

    /**
     * Returns the action played at a given turn.
     * Be careful: the turn is not equal to the amount of moves played, but to how many moves were played up to the turn.
     */
    public Move getPlayedAction(int turn) {
        if (turn < 0 || turn > this.getTurn()) {
            throw new IllegalArgumentException();
        }
        return this.playedActions.get(turn);
    }

    /**
     * Return an arraylist containing all the actions played in this game so far.
     */
    public ArrayList<Move> getPlayedActions() {
        ArrayList<Move> actions = new ArrayList<>();
        actions.addAll(this.playedActions);
        return actions;
    }

    /**
     * An arraylist containing all the actions played in this game so far.
     */
    private final ArrayList<Move> playedActions = new ArrayList<>();

    private int getTurnsToBeRewound() {
        return turnsToBeRewinded;
    }

    public void setTurnsToBeRewound(int turnsToBeRewound) {
        if (!(turnsToBeRewound >= 0 && turnsToBeRewound <= this.getTurn())) {
            throw new IllegalArgumentException();
        }
        this.turnsToBeRewinded = turnsToBeRewound;
    }

    /**
     * Variable registering how many turns this game has to be rewinded.
     */
    private int turnsToBeRewinded = 0;

    /**
     * Initialise all the pieces of this chess game on their starting positions.
     */
    private void initialiseBoard(){
//        SETTING UP THE PAWNS
        for (int i = 0; i < 8; ++i) {
            new Pawn(this.white, this.getPosition(i, 1));
            new Pawn(this.black, this.getPosition(i, 6));
        }
//        SETTING UP THE ROOKS
        Rook whiteRookA = new Rook(this.white, this.getPosition(0, 0));
        Rook whiteRookH = new Rook(this.white, this.getPosition(7, 0));
        Rook blackRookA = new Rook(this.black, this.getPosition(0, 7));
        Rook blackRookH = new Rook(this.black, this.getPosition(7, 7));
//        SETTING UP THE KNIGHTS
        new Knight(this.white, this.getPosition(1, 0));
        new Knight(this.white, this.getPosition(6, 0));
        new Knight(this.black, this.getPosition(1, 7));
        new Knight(this.black, this.getPosition(6, 7));
//        SETTING UP THE BISHOPS
        new Bishop(this.white, this.getPosition(2, 0));
        new Bishop(this.white, this.getPosition(5, 0));
        new Bishop(this.black, this.getPosition(2, 7));
        new Bishop(this.black, this.getPosition(5, 7));
//        SETTING UP THE QUEENS
        new Queen(this.white, this.getPosition(3, 0));
        new Queen(this.black, this.getPosition(3, 7));
//        SETTING UP THE KINGS
        new King(this.white, this.getPosition(4, 0), whiteRookA, whiteRookH);
        new King(this.black, this.getPosition(4, 7), blackRookA, blackRookH);
//        INITIALISING MOVES
        this.update();
    }

    /**
     * Play a turn.
     */
    private void playATurn() {
        TurnAction(this.getPlayerAtPlay());
        this.endTurn();

//        this.getPlayers().forEach(player -> System.out.println(player.toString() + " " + player.evaluate()));

    }

    /**
     * End the turn.
     */
    private void endTurn() {
//        for (Player player : this.getPlayers()) {
//            player.updatePossibleActions();
//        }

//        // TESTING THE UNDO FUNCTION
//        if (this.getTurn() > 6 && Math.random() <0.2) {
//            this.setTurnsToBeRewound(4);
//            System.out.println("TRIGGERED");
//        }

        for (int i = 0; i < this.getTurnsToBeRewound(); i++) {
            this.playedActions.get(playedActions.size() - 1).undo();
            this.playedActions.remove(playedActions.size() - 1);
        }
        this.setTurn(this.getPlayedActions().size());
        this.setTurnsToBeRewound(0);

        this.gameOverChecker();
    }

    /**
     * Return whether or not the game is over, either by a player winning or the game going in remise.
     * If the game is in remise, but the amount of actions in the loop is not equal to 4, it will not be detected.
     */
    private void gameOverChecker() {
        for (Player player: this.getPlayers()) {
            if (player.hasResigned()) {
                this.winner = player.getOpponent();
            }
            // Outdated method
            if (!player.getOpponent().getKing().isAlive()) {
                this.winner = player;
            }
        }
//        // Check for checkmate
//        if (this.getPlayerAtPlay().negaMax(2).getScore() < -Math.pow(10,10)) {
//            this.winner = this.getPlayerAtPlay().getOpponent();
//        }
        if (this.winner != null){
            this.setGameOver();
            this.setTurnsToBeRewound(0);
            System.out.println(this.winner.getName() + " wins after " + this.getTurn() + " turns");
            return;
        }
        // Check for remise
        if (this.getTurn() < 10) {
            return;
        }
        for (int i = 1; i < 4; i++) {
            if (!this.getPlayedAction(this.getTurn() - i)
                    .equals(this.getPlayedAction(this.getTurn() - i - 4))) {
                return;
            }
        }
        System.out.println("Game goes in remise after " + this.getTurn() + " turns");
        this.setGameOver();
        this.setTurnsToBeRewound(8);
    }


    Player getWinner() {
        return winner;
    }

    private Player winner = null;


    public Position getPosition(int x, int y) {
        return this.positions[x + 8*y];
    }

    private final Position[] positions = new Position[64];


    public Position getPossibleEnPassant() {
        return possibleEnPassant;
    }

    /**
     * Changes the possible en passant to the given position.
     */
    public void setPossibleEnPassant(Position possibleEnPassant) {
        this.possibleEnPassant = possibleEnPassant;
    }

    private Position possibleEnPassant = null;


    public Player getWhite() {
        return white;
    }

    private final Player white;

    public Player getBlack() {
        return black;
    }

    private final Player black;


    /**
     * Return an arraylist containing the white player and the black player of this game, in that order.
     */
    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(Arrays.asList(this.white, this.black));
    }


    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getTurn() {
        return this.turn;
    }

    /**
     * A variable registering the turn.
     * The turn of a game is equal to the amount of actions that have been played up to this point.
     */
    private int turn = 0;

    /**
     * Return whether the given game is in endgame.
     * A game is in endgame when there are less than 7 pieces on the board, excluding pawns and kings.
     */
    public Boolean isEndGame() {
        return this.getPlayers().stream().mapToInt(player -> (int) player.getPieces().stream()
                .filter(piece -> !((piece instanceof Pawn) || (piece instanceof King))).count()).sum() < 7;
    }

    /**
     * Updates the sets of positions the pieces can move to, using the method "updatePiece" from the class "Player".
     * Only the pieces who can reach the given positions have to update.
     * If one of the kings is dead, this method does nothing.
     * The kings are updated last.
     *
     * @param positions
     *          The given positions. This usually corresponds to the last action played.
     */
    public void update(Position[] positions) {
        for (Player player: this.getPlayers()) {
            for (Piece piece: player.getPieces()) {
                if (Arrays.stream(positions).anyMatch(position -> (piece.getReach().contains(position)
                        && !(piece instanceof King)))) {
                    player.updatePiece(piece);
                }
            }
        }
        this.getPlayers().stream().filter(player -> player.getKing().isAlive())
                .forEach(player -> player.updatePiece(player.getKing()));
    }

    /**
     * Updates the sets of positions the pieces can move to, using the method "updatePiece" from the class "Player".
     * If one of the kings is dead, this method does nothing.
     * The kings are updated last.
     */
    public void update() {
        for (Player player: this.getPlayers()) {
            for (Piece piece: player.getPieces()) {
                if (!(piece instanceof King)) {
                    player.updatePiece(piece);
                }
            }
        }
        this.getPlayers().stream().filter(player -> player.getKing().isAlive())
                .forEach(player -> player.updatePiece(player.getKing()));
    }

    /**
     * Updates the sets of positions the pawns can move to, using the method "updatePiece" from the class "Player".
     */
    public void updatePawns() {
        for (Player player: this.getPlayers()) {
            for (Piece piece: player.getPieces()) {
                if (piece instanceof Pawn) {
                    player.updatePiece(piece);
                }
            }
        }
    }

    /**
     * Updates the sets of positions for all the pieces except the pawns can move to, using the method "updatePiece"
     * from the class "Player".
     * The kings are updated last.
     */
    public void updateWithoutPawns(Position[] positions) {
        for (Player player: this.getPlayers()) {
            for (Piece piece: player.getPieces()) {
                if (!(piece instanceof Pawn || piece instanceof King)) {
                    if (Arrays.stream(positions).anyMatch(position -> piece.getReach().contains(position))) {
                        player.updatePiece(piece);
                    }
                }
            }
        }
        this.getPlayers().stream().filter(player -> player.getKing().isAlive())
                .forEach(player -> player.updatePiece(player.getKing()));
    }

    /**
     * Updates the sets of positions the kings can move to, using the method "updatePiece" from the class "Player".
     */
    public void updateKings() {
        this.getPlayers().stream().filter(player -> player.getKing().isAlive())
                .forEach(player -> player.updatePiece(player.getKing()));
    }

    /**
     * Updates the sets of positions for all the pieces except the kings can move to, using the method "updatePiece"
     * from the class "Player".
     */
    public void updateWithoutKings(Position[] positions) {
        for (Player player: this.getPlayers()) {
            for (Piece piece: player.getPieces()) {
                if (!(piece instanceof King)) {
                    if (Arrays.stream(positions).anyMatch(position -> piece.getReach().contains(position))) {
                        player.updatePiece(piece);
                    }
                }
            }
        }
    }


    /**
     * Return the player whose turn it is.
     */
    public Player getPlayerAtPlay() {
        return this.getPlayers().get(this.turn % 2);
    }

    /**
     * Return the player whose turn it was at a given turn.
     */
    public Player getPlayerAtPlay(int turn) {
        return this.getPlayers().get(turn % 2);
    }


    /**
     * Determines the action the active player makes
     *
     * @param playingPlayer
     *          The player who's next action is determined.
     */
    private void TurnAction(Player playingPlayer){
        Operation operation = null;
        while (operation == null || !operation.isValidActionForPlayer(playingPlayer)) {
            if (playingPlayer.isControlled()) {
                operation = this.inputConverter(playingPlayer);
            }
            else {
                int depth = playingPlayer.getDifficulty()
                        + Math.max(0, 8 - (this.getPlayers().stream().mapToInt(player -> (int) player.getPieces().stream()
                        .filter(piece -> !(piece instanceof Pawn)).count()).sum()
                        + this.getPlayers().stream().mapToInt(player -> (int) player.getPieces().stream()
                        .filter(piece -> piece instanceof Pawn).count()).sum() / 3));
                NegaMaxReturn output = playingPlayer.alphaBeta(Double.NEGATIVE_INFINITY,
                        Double.POSITIVE_INFINITY, depth);
                Position[] result = output.getAction();
                if (result[0].getOccupyingPiece() instanceof King &&
                        Math.abs(result[1].getCoordinates()[0]
                                - result[0].getCoordinates()[0]) > 1.1) { // Check if output is castle move
                    operation = new Castle(result);
                } else if (result[0].getOccupyingPiece() instanceof Pawn // Check if output is en passant move
                        && result[1].getCoordinates()[1] == result[0].getCoordinates()[1] + ((Pawn) result[0].getOccupyingPiece()).getDirection()
                        && Math.abs(result[1].getCoordinates()[0] - result[0].getCoordinates()[0]) > 0.5
                        && Piece.nooneOnTile(result[1])) {
                    operation = new EnPassant(result);
                } else {
                    operation = new Move(result);
                }

            }
        }
//        try {
            operation.execute();
            System.out.println(operation);
            if (operation instanceof Move) {
                this.playedActions.add((Move) operation);
            }
//        } catch (Exception e) {
//            System.out.println(Arrays.toString(e.getStackTrace()));
//            this.ArnoldStyle(playingPlayer);
//        }
    }

    private Operation inputConverter(Player playingPlayer) {
        System.out.print("Enter starting position: ");
        String start = (new Scanner(System.in)).next().toUpperCase();
        if (start.equals("UNDO")) {
            return new Undo(this);
        }
        else if (start.equals("RESIGN")) {
            return new Resign(this.getPlayerAtPlay());
        }
        System.out.print("Enter ending position: ");
        String end = (new Scanner(System.in)).next();
        try {
            Move move;
            Position startingPosition = this.getPosition((start.charAt(0) - 'A'), start.charAt(1) - '1');
            Position endPosition = this.getPosition((end.charAt(0) - 'A' ), end.charAt(1) - '1');
            if (startingPosition.getOccupyingPiece() instanceof King &&
                    Math.abs(endPosition.getCoordinates()[0]
                            - startingPosition.getCoordinates()[0]) > 1.1) {
                move = new Castle(startingPosition, endPosition);
            }
            else if (startingPosition.getOccupyingPiece() instanceof Pawn
                    && endPosition.getCoordinates()[1] == startingPosition.getCoordinates()[1] + ((Pawn) startingPosition.getOccupyingPiece()).getDirection()
                    && Math.abs(endPosition.getCoordinates()[0] - startingPosition.getCoordinates()[0]) > 0.5
                    && Piece.nooneOnTile(endPosition)) {
                move = new EnPassant(startingPosition, endPosition);
            }
            else {
                move = new Move(startingPosition, endPosition);
            }
            if (!move.isValidActionForPlayer(playingPlayer)) {
                throw new IllegalArgumentException();
            }
            return move;
        } catch (Exception e) {
            System.out.println("Wrong input, try again!");
            return inputConverter(playingPlayer);
        }
    }

}