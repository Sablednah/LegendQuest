package me.sablednah.legendquest.utils.plugins;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.config.LangConfig;
import net.milkbowl.vault.economy.Economy;

public class VaultClass {
	public static Economy econ = null;

	public static boolean payCash(int pay, Player p) {
		if (setupEconomy()) {
			Main lq = (Main) Bukkit.getServer().getPluginManager().getPlugin("LegendQuest");
			LangConfig langf = lq.configLang;
			
            double balance = econ.getBalance(p.getName());
            if (balance >= pay) {
            	econ.withdrawPlayer(p.getName(), pay);
                p.sendMessage(pay + econ.currencyNamePlural() + langf.ecoPaid);
                return true;
            } else {
                p.sendMessage(langf.ecoDeclined);
                return false;
            }
		} else {
			return true; // no ecconomy
		}
	}
	
	
    public static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


	public static Double balance(Player p) {
		if (setupEconomy()) {
            return econ.getBalance(p.getName());
		}
		return null;
	}

}
