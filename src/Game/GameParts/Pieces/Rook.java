package Game.GameParts.Pieces;

import Game.GameParts.Player;
import Game.GameParts.Position;

/**
 * A class of rooks.
 *
 * Created by Koen on 8/3/2016.
 */
public class Rook extends Piece {

    public Rook(Player player, Position position) {
        super(player, position);
    }

    @Override
    public double getValue() {
        return 5.0;
    }

    private Boolean isAlive() {
        return alive;
    }

    private Boolean alive = true;

    @Override
    public void setPosition(Position position) {
        if (this.firstMoved == -1)
            this.firstMoved = this.getGame().getTurn();
        super.setPosition(position);
    }

//    protected void setPosition(Position position, King king) {
//        if (this.firstMoved != -1 || this.getPlayer().getKing() != king)
//            throw new IllegalArgumentException();
//        this.firstMoved = this.getGame().getTurn();
//        this.removeFromPosition();
//        this.addToPosition(position);
//    }

    @Override
    public void resetPosition(Position previousPosition, Piece killedPiece) {
        if (this.firstMoved == this.getGame().getTurn())
            this.firstMoved = -1;
        super.resetPosition(previousPosition, killedPiece);
    }

    @Override
    public void updatePossibleActions() {
        this.possibleActions.clear();
        this.reach.clear();
        for (int a = -1; a < 2; a += 2) {
            // Move sideways
            for (int i = 1; i < 8; ++i) {
                if (Position.isWithinBounds(this.getPosition().getCoordinates()[0] + i*a,
                        this.getPosition().getCoordinates()[1])) {
                    Position possiblePosition = this.getGame().getPosition(
                            this.getPosition().getCoordinates()[0] + a * i, this.getPosition().getCoordinates()[1]);
                    this.reach.add(possiblePosition);
                    if (!friendOnTile(possiblePosition))
                        this.possibleActions.add(new Position[]{this.getPosition(),possiblePosition});
                    if (!nooneOnTile(possiblePosition)) {
                        break;
                    }
                } else
                    break;
            }
            // Move vertically
            for (int i = 1; i < 8; ++i) {
                if (Position.isWithinBounds(this.getPosition().getCoordinates()[0],
                        this.getPosition().getCoordinates()[1] + i*a)) {
                    Position possiblePosition = this.getGame().getPosition(
                            this.getPosition().getCoordinates()[0], this.getPosition().getCoordinates()[1] + a * i);
                    this.reach.add(possiblePosition);
                    if (!friendOnTile(possiblePosition))
                        this.possibleActions.add(new Position[]{this.getPosition(),possiblePosition});
                    if (!nooneOnTile(possiblePosition)) {
                        break;
                    }
                } else
                    break;
            }
        }
        this.reach.add(this.getPosition());
    }

    @Override
    public void killPiece(){
        this.alive = false;
        super.killPiece();
    }

    @Override
    public void resurrectPiece(){
        this.alive = true;
        super.resurrectPiece();
    }

    boolean canCastle() {
        return this.firstMoved == -1 && this.isAlive();
    }

    @Override
    public String toString() {
        return super.toString() + "Rook";
    }

    int getFirstMoved() {
        return firstMoved;
    }

    private int firstMoved = -1;

}
