package me.sablednah.legendquest.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatHitCheck extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private EntityDamageByEntityEvent event;
    private int hitchance;
    private int dodgechance;
    
    public CombatHitCheck(EntityDamageByEntityEvent event, int hitchance, int dodgechance) {
        this.event = event;
        this.hitchance = hitchance;
        this.dodgechance = dodgechance;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public EntityDamageByEntityEvent getEvent() {
        return event;
    }

    public int getHitChance() {
        return hitchance;
    }

    public int getDodgeChance() {
        return dodgechance;
    }

    public void setEvent(EntityDamageByEntityEvent event) {
        this.event = event;
    }
    public void setHitChance(int chance) {
        this.hitchance = chance;
    }

    public void setDodgeChance(int chance) {
        this.dodgechance = chance;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
 
    public void setCancelled(boolean bln) {
        this.cancelled = bln;
    }
}
