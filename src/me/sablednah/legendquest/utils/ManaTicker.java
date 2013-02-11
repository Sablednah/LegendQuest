package me.sablednah.legendquest.utils;

import org.bukkit.entity.Player;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

public class ManaTicker implements Runnable {

	public Main lq;
	
	public ManaTicker(Main p) {
		this.lq = p;
	}
	
	@Override
	public void run() {
		Player[] players = lq.getServer().getOnlinePlayers();
		for (Player p : players) {
			if ( !p.isDead() ) {
				PC pc = lq.players.getPC(p);
				pc.manaGain();
			}
		}
	}
}
