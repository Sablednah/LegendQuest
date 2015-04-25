package me.sablednah.legendquest.utils;

import java.util.Collection;

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
		noticeInterval = 5;
		noticeIntervalCounter = 0;
	}

	public void run() {
		noticeIntervalCounter++;
		final Collection<? extends Player> players = lq.getServer().getOnlinePlayers();
		for (final Player p : players) {
			if (lq.validWorld(p.getWorld().getName())) {
				if (!p.isDead()) {
					final PC pc = lq.players.getPC(p);
					int before = pc.mana;
					int maxMana = pc.getMaxMana();
					boolean showMe = false;
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
					
					ManaTick e = new ManaTick(pc, pc.mana, maxMana, showMe, noticeIntervalCounter);
					Bukkit.getServer().getPluginManager().callEvent(e);

					showMe = e.showMessage();

					if (showMe) {
						final StringBuilder suffix = new StringBuilder();
						suffix.append(" ").append(pc.mana).append(" / ").append(maxMana);
						p.sendMessage(Utils.barGraph(pc.mana, pc.getMaxMana(), 30, lq.configLang.statMana, suffix.toString()));
					}
				}
			}
		}
		if (noticeIntervalCounter == noticeInterval) {
			noticeIntervalCounter = 0;
		}
	}
}
