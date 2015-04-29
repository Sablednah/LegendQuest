package me.sablednah.legendquest.listeners;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.db.HealthStore;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.events.LevelUpEvent;
import me.sablednah.legendquest.experience.SetExp;
import me.sablednah.legendquest.playercharacters.PC;

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

		public int	xp;
		public UUID	player;

		public delayedSpawn(int xp, UUID player) {
			this.xp = xp;
			this.player = player;
		}

		public void run() {
			PC pc = lq.players.getPC(player);
			pc.setXP(xp);
			lq.players.savePlayer(pc);
			pc.scheduleHealthCheck();
			pc.scheduleXPSave();
		}
	}

	public Main	lq;

	public PlayerEvents(final Main p) {
		this.lq = p;
	}

	// preserve XP on death...
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(final PlayerDeathEvent event) {
		int xp = (int) (event.getDroppedExp() * (lq.configMain.percentXpLossRespawn / 100.0D));
		event.setDroppedExp(xp);
		event.setKeepLevel(true);
		if (Main.debugMode) {
			System.out.print("DIED: " + event.getEntity().getName() + " [" + event.hashCode() + "] " + event.getEventName() + " - " + event.getDeathMessage());
		}
	}

	// set to monitor - we're not gonna change the login, only load our data
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		PC pc = lq.players.getPC(p);
		lq.players.addPlayer(uuid, pc);

		if (!lq.validWorld(p.getWorld().getName())) {
			if (lq.configMain.manageHealthNonLqWorlds) {
				HealthStore hs = lq.datasync.getAltHealthStore(p.getUniqueId());
				if (hs == null || hs.getMaxhealth() < 1) {
					double hp = p.getHealth();
					if (hp > 20.0D) {
						hp = 20.0D;
						p.setHealth(hp);
						p.setMaxHealth(20.0D);
						p.setHealthScale(20.0D);
					}
				} else {
					p.setHealth(hs.getHealth());
					p.setMaxHealth(hs.getMaxhealth());
					p.setHealthScale(20.0D);
				}
			}
		} else {
			p.setTotalExperience(pc.currentXP);
			p.setMaxHealth(pc.maxHP);
			if (pc.getHealth() > pc.maxHP) {
				pc.setHealth(pc.maxHP);
			}
			if (pc.getHealth() > p.getMaxHealth()) {
				pc.setHealth(p.getMaxHealth());
			}
			p.setHealth(pc.getHealth());
			pc.healthCheck();
			p.setWalkSpeed(pc.race.baseSpeed);
		}

		if (Main.debugMode) {
			Location l = p.getLocation();
			Chunk c = l.getChunk();
			int x = c.getX();
			int z = c.getZ();

			for (int i = -10; i < 11; i++) {
				for (int j = -10; j < 11; j++) {
					Chunk chunk = p.getWorld().getChunkAt(x + i, z + j);
					Entity[] ents = chunk.getEntities();
					for (Entity e : ents) {
						if (e.getType() == EntityType.IRON_GOLEM) {
							System.out.print("Removing Golem from:" + i + " , " + j);
							e.remove();
						}
					}
				}
			}
		}
	}

	// set to monitor - we're not gonna change the teleport, only load our data
	// this loads PC data if players swich to/from a non LQ world
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPort(PlayerTeleportEvent event) {
		Player p = event.getPlayer();

		if (!lq.validWorld(event.getTo().getWorld().getName())) {
			if (lq.configMain.manageHealthNonLqWorlds) {
				HealthStore hs = lq.datasync.getAltHealthStore(p.getUniqueId());
				if (hs == null || hs.getMaxhealth() < 1) {
					double hp = p.getHealth();
					if (hp > 20.0D) {
						hp = 20.0D;
						p.setHealth(hp);
						p.setMaxHealth(20.0D);
						p.setHealthScale(20.0D);
					}
				} else {
					p.setHealth(hs.getHealth());
					p.setMaxHealth(hs.getMaxhealth());
					p.setHealthScale(20.0D);
				}
			}
			return;
		} else {
			if (!lq.validWorld(p.getWorld().getName())) {
				// from invalid word to valid world
				// save current health
				HealthStore hs = new HealthStore(p.getUniqueId(), p.getHealth(), p.getMaxHealth());
				lq.datasync.addHPWrite(hs);

				// load character
				UUID uuid = p.getUniqueId();
				PC pc = lq.players.getPC(p);
				lq.players.addPlayer(uuid, pc);
				p.setTotalExperience(pc.currentXP);
				p.setMaxHealth(pc.maxHP);
				p.setHealth(pc.getHealth());
				pc.healthCheck();
				pc.scheduleHealthCheck();
				pc.scheduleCheckInv();

			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPort2(PlayerTeleportEvent event) {
		PC pc = lq.players.getPC(event.getPlayer());
		if (pc !=null) {
			pc.scheduleHealthCheck();
		}
	}

	// set to monitor - we can't change the quit - just want to clean our data up.
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (!lq.validWorld(event.getPlayer().getWorld().getName())) {
			if (lq.configMain.manageHealthNonLqWorlds) {
				HealthStore hs = new HealthStore(uuid, event.getPlayer().getHealth(), event.getPlayer().getMaxHealth());
				lq.datasync.addHPWrite(hs);
			}
		}
		lq.players.removePlayer(uuid);
	}

	// set to monitor - we can't change the quit - just want to clean our data up.
	@EventHandler(priority = EventPriority.MONITOR)
	public void okKick(PlayerKickEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (!lq.validWorld(event.getPlayer().getWorld().getName())) {
			if (lq.configMain.manageHealthNonLqWorlds) {
				HealthStore hs = new HealthStore(uuid, event.getPlayer().getHealth(), event.getPlayer().getMaxHealth());
				lq.datasync.addHPWrite(hs);
			}
		}
		lq.players.removePlayer(uuid);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();

		if (!lq.validWorld(p.getWorld().getName())) {
			if (lq.configMain.manageHealthNonLqWorlds) {
				HealthStore hs = lq.datasync.getAltHealthStore(p.getUniqueId());
				if (hs == null || hs.getMaxhealth() < 1) {
					double hp = p.getHealth();
					if (hp > 20.0D) {
						hp = 20.0D;
						p.setHealth(hp);
						p.setMaxHealth(20.0D);
					}
				} else {
					p.setHealth(hs.getHealth());
					p.setMaxHealth(hs.getMaxhealth());
				}
			}
			return;
		}

		PC pc = lq.players.getPC(p);
		lq.effectManager.removeAllProcess(OwnerType.PLAYER, p.getUniqueId());
		lq.logWarn("currentXP: " + pc.currentXP);
		int currentXP = SetExp.getTotalExperience(p);
		lq.logWarn("totxp: " + currentXP);
		int xpLoss = (int) (currentXP * (lq.configMain.percentXpLossRespawn / 100));
		lq.logWarn("xpLoss: " + xpLoss);
		int newXp = currentXP - xpLoss;
		pc.setXP(newXp);
		// lq.players.savePlayer(pc);
		lq.getServer().getScheduler().runTaskLaterAsynchronously(lq, new delayedSpawn(newXp, pc.uuid), 5);
		p.setWalkSpeed(pc.race.baseSpeed);
	}

	// track EXP changes - and halve then if dual class
	@EventHandler(priority = EventPriority.LOWEST)
	public void onXPChange(PlayerExpChangeEvent event) {
		Player p = event.getPlayer();
		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		double xpAmount = event.getAmount();
		if (Main.debugMode) {
			System.out.print("xpAmount: " + xpAmount);
		}

		UUID uuid = p.getUniqueId();
		PC pc = lq.players.getPC(uuid);
		

		if (Main.debugMode) {
			System.out.print("xpAmount (" + xpAmount + ") p.getExpToLevel(): " + p.getExpToLevel());
		}

		if (xpAmount >= SetExp.getExpUntilNextLevel(p)) {
			// will level up
			if (Main.debugMode) {
				System.out.print("Leveling up: lq.configMain.hardLevelCap = "+lq.configMain.hardLevelCap + " - lq.configMain.max_level > "+lq.configMain.max_level);
			}
			if (lq.configMain.hardLevelCap && lq.configMain.max_level > 0) {
				if (Main.debugMode) {
					System.out.print("Leveling up: p.getLevel() = " + p.getLevel() + " - lq.configMain.max_level > "+lq.configMain.max_level);
				}
				if (p.getLevel() >= lq.configMain.max_level) {
					if (Main.debugMode) {
						System.out.print("zeroing XP");
					}
					event.setAmount(0);
					xpAmount = 0;
				}
			}
		}

		if (xpAmount > 0) {

			// ScaleXP
			xpAmount = (int) (xpAmount * (lq.configMain.scaleXP / 100.0D));

			if (Main.debugMode) {
				System.out.print("ScaleXP (" + lq.configMain.scaleXP + ") xpAmount: " + xpAmount);
			}

			// half xp gain for dual class
			if (pc.subClass != null) {
				xpAmount = xpAmount / 2.0D;
			}
			if (Main.debugMode) {
				System.out.print("subclassed xpAmount: " + xpAmount);
			}

			// useParties
			if (lq.configMain.useParties) {
				List<Player> party = lq.partyManager.getPartyMembers(p);
				if (party != null) { // has party
					if (party.size() > 1) { // party has at least 2 people!
						xpAmount = (xpAmount * (1 + (lq.configMain.partyBonus / 100.0D))); // bonus party XP
						if (Main.debugMode) {
							System.out.print("partyBonus (" + lq.configMain.partyBonus + ") xpAmount: " + xpAmount);
						}
						xpAmount = xpAmount / party.size();
						if (Main.debugMode) {
							System.out.print("partyBonus perperson xpAmount: " + xpAmount);
						}
						if (Main.debugMode) {
							System.out.print("party xpAmount: " + xpAmount);
						}
						for (Player pp : party) {
							if (!(pp.getUniqueId().equals(p.getUniqueId()))) {
								/*
								 * pp.giveExp((int) Math.round(xpAmount)); if (lq.configMain.XPnotify) {
								 * pp.sendMessage(lq.configLang.partyXpChange + xpAmount); }
								 */
								Random rnd = new Random();
								long rndNum = rnd.nextInt(8) + 1;
								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(lq, new DelayedXP(p.getUniqueId(), xpAmount), rndNum);
							}
						}
					}
				}
			}

			pc.scheduleXPSave();

			// lq.players.addPlayer(uuid, pc);
			lq.players.scheduleUpdate(uuid);
			
			event.setAmount((int) Math.round(xpAmount));

			if (xpAmount >= SetExp.getExpUntilNextLevel(p)) {
				pc.scheduleHealthCheck();
				pc.skillSet = pc.getUniqueSkills(true);
				final LevelUpEvent e = new LevelUpEvent(p, p.getLevel() + 1, pc);
				Bukkit.getServer().getPluginManager().callEvent(e);
				if (Main.debugMode) {
					System.out.print("Level UP++");
				}
				lq.players.setLqPerms(pc);
			}
		}
	}

	// track EXP changes - and notify if needed
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onXPNotify(PlayerExpChangeEvent event) {
		if (lq.configMain.XPnotify) {
			event.getPlayer().sendMessage(lq.configLang.xpChange + event.getAmount());
		}
	}

	public class DelayedXP implements Runnable {
		UUID	id		= null;
		double	amount	= 0.0D;

		public DelayedXP(UUID id, double amount) {
			this.id = id;
			this.amount = amount;
		}

		public void run() {
			Bukkit.getServer().getScheduler().runTaskLater(lq, new GiveXP(id, amount), 1L);
		}
	}

	public class GiveXP implements Runnable {
		UUID	id		= null;
		double	amount	= 0.0D;

		public GiveXP(UUID id, double amount) {
			this.id = id;
			this.amount = amount;
		}

		public void run() {
			Player pp = lq.getServer().getPlayer(id);
			if (pp != null) {
				pp.giveExp((int) Math.round(amount));
				if (lq.configMain.XPnotify) {
					pp.sendMessage(lq.configLang.partyXpChange + amount);
				}
			}
		}
	}
}
