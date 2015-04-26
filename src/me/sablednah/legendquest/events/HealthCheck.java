package me.sablednah.legendquest.events;

import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HealthCheck extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private PC pc;
    private double health;
    private double maxHealth;
    
    public HealthCheck(PC pc, double health, double maxHP) {
        this.pc = pc;
        this.health = health;
        this.maxHealth = maxHP;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public PC getPC() {
        return pc;
    }
    public double getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public double getMaxHealth() {
        return maxHealth;
    }
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }
    
}
