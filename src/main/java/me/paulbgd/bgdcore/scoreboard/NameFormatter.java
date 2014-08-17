package me.paulbgd.bgdcore.scoreboard;

import lombok.Data;
import me.paulbgd.bgdcore.player.Players;

@Data
public class NameFormatter {

    private String prefix = "";
    private String suffix = "";

    private final Players players; // this can represent both a team, or a player. So yay?

}
