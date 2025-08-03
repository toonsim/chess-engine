package Game.GameParts.Moves;

import Game.GameParts.Position;

import java.util.Arrays;

/**
 * A class of what the NegaMax returns, aka an action and a score.
 *
 * Created by Koen on 8/26/2016.
 */
public class NegaMaxReturn {

    public NegaMaxReturn(double score) {
        this.score = score;
    }

    public Position[] getAction() {
        return this.action;
    }

    public void setAction(Position[] action) {
        this.action = action;
    }

    private Position[] action;

    public double getScore() {
        return this.score;
    }

    public void flipScore() {
        this.score = - this.score;
    }

    private double score;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NegaMaxReturn)) return false;

        NegaMaxReturn that = (NegaMaxReturn) o;

        return  Math.abs(that.getScore()) - Math.abs(this.getScore()) < 0.001 && Arrays.equals(getAction(), that.getAction());

    }

    @Override
    public String toString() {
        return this.action[0] + " to " + this.action[1] + " with score " + this.score;
    }
}
