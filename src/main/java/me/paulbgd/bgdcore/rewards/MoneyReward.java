package me.paulbgd.bgdcore.rewards;

import lombok.RequiredArgsConstructor;
import me.paulbgd.bgdcore.implementations.Implementations;
import me.paulbgd.bgdcore.player.PlayerWrapper;

@RequiredArgsConstructor
public class MoneyReward implements Reward {

    private final double money;

    @Override
    public void give(PlayerWrapper playerWrapper) {
        Implementations.VAULT.addMoney(playerWrapper, money);
    }

    @Override
    public void take(PlayerWrapper playerWrapper) {
        Implementations.VAULT.removeMoney(playerWrapper, money);
    }
}
