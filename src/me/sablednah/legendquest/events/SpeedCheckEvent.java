package me.sablednah.legendquest.events;

import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpeedCheckEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private PC pc;
    private double speed;
    
    public SpeedCheckEvent(PC pc, double speed) {
        this.pc = pc;
        this.setSpeed(speed);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
 
    public void setCancelled(boolean bln) {
        this.cancelled = bln;
    }
    
    public PC getPC() {
        return pc;
    }

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	
}
