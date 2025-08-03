package Game.GameParts.Pieces;

import Game.GameParts.Player;
import Game.GameParts.Position;

/**
 * A class of pawns.
 *
 * Created by Koen on 6/13/2016.
 */
public class Pawn extends Piece {

    /**
     * Initialise this new Pawn with the given player.
     */
    public Pawn(Player player, Position position) {
        super(player, position);
        this.xCoordinate = "" + this.getPosition().toString().charAt(0);
    }

    @Override
    public double getValue() {
        return 1.0;
    }

    @Override
    public double getPositionValue() {
        double horizontalPushValue = 1.0 + 0.5/Math.abs(3.5-this.getPosition().getCoordinates()[0]);
//        if (this.getPosition().getCoordinates()[0] < 3.5) {
//            horizontalPushValue += this.getPosition().getCoordinates()[0];
//        }
//        else {
//            horizontalPushValue += 7.0 - this.getPosition().getCoordinates()[0];
//        }
        double value = (this.getPosition().getCoordinates()[0] * (7.0 - this.getPosition().getCoordinates()[0])
                + horizontalPushValue * 8.0 * Math.min(Math.abs(this.getStartingLine() - this.getPosition().getCoordinates()[1]),3) ) / 100.0;
        if (this.getPosition().getCoordinates()[1] == 0 || this.getPosition().getCoordinates()[1] == 7)
            value = super.getPositionValue() + 9.0;
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            System.out.println(this.getPosition());
            throw new IllegalStateException();
        }
        return value;
    }

    @Override
    public double getPositionValue(Position position) {
        double horizontalPushValue = 1.0 + 0.5/Math.abs(3.5-this.getPosition().getCoordinates()[0]);
//        if (this.getPosition().getCoordinates()[0] < 3.5) {
//            horizontalPushValue += this.getPosition().getCoordinates()[0];
//        }
//        else {
//            horizontalPushValue += 7.0 - this.getPosition().getCoordinates()[0];
//        }
        double value = (position.getCoordinates()[0] * (7.0 - position.getCoordinates()[0])
                + horizontalPushValue * 8.0 * Math.min(Math.abs(this.getStartingLine() - position.getCoordinates()[1]),4)) / 100.0;
        if (position.getCoordinates()[1] == 0 || position.getCoordinates()[1] == 7)
            value = super.getPositionValue(position) + 9.0;
        if (this.getGame().getPossibleEnPassant() != null &&
                position.getCoordinates()[0] == this.getGame().getPossibleEnPassant().getCoordinates()[0] &&
                position.getCoordinates()[1] ==
                        this.getGame().getPossibleEnPassant().getCoordinates()[1] + this.getDirection()) {
            value += this.getValue(); // En Passant
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            System.out.println(position);
            throw new IllegalStateException();
        }
        return value;
    }

    /**
     * Return the direction this pawn walks in. White pawns go up (+ 1), black pawns go down (-1).
     */
    public int getDirection(){
        if (this.getPlayer().isWhite())
            return 1;
        return -1;
    }

    /**
     * Return the line at which this pawn starts. White pawns start at the second line, black pawns at the seventh.
     */
    private int getStartingLine(){
        if (this.getPlayer().isWhite())
            return 1;
        return 6;
    }

    @Override
    public void updatePossibleActions() {
        this.possibleActions.clear();
        this.reach.clear();
        int x = this.getPosition().getCoordinates()[0] - 1;
        int y = this.getPosition().getCoordinates()[1] + this.getDirection();
        Position possiblePosition;
        for (int i = -1; i < 2; ++i) {
            // Check whether the given coordinates are valid.
            if (Position.isWithinBounds(x, y)) {
                possiblePosition = this.getGame().getPosition(x, y);
                this.reach.add(possiblePosition);
                if (this.positionChecker(x, y)) {
                    if (this.getGame().getPosition(x, y) == this.getGame().getPossibleEnPassant()) {
                        this.possibleActions.add(new Position[]{this.getPosition(), possiblePosition,
                                this.getGame().getPosition(x, this.getPosition().getCoordinates()[1])});
                    }
                    else {
                        this.possibleActions.add(new Position[]{this.getPosition(), possiblePosition});
                    }
                }
            }
            x += 1;
        }
        if (this.getPosition().getCoordinates()[1] == this.getStartingLine()) {
            Position twoForward = this.getGame().getPosition(this.getPosition().getCoordinates()[0],
                    this.getPosition().getCoordinates()[1] + 2 * this.getDirection());
            this.reach.add(twoForward);
            if (this.positionChecker(twoForward)) {
                this.possibleActions.add(new Position[]{this.getPosition(),twoForward});
            }
        }
        this.reach.add(this.getPosition());
//        x = this.getPosition().getCoordinates()[0] - 1;
//        y = this.getStartingLine() + 5*this.getDirection();
//        for (int i = -1; i < 2; ++i) {
//            // Check whether the given coordinates are valid.
//            if (Position.isWithinBounds(x, y)) {
//                possiblePosition = this.getGame().getPosition(x, y);
//                this.reach.add(possiblePosition);
//            }
//            x += 1;
//        }
    }

    /**
     * Check whether this pawn can move to the position at the given coordinates.
     * The given positions are assumed to be within the boundaries of the board.
     *
     * NOTE:    This method is used to update the possible moves. Outside of that, the method "canHaveAsPosition" should be used.
     * NOTE:    This method assumes that it only gets positions within the normal range of the pawn.
     *          If this is not the case, weird things happen.
     *          Range:       ___ ___ ___ ___ ___
     *                      |___|___|___|___|___|
     *                      |___|_x_|_x_|_x_|___|
     *                      |___|___|_P_|___|___|
     *                      |___|___|___|___|___|
     */
    private boolean positionChecker(int x, int y) {
        Position possiblePosition = this.getGame().getPosition(x, y);
        // Check whether this pawn moves forward one step
        if (y - this.getPosition().getCoordinates()[1] == this.getDirection()) {
            // Check whether there is any horizontal movement
            if (x == this.getPosition().getCoordinates()[0]) {
                return nooneOnTile(possiblePosition);
            }
            // Check whether there is an enemy at the given coordinates || the coordinates correspond to an en passant
            return this.enemyOnTile(possiblePosition) ||
                    (nooneOnTile(possiblePosition)
                            && this.getGame().getPossibleEnPassant() == possiblePosition
                            && this.getGame().getPlayerAtPlay() != this.getPlayer());
        }
        return false;
    }

    /**
     * Check whether this pawn can move to the position at the given coordinates.
     *
     * NOTE:    This method is used to update the possible moves. Outside of that, the method "canHaveAsPosition" should be used.
     * NOTE:    This method assumes that it only gets positions within the special starting range of the pawn. If this is not the case, weird shit happens.
     *          Range:      ____|_2FW_|____
     *                      ____|_1FW_|____
     *                      ____|__P__|____
     */
    private boolean positionChecker(Position twoForward) {
        // The only other valid scenario is when the pawn is moved two spaces forward.
        // This is allowed when there is no-one at the given coordinates or between the pawn and the given coordinates,
        // and the pawn is at it's starting position.
        return nooneOnTile(this.getGame().getPosition(this.getPosition().getCoordinates()[0], this.getPosition().getCoordinates()[1] + this.getDirection()))
                && nooneOnTile(twoForward);
    }

    /**
     * Changes the position of this pawn.
     * If the pawn makes it's first move and sets two steps forward, the opposing players en passant list is updated.
     * If the pawn reaches the edge of the board, it is 'queened':
     * it is removed from the board and the list of pieces of its player, and a new queen takes it's place.
     */
    @Override
    public void setPosition(Position position) {
        if (Math.abs(position.getCoordinates()[1] - this.getPosition().getCoordinates()[1]) == 2)
            this.getGame().setPossibleEnPassant(
                    this.getGame().getPosition(this.getPosition().getCoordinates()[0],
                    this.getPosition().getCoordinates()[1] + this.getDirection()));
        if (position.getCoordinates()[1] == 0 || position.getCoordinates()[1] == 7) {
            super.setPosition(position);
            this.killPiece();
            this.possibleActions.clear();
            this.reach.clear();
            this.reach.add(this.getPosition());
            this.getPlayer().prefferedPawnReplacement(position);
//            System.out.println("END REACHED");
//            this.getGame().update(new Position[]{replacement.getPosition()});
            return;
        }
//        if (position.getCoordinates()[1] == this.getPosition().getCoordinates()[1] + this.getDirection()
//                && Math.abs(position.getCoordinates()[0] - this.getPosition().getCoordinates()[0]) > 0.5
//                && nooneOnTile(position) && this.getGame().getPossibleEnPassant() == position) {
        if (this.getGame().getPossibleEnPassant() == position) {
            assert position == this.getGame().getPosition(position.getCoordinates()[0],
                    this.getPosition().getCoordinates()[1]);
            this.enPassantKill = (Pawn) this.getGame().getPosition(position.getCoordinates()[0],
                    this.getPosition().getCoordinates()[1]).getOccupyingPiece(); //En passant
            this.enPassantKill.killPiece();
//            System.out.println("WAUW EEN FOUT");
//            this.getGame().update(new Position[]{this.enPassantKill.getPosition()});
        }
        super.setPosition(position);
    }

    @Override
    public void resetPosition(Position previousPosition, Piece killedPiece) {
        if (Math.abs(previousPosition.getCoordinates()[1] - this.getPosition().getCoordinates()[1]) == 2)
            this.getGame().setPossibleEnPassant(null);
        if (this.getPosition().getCoordinates()[1] == 0 || this.getPosition().getCoordinates()[1] == 7) {
            assert !(this.getPosition().getOccupyingPiece() instanceof Pawn);
            this.getPosition().getOccupyingPiece().killPiece();
            assert (this.getPosition().getOccupyingPiece() == null);
            this.resurrectPiece();
            super.resetPosition(previousPosition, killedPiece);
            return;
        }
        if (this.getPosition().getCoordinates()[1] == previousPosition.getCoordinates()[1] + this.getDirection() &&
                Math.abs(previousPosition.getCoordinates()[0] - this.getPosition().getCoordinates()[0]) == 1
                && killedPiece == null) {
//            System.out.println("MADE IT " + this.enPassantKill);
            killedPiece = this.enPassantKill;
            this.enPassantKill = null;
        }
        super.resetPosition(previousPosition, killedPiece);
    }

    public Pawn getEnPassantKill() {
        return this.enPassantKill;
    }

    private Pawn enPassantKill = null;

    @Override
    public String toString() {
        return super.toString() + "Pawn (" + this.xCoordinate + ")";
    }

    private final String xCoordinate;
}
