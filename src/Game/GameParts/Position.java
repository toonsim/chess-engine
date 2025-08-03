package Game.GameParts;
import Game.GameParts.Pieces.Piece;

/**
 * A class of positions on a chess board.
 *
 * Created by Koen on 6/11/2016.
 */
public class Position {

    public Position(int[] coordinates) {
        if (!isValidCoordinates(coordinates))
            throw new IllegalArgumentException();
        this.coordinates = coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;

        Position position = (Position) o;

        return this.getCoordinates()[0] == position.getCoordinates()[0] && this.getCoordinates()[1] == position.getCoordinates()[1];

    }

    public static boolean isWithinBounds(int x, int y) {
        return x >= 0 && x <= 7
                && y >= 0 && y <= 7;
    }

    public boolean isValidCoordinates(int[] coordinates) {
        if (coordinates.length != 2)
            return false;
        if (!Position.isWithinBounds(coordinates[0], coordinates[1])) {
            System.out.println(coordinates[0]+coordinates[1]);
            return false;
        }
        return true;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    private final int[] coordinates;

    public Piece getOccupyingPiece() {
        return occupyingPiece;
    }

    public void setOccupyingPiece(Piece occupyingPiece) {
        this.occupyingPiece = occupyingPiece;
    }

    private Piece occupyingPiece;

    @Override
    public String toString() {
//        if (this.getOccupyingPiece() == null) {
//            return "" + String.valueOf((char)(this.getCoordinates()[0] + 65)) + (this.getCoordinates()[1] + 1) + ", no piece here";
//        }
//        return "" + String.valueOf((char)(this.getCoordinates()[0] + 65)) + (this.getCoordinates()[1] + 1) + ", " + this.getOccupyingPiece().toString();
        return "" + String.valueOf((char)(this.getCoordinates()[0] + 65)) + (this.getCoordinates()[1] + 1);

    }
}