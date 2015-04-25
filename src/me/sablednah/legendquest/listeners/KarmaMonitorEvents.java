package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

public class KarmaMonitorEvents implements Listener {

	public Main	lq;

	public KarmaMonitorEvents(final Main p) {
		this.lq = p;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

			if ((e.getDamager() instanceof Player)) {
				final Player p = (Player) e.getDamager();
				if (!lq.validWorld(p.getWorld().getName())) {
					return;
				}
				final PC pc = lq.players.getPC(p);
				if (pc != null) {
					Entity target = e.getEntity();
					if (target instanceof Player) {
						pc.karma += lq.configMain.karmaDamagePlayer;
					} else if (target instanceof NPC) {
						pc.karma += lq.configMain.karmaDamageVillager;
					} else if (target instanceof Tameable) {
						pc.karma += lq.configMain.karmaDamagePet;
					} else if (target instanceof Animals) {
						pc.karma += lq.configMain.karmaDamageAnimal;
					} else if (target instanceof Monster) {
						pc.karma += lq.configMain.karmaDamageMonster;
					} else if (target instanceof Slime) {
						pc.karma += lq.configMain.karmaDamageSlime;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		Entity target = event.getEntity();

		if (!lq.validWorld(target.getWorld().getName())) {
			return;
		}

		EntityDamageEvent e = target.getLastDamageCause();

		ProjectileSource attacker = null;

		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) e;
			Entity damager = nEvent.getDamager();
			if (damager instanceof Projectile) {
				Projectile p = (Projectile) damager;
				attacker = p.getShooter();
			} else if (damager instanceof Player) {
				attacker = (Player) damager;
			}
		}

		if ((attacker != null && attacker instanceof Player)) {
			final Player p = (Player) attacker;
			final PC pc = lq.players.getPC(p);
			if (pc != null) {
				if (target instanceof Player) {
					pc.karma += lq.configMain.karmaKillPlayer;
				} else if (target instanceof NPC) {
					pc.karma += lq.configMain.karmaKillVillager;
				} else if (target instanceof Tameable) {
					pc.karma += lq.configMain.karmaKillPet;
				} else if (target instanceof Animals) {
					pc.karma += lq.configMain.karmaKillAnimal;
				} else if (target instanceof Monster) {
					pc.karma += lq.configMain.karmaKillMonster;
				} else if (target instanceof Slime) {
					pc.karma += lq.configMain.karmaKillSlime;
				}
			}
		}
	}
}
