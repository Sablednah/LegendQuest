package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.LevelUpEvent;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

	public Main	lq;

	public PlayerEvents(Main p) {
		this.lq = p;
	}

	// set to monitor - we're not gonna change the login, only load our data
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		Player p =event.getPlayer();
		String pName = p.getName();
		PC pc = lq.players.getPC(pName);
		lq.players.addPlayer(pName, pc);
		p.setTotalExperience(pc.currentXP);
		p.setMaxHealth(pc.maxHP);
		p.setHealth(pc.health);
		pc.healthCheck();
	}

	// set to monitor - we can't change the quit - just want to clean our data up.
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		String pName = event.getPlayer().getName();
		lq.players.removePlayer(pName);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onXPChange(PlayerExpChangeEvent event) {
		Player p = event.getPlayer();
		String pName = p.getName();
		PC pc = lq.players.getPC(pName);
		pc.currentXP = (p.getTotalExperience() + event.getAmount());
		pc.scheduleHealthCheck();
		lq.players.addPlayer(pName, pc);
		lq.players.savePlayer(pc);
		
		if(event.getAmount() >= p.getExpToLevel()) {
			LevelUpEvent e = new LevelUpEvent(p, p.getLevel()+1, pc);
			Bukkit.getServer().getPluginManager().callEvent(e);
		}
	}
}
