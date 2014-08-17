package me.paulbgd.bgdcore.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;

public class LinedScores extends Scores {

    @Getter
    private final List<String> lines = new ArrayList<>();

    public void addLine(String line) {
        this.lines.add(line);
    }

    @Override
    HashMap<Integer, String> build() {
        HashMap<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            hashMap.put(lines.size() - i, line == null ? ChatColor.values()[i].toString() : line);
        }
        return hashMap;
    }

    @Override
    boolean needsUpdate(Scoreboard scoreboard, Objective objective) {
        Set<String> entries = scoreboard.getBukkitScoreboard().getEntries();
        String[] array = entries.toArray(new String[entries.size()]);
        boolean needsUpdate = false;

        if (this.lines.size() != entries.size()) {
            needsUpdate = true;
        } else {
            for (int i = 0; i < this.lines.size(); i++) {
                if (!lines.get(i).equals(array[array.length - i])) {
                    needsUpdate = true; // uh oh! A line doesn't match
                    break;
                }
            }
        }
        return needsUpdate;
    }
}
