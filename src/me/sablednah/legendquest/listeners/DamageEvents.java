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

public class DamageEvents implements Listener {
    
    public Main lq;
    
    public DamageEvents(final Main p) {
        this.lq = p;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamaged(final EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            final Player p = (Player) event.getEntity();
            
            final double dmg = event.getDamage();
            final double newHealth = p.getHealth() - dmg;
            
            final PC pc = lq.players.getPC(p);
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
        if (!lq.configMain.useSkillTestForCombat) { return; }
        Entity victim = event.getEntity();
        PC victimPC = null;
        if (victim instanceof Player) {
            victimPC = lq.players.getPC((Player) victim);
        }
        PC attackerPC = getTwistedInstigator(event.getDamager());
        int hitchance = Difficulty.AVERAGE.getDifficulty();
        int dodgechance = Difficulty.AVERAGE.getDifficulty();
        
        CombatHitCheck e = new CombatHitCheck(event,hitchance,dodgechance);
        lq.getServer().getPluginManager().callEvent(e);
        event = e.getEvent();
        hitchance = e.getHitChance();
        dodgechance = e.getDodgeChance();
        if (e.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        
        boolean hitCheck = Mechanics.opposedTest(attackerPC, hitchance, Attribute.DEX, victimPC, dodgechance, Attribute.DEX);        
        if (!hitCheck) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void checkDammage(EntityDamageEvent event) {
        Entity victim = event.getEntity();
        PC pc = null;
        int dodge = 0;
        int power = 0;
        if (victim instanceof Player) {
            pc = lq.players.getPC((Player) victim);
            dodge = pc.getAttributeModifier(Attribute.DEX);
        }
        if (event instanceof EntityDamageByEntityEvent) {
            pc = getTwistedInstigator(((EntityDamageByEntityEvent) event).getDamager());
            if (pc != null) {
                power = pc.getAttributeModifier(Attribute.STR);
            }
        }

        CombatModifiers e = new CombatModifiers(event,power,dodge);
        lq.getServer().getPluginManager().callEvent(e);
        event = e.getEvent();
        power = e.getPower();
        dodge = e.getDodge();
        if (e.isCancelled()) {
            event.setCancelled(true);
            return;
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
    
}
