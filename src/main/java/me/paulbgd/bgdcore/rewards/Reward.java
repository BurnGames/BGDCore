package me.paulbgd.bgdcore.rewards;

import me.paulbgd.bgdcore.player.PlayerWrapper;

public interface Reward {

    public void give(PlayerWrapper playerWrapper);

    public void take(PlayerWrapper playerWrapper);

}
