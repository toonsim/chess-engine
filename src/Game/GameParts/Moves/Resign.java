package Game.GameParts.Moves;

import Game.GameParts.Player;

/**
 * An operation that lets players resign.
 *
 * Created by Koen on 4/17/2018.
 */
public class Resign implements Operation {

    /**
     * Initialise a resign operation.
     *
     * @param   player
     *              The player that resigns using this operation.
     */
    public Resign(Player player) {
        this.player = player;
    }

    /**
     * Makes the player associated with this operation resign.
     */
    @Override
    public void execute() {
        this.player.resign();
    }

    /**
     * Return whether or not the player associated with this resign operation can resign.
     * This is always true, as players are always allowed to resign.
     */
    @Override
    public boolean isValidActionForPlayer(Player player) {
        return true;
    }

    /**
     * The player associated with this resign operation.
     */
    private final Player player;

    @Override
    public String toString(){
        return this.player.toString() + " resigns.";
    }

}
