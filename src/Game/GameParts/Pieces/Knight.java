package Game.GameParts.Pieces;

import Game.GameParts.Player;
import Game.GameParts.Position;

/**
 * A class of knights.
 *
 * Created by Koen on 7/8/2016.
 */
public class Knight extends Piece {

    public Knight(Player player, Position position) {
        super(player, position);
    }

    @Override
    public double getValue() {
        return 3.0;
    }

    @Override
    public void updatePossibleActions() {
        this.possibleActions.clear();
        this.reach.clear();
        for (int a = -1; a < 2; a += 2) {
            for (int b = -1; b < 2; b += 2) {
                if (Position.isWithinBounds(this.getPosition().getCoordinates()[0] + 2*a,
                        this.getPosition().getCoordinates()[1] + b)) {
                    Position possiblePosition =
                            this.getGame().getPosition(
                                    this.getPosition().getCoordinates()[0] + a * 2, this.getPosition().getCoordinates()[1] + b);
                    this.reach.add(possiblePosition);
                    if (!friendOnTile(possiblePosition)) {
                        this.possibleActions.add(new Position[]{this.getPosition(),possiblePosition});
                    }
                }
                if (Position.isWithinBounds(this.getPosition().getCoordinates()[0] + a,
                        this.getPosition().getCoordinates()[1] + 2*b)) {
                    Position possiblePosition =
                            this.getGame().getPosition(
                                    this.getPosition().getCoordinates()[0] + a, this.getPosition().getCoordinates()[1] + b * 2);
                    this.reach.add(possiblePosition);
                    if (!friendOnTile(possiblePosition)) {
                        this.possibleActions.add(new Position[]{this.getPosition(),possiblePosition});
                    }
                }
            }
        }
        this.reach.add(this.getPosition());
    }

    @Override
    public String toString() {
        return super.toString() + "Knight";
    }
}
