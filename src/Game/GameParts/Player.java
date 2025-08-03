package Game.GameParts;

import Game.Game;
import Game.GameParts.Moves.NegaMaxReturn;
import Game.GameParts.Pieces.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class of chess players.
 *
 * Created by Koen on 6/11/2016.
 */
public class Player {

    /**
     * Initiate a new Player.
     *
     * @param   controlled
     *              Whether or not this new player will be controlled, or be operated by a computer algorithm.
     * @param   white
     *              Whether or not this player is the white player.
     * @param   game
     *              The game in which this player plays.
     */
    public Player(boolean controlled, boolean white, Game game) {
        this.controlled = controlled;
        this.white = white;
        this.game = game;
        this.opponent = this.getGame().getPlayers().stream().filter(player -> player != this).collect(Collectors.toList()).get(0);
        if (this.opponent != null)
            this.opponent.opponent = this;
        if (this.isWhite()) {
            this.colour = "White";
        }
        else {
            this.colour = "Black";
        }
        if (this.isControlled()) {
            System.out.print("Name of player: ");
            this.name = (new Scanner(System.in)).nextLine();
        }
        else {
            this.name = this.getColour();
            while (!this.isValidDifficulty(this.getDifficulty())) {
                System.out.print(this.getName() + " difficulty? ");
                try {
                    this.setDifficulty((new Scanner(System.in)).nextInt());
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public Player(boolean white, Game game, int difficulty) {
        if (!this.isValidDifficulty(difficulty)) {
            throw new IllegalArgumentException();
        }
        this.controlled = false;
        this.white = white;
        this.game = game;
        this.opponent = this.getGame().getPlayers().stream().filter(player -> player != this).collect(Collectors.toList()).get(0);
        if (this.opponent != null)
            this.opponent.opponent = this;
        this.difficulty = difficulty;
        if (this.isWhite()) {
            this.colour = "White";
        }
        else {
            this.colour = "Black";
        }
        this.name = this.getColour();
    }

    /**
     * Return this player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Variable registering this player's name. A player's name can't be changed.
     */
    private final String name;

    /**
     * Return this player's colour.
     */
    public String getColour() {
        return colour;
    }

    /**
     * Variable registering this player's colour. A player's colour can't be changed.
     */
    private final String colour;

    /**
     * Return whether or not this player is operated by a person playing the game, or by the computer.
     */
    public boolean isControlled() {
        return controlled;
    }

    /**
     * Variable registering whether or not this player is operated by a person playing the game, or by the computer.
     */
    private final boolean controlled;


    /**
     * Return the difficulty of this player.
     */
    public int getDifficulty() {
        return this.difficulty;
    }

    /**
     * Set the difficulty of this player to the given value.
     */
    public void setDifficulty(int difficulty) {
        if (!this.isValidDifficulty(difficulty)) {
            throw new IllegalArgumentException("That's not a valid difficulty setting!");
        }
        this.difficulty = difficulty;
    }

    /**
     * Return whether the given difficulty is a valid difficulty for this player.
     */
    private boolean isValidDifficulty(int difficulty) {
        return this.isControlled() || difficulty >= 1;
    }

    /**
     * Variable registering the difficulty of this player.
     * If this player is not controlled by the computer, it's difficulty is zero.
     */
    private int difficulty = 0;


    /**
     * Return whether this player is a white player.
     */
    public boolean isWhite() {
        return white;
    }

    /**
     * Variable registering whether this player is a white player.
     */
    private final boolean white;


    /**
     * Return the game of this player.
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * The game this player is playing.
     */
    private final Game game;


    /**
     * Add a piece to the pieces controlled by this player.
     */
    public void addPiece(Piece piece) throws IllegalArgumentException {
        if (piece.getPlayer() != this)
            throw new IllegalArgumentException();
        this.pieces.add(piece);
        this.valuePieces += piece.getValue();
//        this.addToActionMap(piece.getPossibleActions());
    }

    /**
     * Remove a piece from the pieces controlled by this player.
     */
    public void removePiece(Piece piece) throws IllegalArgumentException {
        if (piece.getPlayer() != this)
            throw new IllegalArgumentException();
        if (!this.pieces.contains(piece))
            throw new IllegalStateException();
        this.removeFromActionMap(piece.getPossibleActions());
        this.pieces.remove(piece);
        this.valuePieces -= piece.getValue();
    }

    /**
     * Return a list of the pieces controlled by this player.
     */
    public ArrayList<Piece> getPieces() {
        return this.pieces;
    }

    /**
     * A list of the pieces controlled by this player.
     */
    private final ArrayList<Piece> pieces = new ArrayList<>();


    /**
     * The value of the pieces of this player.
     */
    private double valuePieces = 0.0;


    /**
     * Return the value of the pieces of this player.
     */
    public double getValuePieces() {
        return this.valuePieces;
    }

    /**
     * Set the value of the pieces of this player.
     */
    public void setValuePieces(double valuePieces) {
        this.valuePieces = valuePieces;
    }

    /**
     * Return the king of this player.
     */
    public King getKing() {
        return this.king;
    }

    /**
     * Set the given king as the king of this player.
     */
    public void setKing(King king) {
        if (this.getKing() != null) {
            throw new IllegalStateException();
        }
        this.king = king;
        this.valuePieces += king.castlingValue();
    }

    /**
     * The king of this player.
     */
    private King king = null;


    /**
     * Return whether or not this player has resigned.
     */
    public Boolean hasResigned() {
        return resigned;
    }

    /**
     * Make this player resign.
     */
    public void resign() {
        this.resigned = true;
    }

    /**
     * Whether or not this player has resigned.
     */
    private Boolean resigned = false;

    /**
     * Return what kind of piece this player wants to replace a pawn that reached the edge of the board.
     */
    public Piece prefferedPawnReplacement(Position position) {
        Piece piece;
        if (!this.getGame().getPlayerAtPlay(this.getGame().getPlayedActions().size()).isControlled()) {
            piece = new Queen(this, position);
        }
        else {
            System.out.println("Your pawn reached the other end! You can replace it with a:");
            System.out.println(
                    "Queen - type 1" + System.getProperty("line.separator") +
                            "Rook - type 2" + System.getProperty("line.separator") +
                            "Bishop - type 3" + System.getProperty("line.separator") +
                            "Knight - type 4" + System.getProperty("line.separator"));
            int choice = (new Scanner(System.in)).nextInt();
            while (choice < 1 || choice > 4) {
                System.out.println("Please chose a valid option: ");
                choice = (new Scanner(System.in)).nextInt();
            }
            switch (choice) {
                case 1:
                    piece = new Queen(this, position);
                    break;
                case 2:
                    piece = new Rook(this, position);
                    break;
                case 3:
                    piece = new Bishop(this, position);
                    break;
                case 4:
                    piece = new Knight(this, position);
                    break;
                default:
                    piece = this.prefferedPawnReplacement(position);
                    break;
            }
        }
        piece.updatePossibleActions();
        return piece;
    }

    /**
     * Return this player's opponent.
     */
    public Player getOpponent(){
        return this.opponent;
    }

    /**
     * This player's opponent.
     */
    private Player opponent;


    /**
     * Return whether or not this player can reach the given position.
     */
    public boolean canReachPosition(Position position) {
        return this.getPieces().stream().anyMatch(piece -> piece.getReach().contains(position));
    }


    /**
     * Return the actions this player could execute, if (s)he were to play.
     * This list is based on the player's actionMap, and should be kept up to date using the method
     * "updatePossibleActions".
     */
    private ArrayList<Position[]> getPossibleActions() {
        return new ArrayList<>(this.actionMap.keySet());
    }

    /**
     * Sorts the given list of actions according to how beneficial they are to this player.
     */
    private ArrayList<Position[]> sortPossibleActions( ArrayList<Position[]> possibleActions) {
        if (possibleActions.contains(null)) {
            System.out.println("NULL ACTION DETECTED");
        }
//        Collections.shuffle(possibleActions);     // Allows for some randomness in the AI behavior
        for (double value : this.actionMap.values()) {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                System.out.println("aha");
                throw new IllegalStateException();
            }
        }
        possibleActions.sort((o1, o2) -> (int) ((this.actionMap.get(o2) - this.actionMap.get(o1)) * 10000.0));
        return possibleActions;
    }

    /**
     * Remove the given actions from this player's actionMap.
     */
    private void removeFromActionMap(Set<Position[]> actions) {
//        if (actions.stream().anyMatch(action ->
//                this.actionMap.keySet().stream().noneMatch(action1 -> Arrays.equals(action,action1)))) {
//            Position[] wrongAction = null;
//            for (Position[] action : actions) {
//                if (this.actionMap.keySet().stream().noneMatch(action1 -> Arrays.equals(action,action1))) {
//                    wrongAction = action;
//                    break;
//                }
//            }
//            assert wrongAction != null;
//            System.out.println("ActionMap not up to date / wrong action for " + this.toString());
//            System.out.println("King alive: " + this.getKing().isAlive());
//            System.out.println("Moved piece: " + wrongAction[0].getOccupyingPiece());
//            System.out.println("Now: " + wrongAction[0].toString());
//            System.out.println("New: " + wrongAction[1].toString());
//            System.out.println("Current occupant: " + wrongAction[1].getOccupyingPiece());
//            System.out.println("Mover's possibilities: ");
//            for (Position[] position1: wrongAction[0].getOccupyingPiece().getPossibleActions())
//                System.out.print(Arrays.toString(position1));
//            System.out.println();
//            System.out.println("Mover's reach: ");
//            for (Position position1: wrongAction[0].getOccupyingPiece().getReach())
//                System.out.print(position1 + " ");
//            System.out.println();
//            throw new IllegalArgumentException();
//        }
        this.actionMap.keySet().removeAll(actions);
    }

    /**
     * Add the given actions to this player's actionMap.
     */
    private void addToActionMap(Set<Position[]> actions) {
        if (actions.stream().anyMatch(this.actionMap::containsKey)) {
            throw new IllegalArgumentException();
        }
        actions.forEach(action -> this.actionMap.put(action, this.getActionValue(action)));
    }

    /**
     * A Hashmap containing the action this player can execute and the corresponding estimated actionValues.
     */
    private final HashMap<Position[], Double> actionMap = new HashMap<>();

    /**
     * Updates the given piece.
     * The given piece should belong to this player.
     */
    public void updatePiece(Piece piece) {
        if (piece.getPlayer() != this) {
            throw new IllegalArgumentException();
        }
        this.removeFromActionMap(piece.getPossibleActions());
        piece.updatePossibleActions();
        try {
            this.addToActionMap(piece.getPossibleActions());
        } catch (Exception e) {
            System.out.println(piece.toString() + " at " + piece.getPosition().toString());
            System.out.println(piece.getPossibleActions().size());
            for (Position[] positions: piece.getPossibleActions())
                System.out.print(Arrays.toString(positions) + " ");
            throw e;
        }
    }


    /**
     * Provide a string representation of this player.
     */
    @Override
    public String toString() {
        if (!this.isControlled())
            return "PC " + this.getName();
        return this.getName();
    }

    /**
     * The most basic NegaMax algorithm.
     * This is only used to check whether the game is over, with the depth set to 2.
     *
     * @param   depth
     *              How many moves deep the algorithm is.
     * @return  Returns a NegaMaxReturn, which contains an action and a score. The returned action is the one with the best score.
     */
    public NegaMaxReturn negaMax(int depth) {
        if (!this.getKing().isAlive()) {
            return new NegaMaxReturn(-Math.pow(10,25+depth));
        }
        if ( depth == 0 ) {
            double result = this.evaluate();
            return new NegaMaxReturn(result);
        }
        NegaMaxReturn max = new NegaMaxReturn(Double.NEGATIVE_INFINITY);
        ArrayList<Position[]> possibleActions = this.sortPossibleActions(this.getPossibleActions());
        Position erasedEnPassant = this.getGame().getPossibleEnPassant();
        Piece movedPiece;
        Piece killedPiece;
        for (Position[] action : possibleActions)  {
            movedPiece = action[0].getOccupyingPiece();
            killedPiece = this.execute(action);
            NegaMaxReturn score = this.getOpponent().negaMax(depth - 1);
            this.undo(action, erasedEnPassant, movedPiece, killedPiece);
            if (score.getScore() > max.getScore()) {
                max = score;
                max.setAction(action);
            }
        }
        return max;
    }

    public NegaMaxReturn alphaBeta(double alpha, double beta, int depth) {
        if (!this.getKing().isAlive()) {
            return new NegaMaxReturn(-Math.pow(10,25+depth));
        }
        Position erasedEnPassant = this.getGame().getPossibleEnPassant();
        Piece movedPiece;
        Piece killedPiece;
        NegaMaxReturn possibleMove;
        NegaMaxReturn max = new NegaMaxReturn(alpha);
        ArrayList<Position[]> possibleActions = this.sortPossibleActions(this.getPossibleActions());
        for (Position[] action : possibleActions)  {

            movedPiece = action[0].getOccupyingPiece();

            killedPiece = this.execute(action);
            if (depth > 1) {
                try {
                    possibleMove = this.getOpponent().alphaBeta(-beta, -max.getScore(), depth - 1);
                } catch (Exception e) {
                    System.out.println(Arrays.toString(action));
                    throw e;
                }
            }
            else {
                possibleMove = this.getOpponent().quiesce(-beta, -max.getScore());
            }
            this.undo(action, erasedEnPassant, movedPiece, killedPiece);

            possibleMove.flipScore();

            if (possibleMove.getScore() >= beta) {
                return new NegaMaxReturn(beta);   //  fail hard beta-cutoff
            }
            if (possibleMove.getScore() > max.getScore()) {
                max = possibleMove; // alpha acts like max in MiniMax
                max.setAction(action);
            }

            if (this.getPossibleActions().size() != possibleActions.size()) {
                System.out.println("PROBLEM at " + depth);
                System.out.println(movedPiece + " " + Arrays.toString(action));
                System.out.println(possibleActions.size() - this.getPossibleActions().size());
                ArrayList<Position[]> newActions = this.getPossibleActions();
                this.getGame().update();
                System.out.println(newActions.size() - this.getPossibleActions().size());
                newActions.stream()
                        .filter(positions -> !positions[0].getOccupyingPiece().canHaveAsPosition(positions[1]))
                        .forEach(positions -> System.out.println(Arrays.toString(positions)));
                System.out.println();
                this.getPossibleActions().stream()
                        .filter(positions -> possibleActions.stream().noneMatch(positions1 -> Arrays.equals(positions,positions1)))
                        .forEach(positions -> System.out.println(Arrays.toString(positions) + " " + positions[0].getOccupyingPiece()));
                throw new IllegalStateException();
            }

        }
        return max;
    }

    private NegaMaxReturn quiesce(double alpha, double beta ) {
        if (!this.getKing().isAlive()) {
            return new NegaMaxReturn(-Math.pow(10,13));
        }
        ArrayList<Position[]> possibleActions = this.getPossibleActions();
        // If the King is not in check, quiet moves are removed, and the alpha value is set to the maximum of the given
        // alpha and the current evaluation.
        if (!this.getOpponent().canReachPosition(this.getKing().getPosition())) {
            double stand_pat = this.evaluate();
            if (stand_pat >= beta)
                return new NegaMaxReturn(beta);
            alpha = Math.max(alpha, stand_pat);
            possibleActions.removeIf(a -> (a[1].getOccupyingPiece() == null && this.actionMap.get(a) < 3.0));
        }
        possibleActions = this.sortPossibleActions(possibleActions);
        Position erasedEnPassant = this.getGame().getPossibleEnPassant();
        Piece movedPiece;
        Piece killedPiece;
        NegaMaxReturn max = new NegaMaxReturn(alpha);
        for( Position[] action : possibleActions )  {
            movedPiece = action[0].getOccupyingPiece();
            killedPiece = this.execute(action);
            NegaMaxReturn possibleMove;
            if (this.getOpponent().canReachPosition(this.getKing().getPosition())) {
                possibleMove = new NegaMaxReturn(-Math.pow(10,14));
            }
            else {
                possibleMove = this.getOpponent().quiesce(-beta, -max.getScore());
                possibleMove.flipScore();
            }
            this.undo(action, erasedEnPassant, movedPiece, killedPiece);
            if( possibleMove.getScore() >= beta )
                return new NegaMaxReturn(beta);   //  fail hard beta-cutoff
            if( possibleMove.getScore() > max.getScore() ) {
                max = possibleMove; // alpha acts like max in MiniMax
                max.setAction(action);
            }
        }
        return max;
    }


    private Piece execute(Position[] action){
        Position oldEnPassant = this.getGame().getPossibleEnPassant();
        Piece killedPiece = action[1].getOccupyingPiece();
        try {
            action[0].getOccupyingPiece().setPosition(action[1]);
        } catch (NullPointerException n) {
            System.out.println(action[0] + " to " + action[1]);
            throw n;
        }
        // Check if the oldEnPassant is replaced by a new one (happens in Pawn.setPosition())
        if (this.getGame().getPossibleEnPassant() != null) {
            if (oldEnPassant == this.getGame().getPossibleEnPassant()) {
                this.getGame().setPossibleEnPassant(null);
            }
            this.game.updatePawns();
        }
        try {
            this.getGame().updateWithoutKings(action);
            this.getGame().setTurn(this.getGame().getTurn()+1);
            this.getGame().updateKings();
        } catch (Exception n) {
            System.out.println("Original action:");
            System.out.println(Arrays.toString(action));
            throw n;
        }
        return killedPiece;
    }

    private void undo(Position[] action, Position erasedEnPassant, Piece movedPiece, Piece killedPiece) {
        this.getGame().setTurn(this.getGame().getTurn()-1);
        Position eraser = this.getGame().getPossibleEnPassant();
        movedPiece.resetPosition(action[0], killedPiece);
        this.getGame().setPossibleEnPassant(erasedEnPassant);
        if (eraser != erasedEnPassant) {
            this.getGame().setTurn(this.getGame().getTurn()+1);
            this.game.updatePawns();
            this.getGame().setTurn(this.getGame().getTurn()-1);
            this.getGame().updateWithoutPawns(action);
        }
        else {
            this.getGame().update(action);
        }
    }


//    private double getActionValue(Position[] action) {
//        if (action[0].getOccupyingPiece() instanceof Pawn
//                && (action[1].getCoordinates()[1] == 0 || action[1].getCoordinates()[1] == 7)) {
//            double valuePieces = 0;
//            if (action[1].getOccupyingPiece() != null) {
//                valuePieces = action[1].getOccupyingPiece().getValue();
//            }
//            return valuePieces + action[0].getOccupyingPiece().getPositionValue(action[1])
//                    - action[0].getOccupyingPiece().getPositionValue();
//        }
//        Position oldEnPassant = this.getGame().getPossibleEnPassant();
//        Piece killedPiece = action[1].getOccupyingPiece();
//        Piece restorePiece = action[1].getOccupyingPiece();
//        if (action.length == 3) {
//            restorePiece = action[2].getOccupyingPiece();
//        }
//        HashMap<Position[], Double> killedActions = new HashMap<>();
//        if (restorePiece != null) {
//            restorePiece.getPossibleActions().forEach(possibleAction ->
//                    killedActions.put(possibleAction, this.getOpponent().actionMap.get(possibleAction)));
//        }
//        Piece movedPiece = action[0].getOccupyingPiece();
//        this.getGame().setTurn(this.getGame().getTurn() + 1);
//        movedPiece.setPosition(action[1]);
//        double after = this.evaluate();
//        movedPiece.resetPosition(action[0], killedPiece);
//        this.getGame().setTurn(this.getGame().getTurn() - 1);
//        this.getGame().setPossibleEnPassant(oldEnPassant);
//        this.getOpponent().actionMap.putAll(killedActions);
//        return after - this.evaluate();
//    }


    private double getActionValue(Position[] action) {
        double valuePieces = 0;
        double valuePositions = 0;
        if (action[1].getOccupyingPiece() != null) {
            valuePieces += action[1].getOccupyingPiece().getValue();
            valuePositions += action[1].getOccupyingPiece().getPositionValue(action[1]);
        }
        try {
            Piece movedPiece = action[0].getOccupyingPiece();
            valuePositions +=  movedPiece.getPositionValue(action[1]) - movedPiece.getPositionValue();
        } catch (NullPointerException n) {
            System.out.println(action[0] + " to " + action[1]);
            this.getPieces().stream()
                    .filter(piece -> piece.getPosition() == action[0])
                    .forEach(piece -> System.out.print(piece.toString() + " "));
            throw n;
        }
        return valuePieces + valuePositions;
    }

    private double evaluate() {
        return this.getValuePieces() - this.getOpponent().getValuePieces();
    }

}