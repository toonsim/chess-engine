package Game.GameParts.Moves;

import Game.Game;
import Game.GameParts.Position;

/**
 * A class defining castling as a special type of move executed by the player.
 *
 * Created by Koen on 11/3/2018.
 */
public class Castle extends Move {

    /**
     * Initialise a castling move with the given starting- and ending positions and the player executing the move.
     *
     * @param startingPosition
     *                  The given starting position.
     * @param endPosition
     *                  The given ending position.
     * @throws NullPointerException
     *                  If there is no piece on the given starting position.
     */
    public Castle(Position startingPosition, Position endPosition) {
        super(Castle.affectedByCastle(startingPosition, endPosition));
    }

    /**
     * Return the positions affected by a castling move between the given positions.
     */
    private static Position[] affectedByCastle(Position startingPosition, Position endPosition) {
        Game game = startingPosition.getOccupyingPiece().getGame();
        if (endPosition.getCoordinates()[0]
                - startingPosition.getCoordinates()[0] > 0)
            return new Position[]{startingPosition, endPosition,
                    game.getPosition(7, startingPosition.getCoordinates()[1]),
                    game.getPosition(5, startingPosition.getCoordinates()[1])};
        else
            return new Position[]{startingPosition, endPosition,
                    game.getPosition(0, startingPosition.getCoordinates()[1]),
                    game.getPosition(3, startingPosition.getCoordinates()[1])};
    }

    /**
     * Initialise a castling move with the given affected positions and the player executing the move.
     *
     * @param   affectedPositions
     *              An array containing the positions this castling move affects.
     *              The first and second element are defined as the starting and ending position of the move.
     * @throws  IllegalStateException
     *              If there is no piece on the given starting position.
     */
    public Castle(Position[] affectedPositions) {
        super(affectedPositions);
    }

    /**
     * Return the PGN of this move
     */
    @Override
    public String toPGN(){
        if (this.getEndPosition().getCoordinates()[0]
                - this.getStartingPosition().getCoordinates()[0] > 0)
            return "O-O";
        else
            return "O-O-O";
    }

    /**
     * Return a textual representation of this castling move.
     */
    @Override
    public String toString(){
        return this.getMovedPiece().getPlayer().getName() + " castles their king from " + this.getStartingPosition()
                + " to " + this.getEndPosition();
    }

}
