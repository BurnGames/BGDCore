package me.paulbgd.bgdcore.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import me.paulbgd.bgdcore.player.PlayerGroup;
import me.paulbgd.bgdcore.player.PlayerWrapper;
import me.paulbgd.bgdcore.player.Players;
import me.paulbgd.bgdcore.reflection.ReflectionField;
import me.paulbgd.bgdcore.reflection.ReflectionObject;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

public class Scoreboard {

    @Getter
    private final List<Scoreboard> children = new ArrayList<>();
    @Getter
    private final Scoreboard parent;
    @Getter
    private final DisplaySlot displaySlot;
    @Getter
    private final List<NameFormatter> formatters = new ArrayList<>();
    @Getter
    private final org.bukkit.scoreboard.Scoreboard bukkitScoreboard;
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private Scores scores;
    private boolean formattersUpdated = false;

    private Objective objective;

    public Scoreboard(Scoreboard parent, DisplaySlot displaySlot, String name, boolean main) {
        this.parent = parent;
        this.displaySlot = displaySlot;
        this.name = name;
        this.bukkitScoreboard = main ? Bukkit.getScoreboardManager().getMainScoreboard() : Bukkit.getScoreboardManager().getNewScoreboard();
        updateObjective();
    }

    public Scoreboard(Scoreboard parent, boolean main) {
        this(parent, parent.getDisplaySlot(), parent.getName(), main);
    }

    public Scoreboard(DisplaySlot displaySlot, String name, boolean main) {
        this(null, displaySlot, name, main);
    }

    public Scoreboard(Scoreboard parent, DisplaySlot displaySlot, String name) {
        this(parent, displaySlot, name, false);
    }

    public Scoreboard(Scoreboard parent) {
        this(parent, parent.getDisplaySlot(), parent.getName());
    }

    public Scoreboard(DisplaySlot displaySlot, String name) {
        this(null, displaySlot, name);
    }

    public Scoreboard createChild() {
        Scoreboard scoreboard = new Scoreboard(this);
        this.children.add(scoreboard);
        return scoreboard;
    }

    public void addFormatter(NameFormatter formatter) {
        this.formatters.add(formatter);
        this.formattersUpdated = true;
    }

    public void removeFormatter(Players players) {
        for (int i = 0; i < formatters.size(); i++) {
            Players formatting = formatters.get(i).getPlayers();
            if (formatting == players) {
                formatters.remove(i);
                this.formattersUpdated = true;
            } else if (formatting instanceof PlayerGroup && players instanceof PlayerWrapper && formatting.getPlayers().contains(players)) {
                formatting.getPlayers().remove(players);
                this.formattersUpdated = true;
            }
        }
    }

    private Objective updateObjective() {
        for (Objective objective : bukkitScoreboard.getObjectives()) {
            objective.unregister();
        }
        this.objective = bukkitScoreboard.registerNewObjective("test", "dummy");
        this.objective.setDisplaySlot(this.findHighest("displaySlot").displaySlot);
        this.objective.setDisplayName(this.findHighest("name").name);
        return this.objective;
    }

    public void build() {
        Scoreboard scoreboard = this;
        while (scoreboard.parent != null) {
            scoreboard = scoreboard.parent;
        }
        scoreboard.build(true);
    }

    private void build(boolean original) {
        update(); // children first
        Scores scores = this.findHighest("scores").getScores();
        if (scores != null) {
            if (scores.needsUpdate(this, this.objective)) {
                this.updateObjective();
                HashMap<Integer, String> hashMap = scores.build();
                for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {
                    this.objective.getScore(entry.getValue()).setScore(entry.getKey());
                }
            }
        }
        if (!this.findHighest("name").name.equals(objective.getDisplayName())) {
            updateObjective();
        }
        Scoreboard highestUpdated = this.findHighest("formattersUpdated");
        if (highestUpdated.formattersUpdated) {
            Set<Team> teams = this.bukkitScoreboard.getTeams();
            for (Team team : teams) {
                team.unregister();
            }
            List<PlayerWrapper> used = new ArrayList<>();
            ArrayList<NameFormatter> all = new ArrayList<>();
            loadFormatters(all);
            for (NameFormatter formatter : all) {
                for (PlayerWrapper player : formatter.getPlayers().getPlayers()) {
                    if (!used.contains(player) && player.getPlayer() != null) {
                        Team team = this.bukkitScoreboard.registerNewTeam(player.getName());
                        team.setPrefix(formatter.getPrefix());
                        team.setSuffix(formatter.getSuffix());
                        team.addPlayer(player.getPlayer());
                        used.add(player);
                    }
                }
            }
        }
        if (original) {
            highestUpdated.formattersUpdated = false;
        }
    }

    private void update() {
        for (Scoreboard child : this.children) {
            child.update();
            child.build(false);
        }

    }

    private Scoreboard findHighest(String field) {
        ReflectionObject reflectionObject = new ReflectionObject(this);
        ReflectionField reflectionField = reflectionObject.getField(field);
        Scoreboard scoreboard = this;
        while (scoreboard != null) {
            Object object = reflectionField.getValue().getObject();
            if (object != null && !object.equals(false)) {
                return scoreboard;
            }
            scoreboard = scoreboard.parent;
        }
        return this;
    }

    private void loadFormatters(ArrayList<NameFormatter> formatters) {
        formatters.addAll(this.formatters);
        if (this.parent != null) {
            parent.loadFormatters(formatters); // load parents after children's
        }
    }

}
