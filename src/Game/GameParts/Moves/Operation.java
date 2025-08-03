package Game.GameParts.Moves;

import Game.GameParts.Player;

/**
 * An interface for everything a player can do.
 *
 * Created by Koen on 1/4/2017.
 */
public interface Operation {

    /**
     * Execute this operation.
     */
    public void execute();

    /**
     * Return whether the given player can execute this operation.
     */
    public boolean isValidActionForPlayer(Player player);

}
