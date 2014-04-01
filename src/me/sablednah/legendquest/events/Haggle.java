package me.sablednah.legendquest.events;

import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class Haggle extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private InventoryCloseEvent event;
    private PC pc;
    private int charismaMod;
    
    public Haggle(InventoryCloseEvent event, int charismaMod, PC pc) {
        this.event = event;
        this.pc = pc;
        this.charismaMod = charismaMod;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public InventoryCloseEvent getEvent() {
        return event;
    }

    public int getCharismaMod() {
        return charismaMod;
    }

    public PC getPC() {
        return pc;
    }

    public void setEvent(InventoryCloseEvent event) {
        this.event = event;
    }
    public void setCharismaMod(int charismaMod) {
        this.charismaMod = charismaMod;
    }
}
