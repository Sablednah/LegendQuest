package me.sablednah.legendquest.utils.plugins;

import java.util.List;

import me.sablednah.legendquest.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/*  sandbox to call plugin specific classes  */
public class PluginUtils {

    public static Boolean canBuild(Location l, Player p) {
        Boolean hasWorldGuard = Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard");
        if (hasWorldGuard) {
            if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().startsWith("5")) {
            	return WorldGuardClass5.canBuild(l, p);
            } else {
            	return WorldGuardClass6.canBuild(l, p);
            }
        }

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
        Boolean hasWorldGuard = Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard");
        if (hasWorldGuard) {
            if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().startsWith("5")) {
            	return WorldGuardClass5.canBuild(b.getLocation(), p);
            } else {
            	return WorldGuardClass6.canBuild(b.getLocation(), p);
            }
        }
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
    	Main lq = (Main) Bukkit.getPluginManager().getPlugin("LegendQuest");
    	if (lq.configMain.useParties && lq.configMain.blockPartyPvP) {
			List<Player> party = lq.partyManager.getPartyMembers(p);
			if (party != null) { // has party
				if (party.size() > 1) { // party has at least 2 people!
					if (party.contains(t)) {
						return false;
					}
//					for (Player pp : party) {
//						if (!(pp.getUniqueId().equals(p.getUniqueId()))) {							
//						}
//					}
				}
			}
    	}
        Boolean hasWorldGuard = Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard");
        if (hasWorldGuard) {
            if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().startsWith("5")) {
            	return WorldGuardClass5.canHurt(p, t);
            } else {
            	return WorldGuardClass6.canHurt(p, t);
            }
        }
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

	public static boolean gainCash(int gain, Player p) {
        Boolean hasVault = Bukkit.getServer().getPluginManager().isPluginEnabled("Vault");
        if (hasVault) {
        	return VaultClass.gainCash(gain, p);
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

	public static boolean canPay(int pay, Player p) {
        Boolean hasVault = Bukkit.getServer().getPluginManager().isPluginEnabled("Vault");
        if (hasVault) {
        	return VaultClass.canPay(pay, p);
        }
		return true;
	}
    
    
}
