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
        Boolean hasGriefPrevention = Bukkit.getServer().getPluginManager().isPluginEnabled("GriefPrevention");
        if (hasGriefPrevention) {
        	return GriefPreventionClass.canBuild(l, p);
        }
        Boolean hasTowny = Bukkit.getServer().getPluginManager().isPluginEnabled("Towny");
        if (hasTowny) {
        	return TownyClass.canBuild(l, p);
        }
        return true;
    }

    public static Boolean canBuild(Block b, Player p) {
        Boolean hasFactions = Bukkit.getServer().getPluginManager().isPluginEnabled("Factions");
        if (hasFactions) {
        	return FactionsSkills.canBuildFactions(b, p);
        }
        Boolean hasGriefPrevention = Bukkit.getServer().getPluginManager().isPluginEnabled("GriefPrevention");
        if (hasGriefPrevention) {
        	return GriefPreventionClass.canBuild(b.getLocation(), p);
        }
        Boolean hasTowny = Bukkit.getServer().getPluginManager().isPluginEnabled("Towny");
        if (hasTowny) {
        	return TownyClass.canBuild(b.getLocation(), p);
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
        Boolean hasGriefPrevention = Bukkit.getServer().getPluginManager().isPluginEnabled("GriefPrevention");
        if (hasGriefPrevention) {
        	return GriefPreventionClass.canHurt(p,t);
        }
        Boolean hasTowny = Bukkit.getServer().getPluginManager().isPluginEnabled("Towny");
        if (hasTowny) {
        	return TownyClass.canHurt(p,t);
        }
		return true;
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
