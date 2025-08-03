package Game;

import Game.GameParts.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class that saves a given game.
 *
 * Created by Koen on 12/6/2017.
 */
class GameSaver {

    static void saveGame(Game game, String duration) {
        FileWriter writer;
        String dir = System.getProperty("user.dir");
        String destination = dir + "\\Played Games";
        String matchName = game.getWhite() + " vs " + game.getBlack();
        String separator = " - ";
        String date = (new SimpleDateFormat("yyyy-MM-dd HH-mm-ss")).format(new Date()) + ".txt";
        File file = new File(destination, matchName + separator + date);
        try {
            writer = new FileWriter(file);

            writer.write(GameSaver.startingText(game));
            writer.write(System.lineSeparator());

            for(int i = 0; i < game.getPlayedActions().size() - 1; i++) {
                writer.write(game.getPlayedActions().get(i).toString());
                writer.write(System.lineSeparator());
            }
            writer.write(System.lineSeparator());

            writer.write(GameSaver.endingText(game, duration));

            writer.write(System.lineSeparator());
            writer.write(System.lineSeparator());

            writer.write(GameSaver.PGN(game));

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String startingText(Game game) {
        String header = "NEW GAME" + System.lineSeparator() + System.lineSeparator();
        StringBuilder playerIntroductions = new StringBuilder();
        for (Player player: game.getPlayers()) {
            if (player.isControlled()) {
                playerIntroductions.append(player.getColour()).append(" is played by ").append(player.getName());
            }
            else {
                playerIntroductions.append(player.getColour()).append(" is played by the PC at difficulty ").append(player.getDifficulty());
            }
            playerIntroductions.append(System.lineSeparator());
        }
        return header + playerIntroductions;
    }

    private static String endingText(Game game, String duration) {
        String text = "";
        if (game.getWinner() == null) {
            text += "Game goes in remise after "+ game.getTurn() + " turns" + System.lineSeparator();
        }
        else {
            if (game.getWinner().getOpponent().hasResigned()) {
                text += game.getWinner().getOpponent().getName() + " resigns after " + game.getTurn() + " turns" + System.lineSeparator();
            }
            else {
                text += game.getWinner().getName() + " wins after " + game.getTurn() + " turns" + System.lineSeparator();
            }
        }
        text += System.lineSeparator();

        text += "GAME OVER" + System.lineSeparator();
        return text + game.getTurn() + " turns in " + duration + System.lineSeparator();
    }

    private static String PGN(Game game) {
        StringBuilder text = new StringBuilder("PGN: " + System.lineSeparator());

        int turn = 1;
        for (int i = 0; i < game.getPlayedActions().size() - 1; i++) {
            String move;
            if ((i+1)%2 != 0) {
                move = Integer.toString(turn) + ". ";
                turn += 1;
            }
            else {
                move = " ";
            }

            move = move + game.getPlayedAction(i).toPGN();

            if (i%2 != 0) {
                move = move + System.lineSeparator();
            }

            text.append(move);
        }

        return text.toString();
    }

}
