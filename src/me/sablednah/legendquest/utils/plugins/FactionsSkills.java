package me.sablednah.legendquest.utils.plugins;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.listeners.FactionsListenerMain;
import com.massivecraft.massivecore.ps.PS;


public class FactionsSkills {

	public static Boolean canBuildFactions(Location l, Player p) {
		return canBuildFactions(PS.valueOf(l), p);
	}

	public static Boolean canBuildFactions(Block b, Player p) {
		return canBuildFactions(PS.valueOf(b), p);
	}

	public static Boolean canBuildFactions(PS ps, Player p) {
		if (ps==null) { return false; }
		return FactionsListenerMain.canPlayerBuildAt(p, ps, false);
	}

	public static Boolean canHurt(Player p, Player t) {
	    UPlayer up = UPlayer.get(p);
	    UPlayer ut = UPlayer.get(t);
	    
		Rel rel = up.getRelationTo(ut);
		
		switch(rel) {
			case LEADER:
			case OFFICER: 
			case MEMBER: 
			case RECRUIT: 
			case ALLY: 
			case TRUCE:
				return false;
			case NEUTRAL: 
			case ENEMY:
				return true;
		}		
		return null;
	}
}
