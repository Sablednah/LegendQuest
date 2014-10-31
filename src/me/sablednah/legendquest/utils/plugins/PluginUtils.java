package me.sablednah.legendquest.utils.plugins;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/*  sandbox to call plugin specific classes  */
public class PluginUtils {

    public static Boolean canBuild(Location l, Player p) {
        Boolean hasFactions = Bukkit.getServer().getPluginManager().isPluginEnabled("Factions");
        if (hasFactions) {
        	return FactionsSkills.canBuildFactions(l, p);
        }
        return true;
    }

    public static Boolean canBuild(Block b, Player p) {
        Boolean hasFactions = Bukkit.getServer().getPluginManager().isPluginEnabled("Factions");
        if (hasFactions) {
        	return FactionsSkills.canBuildFactions(b, p);
        }
        return true;
    }

    public static void registerBlock(Block b){
    	if (Bukkit.getServer().getPluginManager().isPluginEnabled("CreeperHeal")) {
    		CreeperHealClass.recordBlock(b);
        }
    }
    
    public static Boolean canHurt(Player p, Player t) {
        Boolean hasFactions = Bukkit.getServer().getPluginManager().isPluginEnabled("Factions");
        if (hasFactions) {
        	return FactionsSkills.canHurt(p, t);
        }
    	return false;
    }

	public static boolean payCash(int pay, Player p) {
        Boolean hasVault = Bukkit.getServer().getPluginManager().isPluginEnabled("Vault");
        if (hasVault) {
        	return VaultClass.payCash(pay, p);
        }
		return true;
	}

	public static Double balance(Player p) {
        Boolean hasVault = Bukkit.getServer().getPluginManager().isPluginEnabled("Vault");
        if (hasVault) {
        	return VaultClass.balance(p);
        }
		return null;
	}
    
    
}
