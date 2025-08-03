package Game.GameParts.Pieces;

import Game.Game;
import Game.GameParts.Player;
import Game.GameParts.Position;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * A class of chess pieces.
 *
 * Created by Koen on 6/11/2016.
 */
public abstract class Piece {

    /**
     * Abstract constructor for the Piece class.
     * It adds the created piece to its player and position.
     *
     * @param player
     *          The player this piece belongs to.
     * @param position
     *          The position of the created piece.
     */
    public Piece(Player player, Position position) {
        this.player = player;
        this.addToPosition(position);
        this.player.addPiece(this);
        this.game = this.player.getGame();
    }

    /**
     * Return the player of this piece.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * The player of this piece.
     */
    private final Player player;

    /**
     * Return the game of this piece.
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * The game this piece is a part of.
     */
    private final Game game;

    /**
     * Return the worth of this piece for its player.
     * (9 queen, 5 rook, 1 pawn, 3 knight, 3.25 bishop)
     */
    public abstract double getValue();


    /**
     * Return the position of this piece.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Return the value of the position of this piece.
     */
    public double getPositionValue() {
        return (this.getPosition().getCoordinates()[0] * (7.0 - this.getPosition().getCoordinates()[0])
                + 2.0 * this.getPosition().getCoordinates()[1] * (7.0 - this.getPosition().getCoordinates()[1])) / 100.0;
    }

    /**
     * Return the value of the given position would have if this piece was on it.
     * This method should only be used when estimating the value of a move, and never when updating the players' values.
     */
    public double getPositionValue(Position position){
        return (position.getCoordinates()[0] * (7.0 - position.getCoordinates()[0])
                + 2.0 * position.getCoordinates()[1] * (7.0 - position.getCoordinates()[1])) / 100.0;
    }

    /**
     * Set the position of this piece to the given position.
     * If this piece can't move to the given position (determined by this.canHaveAsPosition(position)),
     * an IllegalArgumentException is thrown.
     * If there is an enemy piece on the given position, it is killed.
     * The score of the player owning this piece is changed to take into account the new position.
     */
    public void setPosition(Position position) {
        if (!this.canHaveAsPosition(position)) {
            System.out.println("oh no");
            System.out.println("Moved piece: " + this);
            System.out.println("Now: " + this.getPosition().toString());
            System.out.println("New: " + position.toString());
            System.out.println("Current occupant: " + position.getOccupyingPiece());
            System.out.println("Mover's possibilities: ");
            for (Position[] position1: this.getPossibleActions())
                System.out.print(Arrays.toString(position1) + " | ");
            System.out.println();
            System.out.println("Mover's reach: ");
            for (Position position1: this.getReach())
                System.out.print(position1 + " ");
            System.out.println();
            if (this instanceof King) {
                System.out.println(((King) this).getFirstMoved() + " " + this.getPlayer().getOpponent().canReachPosition(this.getPosition()));
                this.getPlayer().getOpponent().getPieces().stream()
                        .filter(piece -> piece.getReach().contains(this.getPosition()))
                        .forEach(piece -> System.out.println(piece.toString() + " " + piece.getPosition().toString() + " | "));
                System.out.println(((King) this).castleA + " " + ((King) this).getRookA().getFirstMoved() + " " + ((King) this).getRookA().canCastle());
                System.out.println(((King) this).castleH + " " + ((King) this).getRookH().getFirstMoved() + " " + ((King) this).getRookH().canCastle());
            }
            System.out.println("King's positions:");
            for (Position[] positions: this.getPlayer().getKing().getPossibleActions())
                System.out.print(Arrays.toString(positions) + " ");
            System.out.println();
            System.out.println(this.getPlayer().getKing().getFirstMoved());
            System.out.println(this.getPlayer().toString() + " vs " + this.getGame().getPlayerAtPlay(this.getGame().getTurn()).toString());
            throw new IllegalArgumentException();
        }
        if (position.getOccupyingPiece() != null)
            position.getOccupyingPiece().killPiece();
        this.removeFromPosition();
        this.addToPosition(position);
    }

    /**
     * Reset the position of this piece to the given position.
     * If there is another piece occupying the given position, an AssertionError is thrown.
     * If setting this piece to its current position caused another piece to die, that killed piece is resurrected.
     * The score of the player owning this piece is changed to take the new position into account.
     *
     *  @param previousPosition
     *          The given position.
     *          This should always be the position of this piece before it was moved to its current position.
     */
    public void resetPosition(Position previousPosition, Piece killedPiece) {
        if (previousPosition.getOccupyingPiece() != null){
            System.out.println(previousPosition);
            System.out.println(previousPosition.getOccupyingPiece());
            throw new AssertionError();
        }
        this.removeFromPosition();
        this.addToPosition(previousPosition);
        if (killedPiece != null)
            killedPiece.resurrectPiece();
    }

    /**
     * The position of this piece.
     */
    private Position position;

    /**
     * Return whether this piece can move to the given position.
     * This is determined by whether the given position is in
     * the set of positions this piece can move to (this.possibleActions).
     */
    public boolean canHaveAsPosition(Position givenPosition) {
        return this.getPossibleActions().stream().anyMatch(positions -> positions[1] == givenPosition);
    }


    /**
     * Return whether there is an enemy piece on the given position.
     */
    public boolean enemyOnTile(Position position) {
        return !nooneOnTile(position) && position.getOccupyingPiece().getPlayer() != this.getPlayer();
    }

    /**
     * Return whether there is an ally piece (belonging to the same player) on the given position.
     */
    public boolean friendOnTile(Position position) {
        return !nooneOnTile(position) && position.getOccupyingPiece().getPlayer() == this.getPlayer();
    }

    /**
     * Return whether there is a piece on the given position.
     * If the given position is unoccupied, this method returns "true". If it is occupied, this method returns "false".
     */
    static public boolean nooneOnTile(Position position) {
        return position.getOccupyingPiece() == null;
    }


    /**
     * Return the set of all possible actions this piece can execute.
     */
    public HashSet<Position[]> getPossibleActions() {
        return this.possibleActions;
    }

    /**
     * Update the set of all possible actions this piece can execute.
     * Also update the reach of this piece.
     * Since the elements of this set will be new instances of Position[], the resulting set will have no overlap with
     * the original set, even though some of the instances of Position[] will contain the same Positions.
     */
    public abstract void updatePossibleActions();

    /**
     * The set of all possible actions this piece can execute.
     */
    protected HashSet<Position[]> possibleActions = new HashSet<>();

    /**
     * Return the set of all possible positions this piece could move to if the ally pieces were all enemy pieces,
     * and its current position.
     */
    public HashSet<Position> getReach() {
        return this.reach;
    }

    /**
     * The set of all possible positions this piece could move to if the ally pieces were all enemy pieces.
     * The reach of a piece also contains its current position.
     */
    protected HashSet<Position> reach = new HashSet<>(Collections.singletonList(this.getPosition()));


    public void killPiece() {
        this.removeFromPosition();
        this.getPlayer().removePiece(this);
    }

    public void resurrectPiece() {
        if (position.getOccupyingPiece() != null) {
            System.out.println(position.getOccupyingPiece());
            throw new AssertionError();
        }
        this.addToPosition(this.position);
        this.getPlayer().addPiece(this);
    }

    @Override
    public String toString() {
        return player.getName() + "'s ";
    }


    protected void removeFromPosition() {
        this.position.setOccupyingPiece(null);
        this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() - this.getPositionValue());
    }

    protected void addToPosition(Position position) {
        assert nooneOnTile(position);
        this.position = position;
        this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() + this.getPositionValue());
        this.position.setOccupyingPiece(this);
    }

}
