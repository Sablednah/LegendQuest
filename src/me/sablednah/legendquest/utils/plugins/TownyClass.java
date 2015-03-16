package me.sablednah.legendquest.utils.plugins;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.util.BukkitTools;
import com.palmergames.bukkit.towny.object.TownyPermission;

public class TownyClass {

	public static Boolean canBuild(Block b, Player p) {
		return canBuild(b.getLocation(), p);
	}

	public static Boolean canBuild(Location l, Player p) {
		return PlayerCacheUtil.getCachePermission(p, l, Integer.valueOf(BukkitTools.getTypeId(l.getBlock())), BukkitTools.getData(l.getBlock()), TownyPermission.ActionType.DESTROY);
	}

	public static Boolean canHurt(Player p, Player t) {
		Towny towny = (Towny) Bukkit.getServer().getPluginManager().getPlugin("Towny");
	     return  !(CombatUtil.preventDamageCall(towny, p,t));
	}
}
