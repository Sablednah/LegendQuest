package me.sablednah.legendquest.utils.plugins;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;


public class WorldGuardClass6 {

	public static Boolean canBuild(Block b, Player p) {
		return canBuild(b.getLocation(), p);
	}

	public static Boolean canBuild(Location l, Player p) {
		return WGBukkit.getPlugin().canBuild(p, l);				
	}

	public static Boolean canHurt(Player p, Player t) {
        return WGBukkit.getPlugin().createProtectionQuery().testEntityDamage(p, t);
	}
}
