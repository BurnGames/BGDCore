package me.paulbgd.bgdcore.rewards;

import me.paulbgd.bgdcore.player.PlayerWrapper;

public class EmptyReward implements Reward {
    @Override
    public void give(PlayerWrapper playerWrapper) {
    }

    @Override
    public void take(PlayerWrapper playerWrapper) {
    }
}
