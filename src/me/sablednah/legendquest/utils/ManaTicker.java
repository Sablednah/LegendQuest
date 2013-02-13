package me.sablednah.legendquest.utils;

import org.bukkit.entity.Player;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

public class ManaTicker implements Runnable {

	public Main lq;
	private int noticeInterval;
	private int noticeIntervalCounter;
	
	public ManaTicker(Main p) {
		this.lq = p;
		noticeInterval=10;
		noticeIntervalCounter=0;
	}
	
	@Override
	public void run() {
		noticeIntervalCounter++;
		Player[] players = lq.getServer().getOnlinePlayers();
		for (Player p : players) {
			if ( !p.isDead() ) {
				PC pc = lq.players.getPC(p);
				if (pc.manaGain()) {
					if (noticeIntervalCounter==noticeInterval) { //notice every 10 seconds
						StringBuilder suffix = new StringBuilder();
						suffix.append(" ").append(pc.mana).append(" / ").append(pc.getMaxMana());
						p.sendMessage(Utils.barGraph(pc.mana, pc.getMaxMana(), 30, lq.configLang.statMana, suffix.toString()));
					}
				}
				
			}
		}
		if (noticeIntervalCounter==noticeInterval) {
			noticeIntervalCounter=0;
		}
	}
}
