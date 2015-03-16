package me.sablednah.legendquest.utils.plugins;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionClass {
	public static Boolean canBuild(Location l, Player p) {
		GriefPrevention gp = GriefPrevention.instance; // (GriefPrevention)
														// Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention");
		if (gp.allowBreak(p, l.getBlock(), l) != null) {
			return false;
		}
		return true;
	}

	public static Boolean canHurt(Player p, Player t) {
		return canPvp(p, t);
	}

	public static boolean canPvp(Player attacker, Player defender) {
		GriefPrevention gp =GriefPrevention.instance; // (GriefPrevention) Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention");
		DataStore dataStore = gp.dataStore;
		if (attacker.hasPermission("griefprevention.nopvpimmunity")) {
			return false;
		}
		if (attacker != defender) {
			PlayerData defenderData = dataStore.getPlayerData(defender.getUniqueId());
			PlayerData attackerData = dataStore.getPlayerData(attacker.getUniqueId());
			if (GriefPrevention.instance.config_pvp_protectFreshSpawns) {
				if (defenderData.pvpImmune) {
					return false;
				}
				if (attackerData.pvpImmune) {
					return false;
				}
			}
			if ((GriefPrevention.instance.config_pvp_noCombatInPlayerLandClaims) || (GriefPrevention.instance.config_pvp_noCombatInAdminLandClaims)) {
				Claim attackerClaim = dataStore.getClaimAt(attacker.getLocation(), false, attackerData.lastClaim);
				if ((attackerClaim != null)
						&& (((attackerClaim.isAdminClaim()) && (attackerClaim.parent == null) && (GriefPrevention.instance.config_pvp_noCombatInAdminLandClaims))
								|| ((attackerClaim.isAdminClaim()) && (attackerClaim.parent != null) && (GriefPrevention.instance.config_pvp_noCombatInAdminSubdivisions)) || ((!attackerClaim.isAdminClaim()) && (GriefPrevention.instance.config_pvp_noCombatInPlayerLandClaims)))) {
					return false;
				}
				Claim defenderClaim = dataStore.getClaimAt(defender.getLocation(), false, defenderData.lastClaim);
				if ((defenderClaim != null)
						&& (((defenderClaim.isAdminClaim()) && (defenderClaim.parent == null) && (GriefPrevention.instance.config_pvp_noCombatInAdminLandClaims))
								|| ((defenderClaim.isAdminClaim()) && (defenderClaim.parent != null) && (GriefPrevention.instance.config_pvp_noCombatInAdminSubdivisions)) || ((!defenderClaim.isAdminClaim()) && (GriefPrevention.instance.config_pvp_noCombatInPlayerLandClaims)))) {
					return false;
				}
			}
		}
		return true;
	}
}
