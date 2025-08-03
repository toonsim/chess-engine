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

    /**
     * Create a new chess game.
     *
     * @param whiteControlled Whether white player is human-controlled (true) or AI (false)
     * @param blackControlled Whether black player is human-controlled (true) or AI (false)
     * @param whiteDifficulty AI difficulty for white player (ignored if whiteControlled=true)
     * @param blackDifficulty AI difficulty for black player (ignored if blackControlled=true)
     */
    public Game(boolean whiteControlled, boolean blackControlled, int whiteDifficulty, int blackDifficulty) {
        // Initialize board positions
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                this.positions[i + 8*j] = new Position(new int[] {i, j});
            }
        }
        
        // Create players
        this.white = new Player(whiteControlled, true, this, whiteDifficulty);
        this.black = new Player(blackControlled, false, this, blackDifficulty);
        
        // Set up the board
        this.initialiseBoard();
    }

    /**
     * Convenience constructor with default difficulty (3) for AI players.
     */
    public Game(boolean whiteControlled, boolean blackControlled) {
        this(whiteControlled, blackControlled, 3, 3);
    }

    /**
     * Convenience constructor for AI vs AI games with same difficulty.
     */
    public Game(int difficulty) {
        this(false, false, difficulty, difficulty);
    }


    public void playGame() {
        while (!this.isGameOver()) {
            this.playATurn();
        }
    }

    public boolean isGameOver() {
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
     * Play a turn for AI players only.
     */
    public void playATurn() {
        if (this.getPlayerAtPlay().isControlled()) {
            throw new IllegalStateException("Current player is human-controlled. Use makeMove() instead.");
        }
        Operation operation = this.getAIPlayerAction(this.getPlayerAtPlay());
        operation.execute();
        this.lastPlayedOperation = operation;
        if (operation instanceof Move) {
            this.playedActions.add((Move) operation);
        }
        this.endTurn();
    }

    /**
     * Make a move for a human player.
     * @param startPos Starting position (e.g., "A1")
     * @param endPos Ending position (e.g., "B2")
     * @return true if move was successful, false if invalid
     */
    public boolean makeMove(String startPos, String endPos) {
        if (!this.getPlayerAtPlay().isControlled()) {
            throw new IllegalStateException("Current player is AI-controlled. Use playATurn() instead.");
        }
        try {
            Operation operation = createMoveFromPositions(startPos, endPos);
            if (operation.isValidActionForPlayer(this.getPlayerAtPlay())) {
                operation.execute();
                this.lastPlayedOperation = operation;
                if (operation instanceof Move) {
                    this.playedActions.add((Move) operation);
                }
                this.endTurn();
                return true;
            }
        } catch (Exception e) {
            // Invalid move
        }
        return false;
    }

    /**
     * Perform undo operation.
     * @return true if undo was successful
     */
    public boolean undoMove() {
        if (!this.getPlayerAtPlay().isControlled()) {
            return false;
        }
        try {
            Operation undo = new Undo(this);
            if (undo.isValidActionForPlayer(this.getPlayerAtPlay())) {
                undo.execute();
                this.lastPlayedOperation = undo;
                this.endTurn();
                return true;
            }
        } catch (Exception e) {
            // Undo failed
        }
        return false;
    }

    /**
     * Resign current player.
     * @return true if resignation was successful
     */
    public boolean resignPlayer() {
        if (!this.getPlayerAtPlay().isControlled()) {
            return false;
        }
        try {
            Operation resign = new Resign(this.getPlayerAtPlay());
            resign.execute();
            this.lastPlayedOperation = resign;
            this.endTurn();
            return true;
        } catch (Exception e) {
            // Resignation failed
        }
        return false;
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
            this.gameEndMessage = this.winner.getName() + " wins after " + this.getTurn() + " turns";
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
        this.gameEndMessage = "Game goes in remise after " + this.getTurn() + " turns";
        this.setGameOver();
        this.setTurnsToBeRewound(8);
    }


    public Player getWinner() {
        return winner;
    }

    public String getGameEndMessage() {
        return gameEndMessage;
    }

    private Player winner = null;
    private String gameEndMessage = null;


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


    private Operation createMoveFromPositions(String startPos, String endPos) {
        startPos = startPos.toUpperCase();
        endPos = endPos.toUpperCase();
        
        Position startingPosition = this.getPosition((startPos.charAt(0) - 'A'), startPos.charAt(1) - '1');
        Position endPosition = this.getPosition((endPos.charAt(0) - 'A'), endPos.charAt(1) - '1');
        
        if (startingPosition.getOccupyingPiece() instanceof King &&
                Math.abs(endPosition.getCoordinates()[0]
                        - startingPosition.getCoordinates()[0]) > 1.1) {
            return new Castle(startingPosition, endPosition);
        }
        else if (startingPosition.getOccupyingPiece() instanceof Pawn
                && endPosition.getCoordinates()[1] == startingPosition.getCoordinates()[1] + ((Pawn) startingPosition.getOccupyingPiece()).getDirection()
                && Math.abs(endPosition.getCoordinates()[0] - startingPosition.getCoordinates()[0]) > 0.5
                && Piece.nooneOnTile(endPosition)) {
            return new EnPassant(startingPosition, endPosition);
        }
        else {
            return new Move(startingPosition, endPosition);
        }
    }

    private Operation getAIPlayerAction(Player playingPlayer) {
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
            return new Castle(result);
        } else if (result[0].getOccupyingPiece() instanceof Pawn // Check if output is en passant move
                && result[1].getCoordinates()[1] == result[0].getCoordinates()[1] + ((Pawn) result[0].getOccupyingPiece()).getDirection()
                && Math.abs(result[1].getCoordinates()[0] - result[0].getCoordinates()[0]) > 0.5
                && Piece.nooneOnTile(result[1])) {
            return new EnPassant(result);
        } else {
            return new Move(result);
        }
    }


    public Operation getLastPlayedOperation() {
        return lastPlayedOperation;
    }

    private Operation lastPlayedOperation = null;


}