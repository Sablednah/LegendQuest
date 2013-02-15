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

public class KarmaMonitorEvents implements Listener {

    public Main lq;

    public KarmaMonitorEvents(final Main p) {
        this.lq = p;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

            if ((e.getDamager() instanceof Player)) {
                final Player p = (Player) e.getDamager();
                final PC pc = lq.players.getPC(p);

                Entity target = e.getEntity();
                if (target instanceof Player) {
                    pc.karma -= 5;
                } else if (target instanceof NPC) {
                    pc.karma -= 4;
                } else if (target instanceof Tameable) {
                    pc.karma -= 3;
                } else if (target instanceof Animals) {
                    pc.karma -= 2;
                } else if (target instanceof Monster) {
                    pc.karma += 2;
                } else if (target instanceof Slime) {
                    pc.karma += 1;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        Entity target = event.getEntity();
        EntityDamageEvent e = target.getLastDamageCause();
        
        Entity attacker = null;
        
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) e;
            Entity damager = nEvent.getDamager();
            if (damager instanceof Projectile){
                attacker = ((Projectile)damager).getShooter();
            } else if (damager instanceof Player){
                attacker = (Player)damager;
            } 
        }
        
                
        if ((attacker!= null && attacker instanceof Player)) {
            final Player p = (Player) attacker;
            final PC pc = lq.players.getPC(p);
           
            if (target instanceof Player) {
                pc.karma -= 50;
            } else if (target instanceof NPC) {
                pc.karma -= 40;
            } else if (target instanceof Tameable) {
                pc.karma -= 30;
            } else if (target instanceof Animals) {
                pc.karma -= 20;
            } else if (target instanceof Monster) {
                pc.karma += 20;
            } else if (target instanceof Slime) {
                pc.karma += 10;
            }
        }
    }
}
