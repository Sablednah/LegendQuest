package me.sablednah.legendquest.events;

import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ManaTick extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private PC pc;
    private int mana;
    private int maxMana;
    private int interval;
    private boolean showMessage;
    
    public ManaTick(PC pc, int mana, int maxMana, boolean showMessage, int interval) {
        this.pc = pc;
        this.mana = mana;
        this.maxMana = maxMana;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public PC getPC() {
        return pc;
    }

    public int getMana() {
        return mana;
    }

    public int getInterval() {
        return interval;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public boolean showMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean set) {
        showMessage = set;
    }
    
}
