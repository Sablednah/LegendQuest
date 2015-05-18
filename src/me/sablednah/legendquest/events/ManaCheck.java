package me.sablednah.legendquest.events;

import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ManaCheck extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private PC pc;
    private double maxMana;
    
    public ManaCheck(PC pc, double maxMana) {
        this.pc = pc;
        this.maxMana = maxMana;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public PC getPC() {
        return pc;
    }
    public double getMaxMana() {
        return maxMana;
    }
    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }
    
}
