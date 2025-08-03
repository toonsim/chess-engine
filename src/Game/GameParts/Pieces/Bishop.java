package Game.GameParts.Pieces;

import Game.GameParts.Player;
import Game.GameParts.Position;

/**
 * A class of bishops.
 *
 * Created by Koen on 7/8/2016.
 */
public class Bishop extends Piece {

    public Bishop(Player player, Position position) {
        super(player, position);
    }

    @Override
    public double getValue() {
        return 3.25;
    }

    @Override
    public void updatePossibleActions() {
        this.possibleActions.clear();
        this.reach.clear();
        // Move diagonally
        for (int a = -1; a < 2; a += 2) {
            for (int b = -1; b < 2; b += 2) {
                for (int i = 1; i < 8; ++i) {
                    if (Position.isWithinBounds(this.getPosition().getCoordinates()[0] + a * i,
                            this.getPosition().getCoordinates()[1] + b * i)) {
                        Position possiblePosition = this.getGame().getPosition(
                                this.getPosition().getCoordinates()[0] + a * i, this.getPosition().getCoordinates()[1] + b * i);
                        this.reach.add(possiblePosition);
                        if (!friendOnTile(possiblePosition)) {
//                            System.out.println(this.getPlayer());
                            this.possibleActions.add(new Position[]{this.getPosition(),possiblePosition});
                        }
                        if (!nooneOnTile(possiblePosition)) {
                            break;
                        }
                    }
                    else
                        break;
                }
            }
        }
        this.reach.add(this.getPosition());
    }

    @Override
    public String toString() {
        return super.toString() + "Bishop";
    }
}
