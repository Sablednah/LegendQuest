package me.sablednah.legendquest.events;

import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LevelUpEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Player player;
    private final int newLevel;

    private final PC pc;

    public LevelUpEvent(final Player player, final int newLevel, final PC pc) {
        this.player = player;
        this.newLevel = newLevel;
        this.pc = pc;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public PC getPc() {
        return pc;
    }

    public Player getPlayer() {
        return player;
    }
}
