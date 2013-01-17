package me.sablednah.legendquest.events;

	import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
	 
	public class LevelUpEvent extends Event {
	    private static final HandlerList handlers = new HandlerList();
	    private Player player;
	 private int newLevel;
	 private PC pc;
	    
	    public LevelUpEvent(Player player, int newLevel, PC pc) {
	        this.player = player;
	        this.newLevel = newLevel;
	        this.pc = pc;
	    }

	 
	    public HandlerList getHandlers() {
	        return handlers;
	    }
	 
	    public static HandlerList getHandlerList() {
	        return handlers;
	    }


		public Player getPlayer() {
			return player;
		}


		public int getNewLevel() {
			return newLevel;
		}


		public PC getPc() {
			return pc;
		}
	}
