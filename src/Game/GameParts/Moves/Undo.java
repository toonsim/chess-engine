package Game.GameParts.Moves;

import Game.Game;
import Game.GameParts.Player;

import java.util.ArrayList;

/**
 * An operation that undoes every action up to and including the last action executed by a controlled player.
 *
 * Created by Koen on 1/4/2017.
 */
public class Undo implements Operation {

    public Undo(Game game) {
        if (!(game.getTurn() > 0)) {
            throw new IllegalArgumentException();
        }
        this.game = game;
    }

    private Game game;


    @Override
    public void execute() {
        this.actionsToBeUndone.add(this.game.getPlayedAction(this.game.getTurn() - 1));
        if (this.game.getTurn() >= 2 && !this.game.getPlayerAtPlay(game.getTurn() - 1).isControlled()) {
            this.actionsToBeUndone.add(this.game.getPlayedAction(this.game.getTurn() - 2));
        }
        this.game.setTurnsToBeRewound(this.actionsToBeUndone.size());
    }

    /**
     * Return the actions that have to be undone according to this Undo.
     */
    public ArrayList<Move> getActionsToBeUndone() {
        return this.actionsToBeUndone;
    }

    /**
     * An arraylist containing the actions that have to be undone.
     */
    private final ArrayList<Move> actionsToBeUndone = new ArrayList<>();

    @Override
    public boolean isValidActionForPlayer(Player player) {
        return player.isControlled() &&
                (this.game.getTurn() >= 1 && player.getOpponent().isControlled() || this.game.getTurn() >= 2);
    }

    @Override
    public String toString() {
        return "The following moves are undone: " + System.lineSeparator() +
                this.getActionsToBeUndone();
    }
}
