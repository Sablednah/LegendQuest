package me.sablednah.legendquest.events;

import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantSkill extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private EnchantItemEvent event;
    private PC pc;
    private int wisdomMod;
    
    public EnchantSkill(EnchantItemEvent event, int wisdomMod, PC pc) {
        this.event = event;
        this.pc = pc;
        this.wisdomMod = wisdomMod;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public EnchantItemEvent getEvent() {
        return event;
    }

    public int getWisdomMod() {
        return wisdomMod;
    }

    public PC getPC() {
        return pc;
    }

    public void setEvent(EnchantItemEvent event) {
        this.event = event;
    }
    public void setWisdomMod(int wisdomMod) {
        this.wisdomMod = wisdomMod;
    }
}
