package me.sablednah.legendquest.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatModifiers extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private EntityDamageEvent event;
    private int power;
    private int dodge;
    
    public CombatModifiers(EntityDamageEvent event2, int power, int dodge) {
        this.event = event2;
        this.power = power;
        this.dodge = dodge;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public EntityDamageEvent getEvent() {
        return event;
    }

    public int getPower() {
        return power;
    }

    public int getDodge() {
        return dodge;
    }

    public void setEvent(EntityDamageByEntityEvent event) {
        this.event = event;
    }
    public void setPower(int power) {
        this.power = power;
    }

    public void setDodge(int chance) {
        this.dodge = chance;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
 
    public void setCancelled(boolean bln) {
        this.cancelled = bln;
    }
}
