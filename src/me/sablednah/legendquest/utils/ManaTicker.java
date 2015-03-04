package me.sablednah.legendquest.utils;

import java.util.Collection;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.entity.Player;

public class ManaTicker implements Runnable {
    
    public Main lq;
    private final int noticeInterval;
    private int noticeIntervalCounter;
    
    public ManaTicker(final Main p) {
        this.lq = p;
        noticeInterval = 5;
        noticeIntervalCounter = 0;
    }
    
    public void run() {
        noticeIntervalCounter++;
        final Collection<? extends Player> players = lq.getServer().getOnlinePlayers();
        for (final Player p : players) {
            if (!p.isDead()) {
                final PC pc = lq.players.getPC(p);
                if (pc.manaGain()) {
                    if (noticeIntervalCounter == noticeInterval) { // notice every 10 seconds
                        final StringBuilder suffix = new StringBuilder();
                        suffix.append(" ").append(pc.mana).append(" / ").append(pc.getMaxMana());
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
