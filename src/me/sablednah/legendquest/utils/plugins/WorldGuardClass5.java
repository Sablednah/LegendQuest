package me.sablednah.legendquest.utils.plugins;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;


public class WorldGuardClass5 {

	public static Boolean canBuild(Block b, Player p) {
		return canBuild(b.getLocation(), p);
	}

	public static Boolean canBuild(Location l, Player p) {
		return WGBukkit.getPlugin().canBuild(p, l);				
	}

	@SuppressWarnings("deprecation")
	public static Boolean canHurt(Player p, Player t) {
		Vector loc = BukkitUtil.toVector(p.getLocation());
		ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(p.getWorld()).getApplicableRegions(loc);		
        if (set.getFlag(DefaultFlag.PVP) != null) {
            if (set.getFlag(DefaultFlag.PVP).equals(State.DENY)) {
            	return false;
            }
        }
        return true;
	}
}