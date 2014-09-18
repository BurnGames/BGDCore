package me.paulbgd.bgdcore.implementations;

import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.player.PlayerWrapper;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

public class VaultImplementation implements Implementation {

    private Economy economy;

    public VaultImplementation() {
        try {
            this.economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        } catch (Exception e) {
            BGDCore.debug("Failed to load Vault Economy - " + e.getClass());
        }
    }

    @Override
    public boolean isLoaded() {
        return economy != null;
    }

    public boolean addMoney(PlayerWrapper playerWrapper, double money) {
        return isLoaded() && this.economy.depositPlayer(Bukkit.getOfflinePlayer(playerWrapper.getUniqueId()), Math.abs(money)).transactionSuccess();
    }

    public boolean removeMoney(PlayerWrapper playerWrapper, double money) {
        return isLoaded() && this.economy.withdrawPlayer(Bukkit.getOfflinePlayer(playerWrapper.getUniqueId()), Math.abs(money)).transactionSuccess();
    }

}
