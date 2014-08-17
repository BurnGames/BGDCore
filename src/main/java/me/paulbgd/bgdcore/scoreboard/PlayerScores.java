package me.paulbgd.bgdcore.scoreboard;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import me.paulbgd.bgdcore.player.Players;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

public class PlayerScores extends Scores {

    private final Map<String, Integer> scores = new LinkedHashMap<>();

    @Override
    public HashMap<Integer, String> build() {
        HashMap<Integer, String> hashMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            hashMap.put(entry.getValue(), entry.getKey());
        }
        return hashMap;
    }

    @Override
    boolean needsUpdate(Scoreboard scoreboard, Objective objective) {
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (objective.getScore(entry.getKey()).getScore() != entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public void setScore(Players players, int score) {
        this.scores.put(players.getName(), score);
    }

    public void setScore(Player player, int score) {
        this.scores.put(player.getName(), score);
    }

    /**
     * While this is left in for use, consider using LinedScores if you want specific placing.
     *
     * @param string
     * @param score
     */
    @Deprecated
    public void setScore(String string, int score) {
        this.scores.put(string, score);
    }

    public int getScore(Players players) {
        return this.scores.get(players.getName());
    }

    public int getScore(String team) {
        return this.scores.get(team);
    }
}
