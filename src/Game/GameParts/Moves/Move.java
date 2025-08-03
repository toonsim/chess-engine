package Game.GameParts.Moves;

import Game.Game;
import Game.GameParts.Pieces.*;
import Game.GameParts.Player;
import Game.GameParts.Position;

/**
 * A class of moves that can be executed by players.
 *
 * Created by Koen on 6/13/2016.
 */
public class Move implements Operation {

    /**
     * Initialise a move with the given starting- and ending positions.
     *
     * @param   startingPosition
     *              The given starting position.
     * @param   endPosition
     *              The given ending position.
     * @throws  IllegalStateException
     *              If there is no piece on the given starting position.
     */
    public Move(Position startingPosition, Position endPosition) {
        if (startingPosition.getOccupyingPiece() == null)
            throw new IllegalStateException();
        this.startingPosition = startingPosition;
        this.endPosition = endPosition;
        this.movedPiece = startingPosition.getOccupyingPiece();
        this.killedPiece = endPosition.getOccupyingPiece();
        this.game = this.movedPiece.getGame();
        affectedPositions = new Position[]{startingPosition, endPosition};
    }

    /**
     * Initialise a move with the given affected positions and the player executing the move.
     *
     * @param   affectedPositions
     *              An array containing the positions this move affects.
     *              The first and second element are defined as the starting and ending position of the move.
     * @throws  IllegalStateException
     *              If there is no piece on the given starting position.
     */
    public Move(Position[] affectedPositions) {
        if (affectedPositions[0].getOccupyingPiece() == null)
            throw new IllegalStateException();
        this.affectedPositions = affectedPositions;
        this.startingPosition = affectedPositions[0];
        this.endPosition = affectedPositions[1];
        this.movedPiece = this.startingPosition.getOccupyingPiece();
        this.killedPiece = this.endPosition.getOccupyingPiece();
        this.game = this.movedPiece.getGame();
    }

    ////////////////////////////////
    // THE POSITIONS OF THIS MOVE //
    ////////////////////////////////

    /**
     * Return the starting position of this move.
     */
    public Position getStartingPosition() {
        return startingPosition;
    }

    /**
     * Variable registering the position of the piece moved by this move before the execution of this move.
     */
    private final Position startingPosition;

    /**
     * Return the ending position of this move.
     */
    public Position getEndPosition() {
        return endPosition;
    }

    /**
     * Variable registering the position of the piece moved by this move after the execution of this move.
     */
    private final Position endPosition;

    public Position[] getAffectedPositions() {
        return this.affectedPositions;
    }

    private final Position[] affectedPositions;

    //////////////////////////////////////
    // THE PIECES AFFECTED BY THIS MOVE //
    //////////////////////////////////////

    public Piece getMovedPiece() {
        return movedPiece;
    }

    private final Piece movedPiece;

    public Piece getKilledPiece() {
        return killedPiece;
    }

    protected final Piece killedPiece;

    ////////////////////////////////
    // THE EXECUTION OF THIS MOVE //
    ////////////////////////////////

    /**
     * Execute this move.
     */
    @Override
    public void execute() throws IllegalArgumentException {
        assert this.movedPiece == startingPosition.getOccupyingPiece();
        this.erasedEnPassant = this.getGame().getPossibleEnPassant();
        this.movedPiece.setPosition(endPosition);
        if (this.getGame().getPossibleEnPassant() != null) {
            if (this.erasedEnPassant == this.getGame().getPossibleEnPassant()) {
                this.getGame().setPossibleEnPassant(null);
            }
            this.game.updatePawns();
        }
        this.getGame().updateWithoutKings(affectedPositions);
        this.getGame().setTurn(this.getGame().getTurn()+1);
        this.getGame().updateKings();
        this.getGame().setTurn(this.getGame().getTurn()-1);
    }

    private Position erasedEnPassant = null;

    /**
     * Undo this move.
     */
    public void undo() {
        this.getGame().setTurn(this.getGame().getTurn() - 1);
        Position eraser = this.getGame().getPossibleEnPassant();
        this.movedPiece.resetPosition(startingPosition, this.killedPiece);
        this.getGame().setPossibleEnPassant(this.erasedEnPassant);
//        this.game.update(new Position[]{this.getStartingPosition(), this.getEndPosition()});
        if (eraser != erasedEnPassant) {
            this.getGame().setTurn(this.getGame().getTurn()+1);
            this.game.updatePawns();
            this.getGame().setTurn(this.getGame().getTurn()-1);
            this.getGame().updateWithoutPawns(this.affectedPositions);
        }
        else {
            this.getGame().update(this.affectedPositions);
        }
//        this.getGame().setTurn(this.getGame().getTurn() + 1);
    }

    ////////////////
    // OTHER SHIT //
    ////////////////

    /**
     * Return the game of this move.
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * The game in which this move takes place.
     * This value is defined as the game the "movedPiece" is part of in the constructor.
     */
    private final Game game;

    /**
     * Return whether the given player can execute this move.
     *
     * @param   player
     *              The given player that wants to execute this move.
     * @return  Whether or not the piece at the starting position of this move is a piece controlled by this player,
     *          and whether or not that piece can move to the end position of this move.
     */
    @Override
    public boolean isValidActionForPlayer(Player player) {
        return this.getMovedPiece().getPlayer().equals(player) && this.getMovedPiece().canHaveAsPosition(this.getEndPosition());
    }

    /**
     * Return whether a given object o is equal to this move.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return this.getStartingPosition().equals(move.getStartingPosition()) && this.getEndPosition().equals(move.getEndPosition());
    }

    /**
     * Return a textual representation of this move.
     */
    @Override
    public String toString() {
        if (this.getKilledPiece() != null) {
            return this.getMovedPiece().getPlayer().getName() + " moves " + startingPosition + " to " + endPosition + ", " + this.getKilledPiece() + " dies";
        }
        return this.getMovedPiece().getPlayer().getName() + " moves " + startingPosition + " to " + endPosition;
    }

    /**
     * Return the PGN of this move
     */
    public String toPGN() {
        String piece = "";
        if (this.getMovedPiece() instanceof King)
            piece = "K";
        else if (this.getMovedPiece() instanceof Queen)
            piece = "Q";
        else if (this.getMovedPiece() instanceof Rook)
            piece = "R";
        else if (this.getMovedPiece() instanceof Bishop)
            piece = "B";
        else if (this.getMovedPiece() instanceof Knight)
            piece = "N";
        return piece + this.getStartingPosition().toString().toLowerCase().substring(0, 1)
                + this.getEndPosition().toString().toLowerCase();
    }
}
