package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
            final int dmg = event.getDamage();
            final int newHealth = p.getHealth() - dmg;

            final PC pc = lq.players.getPC(p);
            if (pc != null) {
                pc.health = newHealth;
                lq.debug.fine("HP: " + p.getHealth() + " | D: " + dmg + " | nHP: " + newHealth + " | p.max: " + p.getMaxHealth() + " | pc.max: " + pc.maxHP);
                p.sendMessage("HP: " + p.getHealth() + " | D: " + dmg + " | hHP: " + newHealth + " | p.max: " + p.getMaxHealth() + " | pc.max: " + pc.maxHP);

            }

            lq.players.addPlayer(p.getName(), pc);
            pc.scheduleHealthCheck();
            lq.players.scheduleUpdate(p.getName());

        }
    }
}
