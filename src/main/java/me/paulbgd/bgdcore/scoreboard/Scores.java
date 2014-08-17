package me.paulbgd.bgdcore.scoreboard;

import java.util.HashMap;
import org.bukkit.scoreboard.Objective;

public abstract class Scores {

    abstract HashMap<Integer, String> build();

    abstract boolean needsUpdate(Scoreboard scoreboard, Objective objective);

    public Scores clone() throws CloneNotSupportedException {
        return (Scores) super.clone();
    }

}
