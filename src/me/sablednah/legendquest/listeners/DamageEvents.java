package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.CombatHitCheck;
import me.sablednah.legendquest.events.CombatModifiers;
import me.sablednah.legendquest.mechanics.Attribute;
import me.sablednah.legendquest.mechanics.Difficulty;
import me.sablednah.legendquest.mechanics.Mechanics;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class DamageEvents implements Listener {

	public Main	lq;

	public DamageEvents(Main p) {
		this.lq = p;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamaged(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player p = (Player) event.getEntity();

			if (!lq.validWorld(p.getWorld().getName())) {
				return;
			}

			double dmg = event.getDamage();
			double newHealth = p.getHealth() - dmg;

			PC pc = lq.players.getPC(p);
			if (pc != null) {
				pc.health = newHealth;
				if (lq.configMain.debugMode) {
					lq.debug.fine("HP: " + p.getHealth() + " | D: " + dmg + " | nHP: " + newHealth + " | p.max: " + p.getMaxHealth() + " | pc.max: " + pc.maxHP);
					p.sendMessage("HP: " + p.getHealth() + " | D: " + dmg + " | hHP: " + newHealth + " | p.max: " + p.getMaxHealth() + " | pc.max: " + pc.maxHP);
				}
			}
			lq.players.addPlayer(p.getUniqueId(), pc);
			pc.scheduleHealthCheck();
			lq.players.scheduleUpdate(p.getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void checkHit(EntityDamageByEntityEvent event) {
		if (!lq.configMain.useSkillTestForCombat) {
			return;
		}
		Entity victim = event.getEntity();
		
		if (!lq.validWorld(victim.getWorld().getName())) {
			return;
		}
		
		PC victimPC = null;
		if (victim instanceof Player) {
			victimPC = lq.players.getPC((Player) victim);
		}
		PC attackerPC = getTwistedInstigator(event.getDamager());
		int hitchance = Difficulty.AVERAGE.getDifficulty();
		int dodgechance = Difficulty.AVERAGE.getDifficulty();

		if (lq.configMain.useSizeForCombat) {
			double atackerSize = lq.players.getSize(this.getTwistedInstigatorEntity(event.getDamager()));
			double defenderSize = lq.players.getSize(victim);

			// 1 difficulty per 0.5D size diference
			int dif = (int) Math.round((defenderSize - atackerSize) / 0.5D);

			if (dif > 5) {
				dif = 5;
			} else if (dif < -5) {
				dif = -5;
			}
			hitchance = hitchance + dif;
			dodgechance = dodgechance + dif;
		}

		boolean ranged = (event.getDamager() instanceof Projectile);

		CombatHitCheck e = new CombatHitCheck(hitchance, dodgechance, this.getTwistedInstigatorPlayer(event.getDamager()), victim, ranged);
		lq.getServer().getPluginManager().callEvent(e);
		hitchance = e.getHitChance();
		dodgechance = e.getDodgeChance();
		if (e.isCancelled()) {
			event.setCancelled(true);
			return;
		}
		boolean hitCheck = Mechanics.opposedTest(attackerPC, hitchance, Attribute.DEX, victimPC, dodgechance, Attribute.DEX);
		if (!hitCheck) {
			if (lq.configMain.verboseCombat) {
				if (event.getDamager() instanceof Player) {
					((Player) (event.getDamager())).sendMessage(lq.configLang.combatMissed);
				}
				if (victim instanceof Player) {
					((Player) (victim)).sendMessage(lq.configLang.combatDodged);
				}
			}
			event.setCancelled(true);
		} else {
			if (lq.configMain.verboseCombat) {
				if (event.getDamager() instanceof Player) {
					((Player) (event.getDamager())).sendMessage(lq.configLang.combatHit);
				}
				if (victim instanceof Player) {
					((Player) (victim)).sendMessage(lq.configLang.combatDodgefail);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void checkDammage(EntityDamageEvent event) {
		Entity victim = event.getEntity();
		
		if (!lq.validWorld(victim.getWorld().getName())) {
			return;
		}
		
		Entity damager = null;
		boolean ranged = false;
		PC pc = null;
		int dodge = 0;
		int power = 0;
		if (victim instanceof Player) {
			pc = lq.players.getPC((Player) victim);
			dodge = pc.getAttributeModifier(Attribute.DEX);
		}
		if (event instanceof EntityDamageByEntityEvent) {
			damager = getTwistedInstigatorPlayer(((EntityDamageByEntityEvent) event).getDamager());
			ranged = (((EntityDamageByEntityEvent) event).getDamager() instanceof Projectile);
			pc = getTwistedInstigator(((EntityDamageByEntityEvent) event).getDamager());
			if (pc != null) {
				power = pc.getAttributeModifier(Attribute.STR);
			}
		}
		if (Main.debugMode) {
			System.out.print("power before: " + power);
			System.out.print("dodge before: " + dodge);
		}
		CombatModifiers e = new CombatModifiers(power, dodge, damager, victim, ranged);
		lq.getServer().getPluginManager().callEvent(e);
		power = e.getPower();
		dodge = e.getDodge();
		if (e.isCancelled()) {
			event.setCancelled(true);
			return;
		}
		if (Main.debugMode) {
			System.out.print("power after: " + power);
			System.out.print("dodge after: " + dodge);
		}
		double dmg = event.getDamage();
		dmg = dmg + power - dodge;
		event.setDamage(dmg);
	}

	public PC getTwistedInstigator(Entity atacker) {
		PC pc = null;
		if (atacker instanceof Projectile) {
			Projectile bullit = (Projectile) atacker;
			if (bullit.getShooter() instanceof Player) {
				pc = lq.players.getPC((Player) bullit.getShooter());
			}
		} else if (atacker instanceof Player) {
			pc = lq.players.getPC((Player) atacker);
		}
		return pc;
	}

	public Player getTwistedInstigatorPlayer(Entity atacker) {
		if (atacker instanceof Projectile) {
			Projectile bullit = (Projectile) atacker;
			if (bullit.getShooter() instanceof Player) {
				return (Player) bullit.getShooter();
			}
		} else if (atacker instanceof Player) {
			return (Player) atacker;
		}
		return null;
	}

	public Entity getTwistedInstigatorEntity(Entity atacker) {
		if (atacker instanceof Projectile) {
			Projectile bullit = (Projectile) atacker;
			return (Entity) bullit.getShooter();
		}
		return atacker;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void healthGain(EntityRegainHealthEvent event) {
		if (!lq.validWorld(event.getEntity().getWorld().getName())) {
			return;
		}

		if (event.getEntity() instanceof Player) {
			PC pc = lq.players.getPC((Player) event.getEntity());
			if (pc != null) {
				pc.scheduleHealthCheck();
				lq.players.scheduleUpdate(((Player) event.getEntity()).getUniqueId());
			}
		}
	}
}
