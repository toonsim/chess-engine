package Game.GameParts.Pieces;


import Game.GameParts.Player;
import Game.GameParts.Position;

/**
 * A class of kings.
 *
 * Created by Koen on 8/3/2016.
 */
public class King extends Piece {

    public King(Player player, Position position, Rook rookA, Rook rookH) {
        super(player, position);
        this.rookA = rookA;
        this.rookH = rookH;
        player.setKing(this);
        this.couldCastleValue = this.castlingValue();
        if (this.getPlayer().isWhite())
            this.defensiveLine = 1;
        else {
            this.defensiveLine = 6;
        }
    }

    public Boolean isAlive() {
        return alive;
    }

    private Boolean alive = true;

    @Override
    public double getValue() {
        return 50000;
    }

    @Override
    public double getPositionValue() {
        return 1.0 - (this.getPosition().getCoordinates()[0] * (7.0 - this.getPosition().getCoordinates()[0])
                + this.getPosition().getCoordinates()[1] * (7.0 - this.getPosition().getCoordinates()[1])) / 100.0;
    }

    @Override
    public double getPositionValue(Position position) {
        return 1.0 - (position.getCoordinates()[0] * (7.0 - position.getCoordinates()[0])
                + position.getCoordinates()[1] * (7.0 - position.getCoordinates()[1])) / 100.0;
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        if (this.firstMoved == -1) {
            this.firstMoved = this.getGame().getTurn();
            if (position == castleA && rookA.canCastle()) {
                rookA.setPosition(this.getGame().getPosition(3, this.getPosition().getCoordinates()[1]));
                // To compensate for the game thinking the option of castling was lost
                this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() + this.couldCastleValue);
                // To incentivize castling (very crude way of doing it, replace if you find something better)
                double castleProtection = 0.;
                double[] protectionValues = {0.15,0.2,0.15};
                for (int i = 0; i < 3; i++){
                    if (this.friendOnTile(this.getGame().getPosition(i, this.getDefensiveLine()))) {
                        if (this.getGame().getPosition(i, this.getDefensiveLine()).getOccupyingPiece() instanceof Pawn) {
                            castleProtection += protectionValues[i];
                        }
                    }
                }
                this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() + castleProtection);
            }
            else if (position == castleH && rookH.canCastle()) {
                rookH.setPosition(this.getGame().getPosition(5, this.getPosition().getCoordinates()[1]));
                // To compensate for the game thinking the option of castling was lost
                this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() + this.couldCastleValue);
                // To incentivize castling (very crude way of doing it, replace if you find something better)
                double castleProtection = 0.;
                double[] protectionValues = {0.15,0.2,0.15};
                for (int i = 0; i < 3; i++){
                    if (this.friendOnTile(this.getGame().getPosition(i+5, this.getDefensiveLine()))) {
                        if (this.getGame().getPosition(i+5, this.getDefensiveLine()).getOccupyingPiece() instanceof Pawn) {
                            castleProtection += protectionValues[i];
                        }
                    }
                }
                this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() + castleProtection);
            }
        }
    }

    @Override
    public void resetPosition(Position previousPosition, Piece killedPiece) {
        super.resetPosition(previousPosition, killedPiece);
        if (this.firstMoved == this.getGame().getTurn()) {
            this.firstMoved = -1;
            if (rookA.getFirstMoved() == this.getGame().getTurn()) {
                rookA.resetPosition(this.getGame().getPosition(0, this.getPosition().getCoordinates()[1]), null);
                // To compensate for the game thinking the option of castling was regained
                this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() - this.castlingValue());
                // To incentivize castling (very crude way of doing it, replace if you find something better)
                double castleProtection = 0.;
                double[] protectionValues = {0.15,0.2,0.15};
                for (int i = 0; i < 3; i++){
                    if (this.friendOnTile(this.getGame().getPosition(i, this.getDefensiveLine()))) {
                        if (this.getGame().getPosition(i, this.getDefensiveLine()).getOccupyingPiece() instanceof Pawn) {
                            castleProtection += protectionValues[i];
                        }
                    }
                }
                this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() - castleProtection);
            }
            else if (rookH.getFirstMoved() == this.getGame().getTurn()) {
                rookH.resetPosition(this.getGame().getPosition(7, this.getPosition().getCoordinates()[1]), null);
                // To compensate for the game thinking the option of castling was regained
                this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() - this.castlingValue());
                // To incentivize castling (very crude way of doing it, replace if you find something better)
                double castleProtection = 0.;
                double[] protectionValues = {0.15,0.2,0.15};
                for (int i = 0; i < 3; i++){
                    if (this.friendOnTile(this.getGame().getPosition(i+5, this.getDefensiveLine()))) {
                        if (this.getGame().getPosition(i+5, this.getDefensiveLine()).getOccupyingPiece() instanceof Pawn) {
                            castleProtection += protectionValues[i];
                        }
                    }
                }
                this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() - castleProtection);
            }
        }
    }

    @Override
    public void updatePossibleActions() {
        this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() - this.couldCastleValue);
        this.possibleActions.clear();
        this.reach.clear();
        for (int a = -1; a < 2; ++a) {
            for (int b = -1; b < 2; ++b) {
                if (!(a == 0 && b == 0)) {
                    if (Position.isWithinBounds(this.getPosition().getCoordinates()[0] + a,
                            this.getPosition().getCoordinates()[1] + b)) {
                        Position possiblePosition = this.getGame().getPosition(
                                this.getPosition().getCoordinates()[0] + a, this.getPosition().getCoordinates()[1] + b);
                        this.reach.add(possiblePosition);
                        if (!friendOnTile(possiblePosition))
                            this.possibleActions.add(new Position[]{this.getPosition(),possiblePosition});
                    }
                }
            }
        }
        //Castling
        if (this.getFirstMoved() == -1) {
            // Rook A
            if (rookA.canCastle()) {
                boolean allowed = true;
                for (int i = 3; i>0; i--) {
                    Position position = this.getGame().getPosition(i, this.getPosition().getCoordinates()[1]);
                    if (!nooneOnTile(position) || this.getPlayer().getOpponent().canReachPosition(position)) {
                        allowed = false;
                    }
                }
                if (allowed && !this.getPlayer().getOpponent().canReachPosition(this.getPosition())) {
                    this.castleA = this.getGame().getPosition(2, this.getPosition().getCoordinates()[1]);
                    this.reach.add(castleA);
                    this.possibleActions.add(new Position[]{this.getPosition(),castleA,
                            this.getGame().getPosition(0, this.getPosition().getCoordinates()[1]),
                            this.getGame().getPosition(3, this.getPosition().getCoordinates()[1])});
                }
            }
            // Rook H
            if (rookH.canCastle()) {
                boolean allowed = true;
                for (int i = 5; i<7; i++) {
                    Position position = this.getGame().getPosition(i, this.getPosition().getCoordinates()[1]);
                    if (!nooneOnTile(position) || this.getPlayer().getOpponent().canReachPosition(position)) {
                        allowed = false;
                    }
                }
                if (allowed && !this.getPlayer().getOpponent().canReachPosition(this.getPosition())) {
                    this.castleH = this.getGame().getPosition(6, this.getPosition().getCoordinates()[1]);
                    this.reach.add(castleH);
                    this.possibleActions.add(new Position[]{this.getPosition(),castleH,
                            this.getGame().getPosition(7, this.getPosition().getCoordinates()[1]),
                            this.getGame().getPosition(5, this.getPosition().getCoordinates()[1])});
                }
            }
        }
        this.reach.add(this.getPosition());
        this.couldCastleValue = this.castlingValue();
        this.getPlayer().setValuePieces(this.getPlayer().getValuePieces() + this.couldCastleValue);
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

    @Override
    public String toString() {
        return super.toString() + "King";
    }

    public int getFirstMoved() {
        return firstMoved;
    }

    private int firstMoved = -1;

    public Rook getRookA() {
        return this.rookA;
    }

    private final Rook rookA;

    public Position castleA = null;

    public Rook getRookH() {
        return this.rookH;
    }

    private final Rook rookH;

    public Position castleH = null;

    public int getDefensiveLine(){
        return this.defensiveLine;
    }

    /**
     * A variable noting at which row the pawns of the player are located (forming a defensiveline for the castled
     * king).
     * This variable is 1 for the White player, and 6 for the Black player.
     */
    private final int defensiveLine;

    /**
     * Return the inherent value of being able to castle this king.
     * This method only takes into account whether or not this king could still castle at some point in the game, which
     * only depends on whether this king and its rooks have moved so far or not. Eg.: if this king can't castle at the
     * moment, because it is in check, but still could castle once that problem is resolved, this method returns the
     * full value.
     * If the king has already castled, this method returns zero. The incentive associated with castling should
     * therefore be added double when castling the king, as it will lose this value once it has castled.
     */
    public double castlingValue() {
        double value = 0.0;
        if (this.getFirstMoved() == -1) {
            if (this.rookA.canCastle())
                value += 0.25;
            if (this.rookH.canCastle())
                value += 0.25;
        }
        return value;
    }

    private double couldCastleValue;

}
