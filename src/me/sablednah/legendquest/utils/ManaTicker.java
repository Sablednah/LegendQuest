package me.sablednah.legendquest.utils;

// import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.ManaTick;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ManaTicker implements Runnable {

	public Main			lq;
	private final int	noticeInterval;
	private int			noticeIntervalCounter;

	public ManaTicker(final Main p) {
		this.lq = p;
		noticeInterval = lq.configMain.noticeInterval;  // default 5 seconds
		noticeIntervalCounter = 0;
	}

	public void run() {
		noticeIntervalCounter++;
		
		for (Entry<UUID, PC> pcs : lq.players.activePlayers.entrySet()) {
			PC pc = pcs.getValue();
			int before = pc.mana;
			int maxMana = pc.getMaxMana();
				Player p = lq.getServer().getPlayer(pcs.getKey());
				if (p!=null) {
					if (p.isOnline()) {
						if (!p.isDead()) {
							if (lq.validWorld(p.getWorld().getName())) {
								boolean showMe = false;
//								if (before < maxMana) {
								if (pc.manaGain()) {
									if (maxMana > 1) {
										if (pc.mana > before && pc.mana == maxMana) {
											showMe = true;
										}
										if (noticeIntervalCounter == noticeInterval) { // notice every 5 seconds
											showMe = true;
										}
									}
								}
//								}
								if (lq.configMain.manaTickEvent) {
									ManaTick e = new ManaTick(pc, pc.mana, maxMana, showMe, noticeIntervalCounter);
									Bukkit.getServer().getPluginManager().callEvent(e);

									showMe = e.showMessage();
								}
								if (showMe) {
									final StringBuilder suffix = new StringBuilder();
									suffix.append(" ").append(pc.mana).append(" / ").append(maxMana);
									p.sendMessage(Utils.barGraph(pc.mana, pc.getMaxMana(), 30, lq.configLang.statMana, suffix.toString()));
								}
							}
						}
					
				}
			}
		}
		
		/*
		final Collection<? extends Player> players = lq.getServer().getOnlinePlayers();
		for (final Player p : players) {
			if (lq.validWorld(p.getWorld().getName())) {
				if (!p.isDead()) {
					final PC pc = lq.players.getPC(p);
				}
			}
		}
		*/
		
		if (noticeIntervalCounter == noticeInterval) {
			noticeIntervalCounter = 0;
		}
	}
}
