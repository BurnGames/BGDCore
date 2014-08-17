package me.paulbgd.bgdcore.player;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;

public class PlayerGroup extends ArrayList<PlayerWrapper> implements Players {
    @Override
    public String getName() {
        return Joiner.on(", ").join(this);
    }

    @Override
    public List<PlayerWrapper> getPlayers() {
        return this;
    }
}
