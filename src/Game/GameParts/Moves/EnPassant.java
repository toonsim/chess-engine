package Game.GameParts.Moves;

import Game.Game;
import Game.GameParts.Pieces.Pawn;
import Game.GameParts.Position;

/**
 * A class defining en passant as a special type of move executed by the player.
 *
 * Created by Koen on 11/5/2018.
 */
public class EnPassant extends  Move {

    /**
     * Initialise an en passant move with the given starting- and ending positions and the player executing the move.
     *
     * @param startingPosition
     *                  The given starting position.
     * @param endPosition
     *                  The given ending position.
     * @throws IllegalStateException
     *                  If this move violates the en passant rules of chess.
     */
    public EnPassant(Position startingPosition, Position endPosition) {
        super(EnPassant.affectedByEnPassant(startingPosition, endPosition));
        this.killPosition = this.getAffectedPositions()[2];
    }

    /**
     * Return the positions affected by an en passant move between the given positions.
     */
    private static Position[] affectedByEnPassant(Position startingPosition, Position endPosition) {
        Game game = startingPosition.getOccupyingPiece().getGame();
        if (!(startingPosition.getOccupyingPiece() instanceof Pawn)) {
            throw new IllegalArgumentException();
        }
        if (!(endPosition.getOccupyingPiece() == null && endPosition == game.getPossibleEnPassant())) {
            throw new IllegalArgumentException();
        }
        Position killPosition = game.getPosition(endPosition.getCoordinates()[0], startingPosition.getCoordinates()[1]);
        if (!(killPosition.getOccupyingPiece() != null
                && killPosition.getOccupyingPiece() instanceof Pawn
                && killPosition.getOccupyingPiece().getPlayer()
                == startingPosition.getOccupyingPiece().getPlayer().getOpponent())) {
            throw new IllegalArgumentException();
        }
        return new Position[]{startingPosition, endPosition, killPosition};
    }

    /**
     * Initialise an en passant move with the given affected positions and the player executing the move.
     *
     * @param   affectedPositions
     *              An array containing the positions this en passant move affects.
     *              The first and second element are defined as the starting and ending position of the move.
     *              The third element is defined as the position of the piece this en passant kills, as specified by
     *              the rules of chess.
     * @throws  IllegalStateException
     *              If there is no piece on the given starting position.
     */
    public EnPassant(Position[] affectedPositions) {
        super(affectedPositions);
        this.killPosition = affectedPositions[2];
    }

    /**
     * Return the position of the piece this en passant kills.
     */
    public Position getKillPosition() {
        return this.killPosition;
    }

    /**
     * The position of the piece this en passant kills.
     * Not to be confused with the result of the method "getKilledPiece()", which returns the piece at the ending
     * position of a move (null for en passant).
     */
    private final Position killPosition;

    /**
     * Return a textual representation of this en passant.
     */
    @Override
    public String toString(){
        return this.getMovedPiece().getPlayer().getName() + " moves " + this.getStartingPosition() + " to "
                + this.getEndPosition() + " via En Passant, killing "
                + ((Pawn) this.getMovedPiece()).getEnPassantKill().toString();
    }

}
