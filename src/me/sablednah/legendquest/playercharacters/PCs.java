package me.sablednah.legendquest.playercharacters;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.sablednah.legendquest.Main;

public class PCs {
	public Main				lq;
	public Map<String, PC>	activePlayers	= new HashMap<String, PC>();

	public PCs(Main p) {
		this.lq = p;
		for (Player player : lq.getServer().getOnlinePlayers()) {
			String pName = player.getName().toLowerCase();
			PC pc = getPC(player);
			addPlayer(pName, pc);
		}		
	}
	
	public void addPlayer(String pName, PC pc) {
		activePlayers.put(pName, pc);		
	}
	
	public void removePlayer(String pName) {
		savePlayer(pName);
		activePlayers.remove(pName);
	}
	
	public PC getPC(String name) {
		PC pc = null;
		if (name!=null) {
			pc = activePlayers.get(name);
			if (pc == null) {
				pc = loadPlayer(name);
			}
		}
		return pc;		
	}

	public PC getPC(OfflinePlayer p) {
		if (p != null) {
			return getPC(p.getName().toLowerCase());
		}
		return null;
	}

	public PC getPC(Player p) {
		if (p != null) {
			return getPC(p.getName().toLowerCase());
		}
		return null;
	}

	private PC loadPlayer(String pName) {
		// Load player from disk. if not found make new blank
		PC pc = null;
		pc = lq.datasync.getData(pName);
		if (pc == null) {
			pc = new PC();
		}
		return pc;
	}

	public void savePlayer(String name) {
		lq.datasync.addWrite(name, getPC(name));
	}
}
