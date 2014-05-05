package me.sablednah.legendquest.listeners;

import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.LevelUpEvent;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.SetExp;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerEvents implements Listener {

	public class delayedSpawn implements Runnable {

		public int		xp;
		public Player	player;

		public delayedSpawn(int xp, Player player) {
			this.xp = xp;
			this.player = player;
		}

		public void run() {
			UUID uuid = player.getUniqueId();
			PC pc = lq.players.getPC(uuid);
			pc.setXP(xp);
			lq.players.savePlayer(pc);
			pc.scheduleHealthCheck();
		}
	}

	public Main	lq;

	public PlayerEvents(final Main p) {
		this.lq = p;
	}

	// preserve XP on death...
	@EventHandler(priority = EventPriority.LOW)
	public void onDeath(final PlayerDeathEvent event) {
		event.setDroppedExp(0);
		event.setKeepLevel(true);
	}

	// set to monitor - we're not gonna change the login, only load our data
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		UUID uuid = p.getUniqueId();
		PC pc = lq.players.getPC(p);
		lq.players.addPlayer(uuid, pc);
		p.setTotalExperience(pc.currentXP);
		p.setMaxHealth(pc.maxHP);
		p.setHealth(pc.health);
		pc.healthCheck();

		Location l = p.getLocation();
		Chunk c = l.getChunk();

		int x = c.getX();
		int z = c.getZ();

		if (Main.debugMode) {
			for (int i = -10; i < 11; i++) {
				for (int j = -10; j < 11; j++) {
					Chunk chunk = p.getWorld().getChunkAt(x + i, z + j);
					Entity[] ents = chunk.getEntities();
					for (Entity e : ents) {
						if (e.getType() == EntityType.IRON_GOLEM) {
							System.out.print("removing Golem from:" + i + " , " + j);
							e.remove();
						}
					}
				}
			}
		}
	}
	
	// set to monitor - we're not gonna change the port, only load our data
	// this loads PC data if players swich to/from a non LQ world 
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPort(PlayerTeleportEvent event) {
		Player p = event.getPlayer();

		if (!lq.validWorld(event.getTo().getWorld().getName())) {
			return;
		} else {
			if (!lq.validWorld(p.getWorld().getName())) {
				//from invalid word to valid world
				UUID uuid = p.getUniqueId();
				PC pc = lq.players.getPC(p);
				lq.players.addPlayer(uuid, pc);
				p.setTotalExperience(pc.currentXP);
				p.setMaxHealth(pc.maxHP);
				p.setHealth(pc.health);
				pc.healthCheck();
			}
		}
	}
	
	// set to monitor - we can't change the quit - just want to clean our data up.
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		lq.players.removePlayer(uuid);
	}

	// set to monitor - we can't change the quit - just want to clean our data up.
	@EventHandler(priority = EventPriority.MONITOR)
	public void okKick(PlayerKickEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		lq.players.removePlayer(uuid);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}
		
		PC pc = lq.players.getPC(p);
		lq.logWarn("currentXP: " + pc.currentXP);
		int currentXP = SetExp.getTotalExperience(p);
		lq.logWarn("totxp: " + currentXP);
		int xpLoss = (int) (currentXP * (lq.configMain.percentXpLossRespawn / 100));
		lq.logWarn("xpLoss: " + xpLoss);
		int newXp = currentXP - xpLoss;
		pc.setXP(newXp);
		lq.players.savePlayer(pc);
		lq.getServer().getScheduler().runTaskLater(lq, new delayedSpawn(newXp, p), 5);
	}

	// track EXP changes - and halve then if dual class
	@EventHandler(priority = EventPriority.LOW)
	public void onXPChange(PlayerExpChangeEvent event) {
		Player p = event.getPlayer();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		int xpAmount = event.getAmount();

		UUID uuid = p.getUniqueId();
		PC pc = lq.players.getPC(uuid);

		// half xp gain for dual class
		if (pc.subClass != null) {
			xpAmount = xpAmount / 2;
		}
		pc.setXP(SetExp.getTotalExperience(p) + xpAmount);
		lq.players.addPlayer(uuid, pc);
		lq.players.savePlayer(pc);

		if (xpAmount >= p.getExpToLevel()) {
			pc.scheduleHealthCheck();
			final LevelUpEvent e = new LevelUpEvent(p, p.getLevel() + 1, pc);
			Bukkit.getServer().getPluginManager().callEvent(e);
		}
	}
}
