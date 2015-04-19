package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.db.HealthStore;
import me.sablednah.legendquest.events.CombatHitCheck;
import me.sablednah.legendquest.events.CombatModifiers;
import me.sablednah.legendquest.experience.ExperienceSource;
import me.sablednah.legendquest.mechanics.Attribute;
import me.sablednah.legendquest.mechanics.Mechanics;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class DamageEvents implements Listener {

	public Main	lq;

	public DamageEvents(Main p) {
		this.lq = p;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamaged(EntityDamageEvent event) {

		if (Main.debugMode) { System.out.print("onDamaged triggered"); }

		if (event.getEntityType() == EntityType.PLAYER) {

			if (Main.debugMode) { System.out.print("onDamaged triggered is player"); }

			Player p = (Player) event.getEntity();

			double dmg = event.getDamage();
			double newHealth = p.getHealth() - dmg;

			if (!lq.validWorld(p.getWorld().getName())) {
				if (lq.configMain.manageHealthNonLqWorlds) {
					HealthStore hs = new HealthStore(p.getUniqueId(), p.getHealth(), p.getMaxHealth());
					lq.datasync.addHPWrite(hs);
				}
				return;
			}

			if (Main.debugMode) { System.out.print("onDamaged triggered - valid lqworld"); }
			
			PC pc = lq.players.getPC(p);
			if (pc != null) {
				pc.setHealth(newHealth);
				if (lq.configMain.debugMode) {
					lq.debug.fine("HP: " + p.getHealth() + " | D: " + dmg + " | nHP: " + newHealth + " | p.max: " + p.getMaxHealth() + " | pc.max: " + pc.maxHP);
					p.sendMessage("HP: " + p.getHealth() + " | D: " + dmg + " | hHP: " + newHealth + " | p.max: " + p.getMaxHealth() + " | pc.max: " + pc.maxHP);
				}
			}
//			lq.players.addPlayer(p.getUniqueId(), pc);
			if (p.getHealth() > 0) {
				pc.scheduleHealthCheck();
				lq.players.scheduleUpdate(p.getUniqueId());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void checkHit(EntityDamageByEntityEvent event) {
		if (!lq.configMain.useSkillTestForCombat) {
			return;
		}
		Entity victim = event.getEntity();

		if (victim instanceof Projectile) { return; }
		
		if (!lq.validWorld(victim.getWorld().getName())) {
			return;
		}

		int hitchance =  lq.configMain.hitchanceenum.getDifficulty(); //Difficulty.AVERAGE.getDifficulty();
		int dodgechance = lq.configMain.dodgechanceenum.getDifficulty(); //Difficulty.AVERAGE.getDifficulty();

		if (Main.debugMode) { System.out.print("hit: "+hitchance + "  dodge: "+dodgechance + "(Start)"); }
		
		PC victimPC = null;
		if (victim instanceof Player) {
			victimPC = lq.players.getPC((Player) victim);
			if (((Player)victim).isBlocking()) {
				hitchance = lq.configMain.blockchanceenum.getDifficulty();				
			}
		}

		if (Main.debugMode) { System.out.print("hit: "+hitchance + "  dodge: "+dodgechance + "(post-dodge)"); }
		
		PC attackerPC = getTwistedInstigator(event.getDamager());

		if (lq.configMain.useSizeForCombat) {
			double atackerSize = lq.players.getSize(this.getTwistedInstigatorEntity(event.getDamager()));
			double defenderSize = lq.players.getSize(victim);

			// 1 difficulty per 0.5D size diference
			int dif = (int) Math.round((defenderSize - atackerSize) / 0.5D);
//positive = bigger target.
			if (dif > 5) {
				dif = 5;
			} else if (dif < -5) {
				dif = -5;
			}
			hitchance = hitchance - dif;
			dodgechance = dodgechance + dif;

			if (Main.debugMode) { System.out.print("SizeMod: "+dif); }

		}
		if (Main.debugMode) { System.out.print("hit: "+hitchance + "  dodge: "+dodgechance + "(post-size)"); }

		boolean ranged = (event.getDamager() instanceof Projectile);

		CombatHitCheck e = new CombatHitCheck(hitchance, dodgechance, this.getTwistedInstigatorPlayer(event.getDamager()), victim, ranged);
		lq.getServer().getPluginManager().callEvent(e);
		hitchance = e.getHitChance();
		dodgechance = e.getDodgeChance();
		if (e.isCancelled()) {
			if (Main.debugMode) { System.out.print("CombatHitCheck - cancelled"); }

			event.setCancelled(true);
			return;
		}

		if (Main.debugMode) { System.out.print("hit: "+hitchance + "  dodge: "+dodgechance + "(post-event)"); }
		
		if (ranged) {
			hitchance=hitchance-lq.configMain.rangedHitBonus;
//			dodgechance=dodgechance-lq.configMain.rangedHitBonus;
			if (Main.debugMode) { System.out.print("ranged: "+lq.configMain.rangedHitBonus); }
		} else {
			if (event.getDamager().getLocation().getBlockY() > victim.getLocation().getBlockY()) {
				hitchance=hitchance-lq.configMain.heightBonus;
				if (Main.debugMode) { System.out.print("heightBonus: "+lq.configMain.heightBonus); }
			}
		}
		if (Main.debugMode) { System.out.print("hit: "+hitchance + "  dodge: "+dodgechance + "(post-raged/height)"); }
		
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
			if (Main.debugMode) { System.out.print("hitCheck - cancelled"); }

			event.setCancelled(true);
		} else {
			if (lq.configMain.verboseCombat && !lq.configMain.hideHitMessage) {
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
	public void checkDammage(EntityDamageByEntityEvent event) {
		Entity victim = event.getEntity();

		if (!lq.validWorld(victim.getWorld().getName())) {
			return;
		}

		Entity damager = null;
		boolean ranged = false;
		PC pc = null;
		int dodge = 0;
		int power = 0;

		// if (event instanceof EntityDamageByEntityEvent) {
		damager = getTwistedInstigatorEntity(((EntityDamageByEntityEvent) event).getDamager());

		if (damager != null && victim!= null && victim.getType()==EntityType.PLAYER  && damager.getType()==EntityType.PLAYER) {
			if (!PluginUtils.canHurt((Player) victim, (Player) damager)) {
				if (Main.debugMode) { System.out.print("canHurt - cancelled"); }

				event.setCancelled(true);
				return;
			}
		}
		
		ranged = (((EntityDamageByEntityEvent) event).getDamager() instanceof Projectile);
		
		pc = getTwistedInstigator(((EntityDamageByEntityEvent) event).getDamager());
		if (pc != null) {
			power = pc.getAttributeModifier(Attribute.STR);
		}

		if (victim instanceof Player) {
			pc = lq.players.getPC((Player) victim);
			dodge = pc.getAttributeModifier(Attribute.DEX);
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
			if (Main.debugMode) { System.out.print("CombatModifiers - cancelled"); }

			event.setCancelled(true);
			return;
		}

		if (Main.debugMode) {
			System.out.print("Power after: " + power);
			System.out.print("Dodge after: " + dodge);
		}

		
		if (ranged) {
	    	if (((EntityDamageByEntityEvent) event).getDamager().getType() == EntityType.ARROW) {	    
			    Arrow arrow = (Arrow)event.getDamager();
			    double speed = arrow.getVelocity().length();
			    if (speed>3.0D) { speed = 3.0D; }
			    power = (int) Math.round((power/3.0D) * speed);  // scale boost by bow power.
				if (Main.debugMode) {
					System.out.print("damage arrow: " + arrow.getVelocity().length());
					System.out.print("power after arrow: " + power);
				}
	    	}
	    }


		double dmg = event.getDamage();

		if (Main.debugMode) {
			System.out.print("damage before stats: " + dmg);
		}
		
		dmg = dmg + power - dodge;
		if (dmg < 0) {
			dmg = 0;
		}
		event.setDamage(dmg);
		
		if (Main.debugMode) {
			System.out.print("damage after stats: " + dmg);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void checkDammagestart(EntityDamageByEntityEvent event) {
		double dmg = event.getDamage();
		if (Main.debugMode) {
			System.out.print("damage before: " + dmg);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void checkDammageEnd(EntityDamageByEntityEvent event) {
		double dmg = event.getDamage();
		if (Main.debugMode) {
			System.out.print("damage end: " + dmg);
		}
	}
	
/*
 	  @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	  public void OnBowFire(EntityShootBowEvent event) {
	    if (event.getProjectile().getType() != EntityType.ARROW) {
	      return;
	    }
	    Arrow arrow = (Arrow)event.getProjectile();
		if (Main.debugMode) {
			System.out.print("damage arrow: " + arrow.getVelocity().length());
		}	    
	  }
*/
	
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
			if (bullit instanceof Entity) {
				return (Entity) bullit.getShooter();
			} else {
				return null;
			}
		}
		return atacker;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void healthGain(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (!lq.validWorld(p.getWorld().getName())) {
				if (lq.configMain.manageHealthNonLqWorlds) {
					HealthStore hs = new HealthStore(p.getUniqueId(), p.getHealth(), p.getMaxHealth());
					lq.datasync.addHPWrite(hs);
				}
				return;
			}

			PC pc = lq.players.getPC(p);
			if (pc != null) {
				pc.scheduleHealthCheck();
				lq.players.scheduleUpdate(p.getUniqueId());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			return;
		}
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity e = (LivingEntity) event.getEntity();
			// EntityDamageEvent cause = e.getLastDamageCause();
			// if (cause == EntityDamageEvent.) {
			Player killer = e.getKiller();
			if (killer != null) {
				PC pc = lq.players.getPC(killer);
				double mod = pc.getXPMod(ExperienceSource.KILL);
				if (mod <= -100.0D) {
					// no experience
					event.setDroppedExp(0);
				} else {
					int xp = event.getDroppedExp();
					xp = (int) (xp * ((100.0D + mod) / 100.0D));
					event.setDroppedExp(xp);
				}
			}
		}
	}
}
