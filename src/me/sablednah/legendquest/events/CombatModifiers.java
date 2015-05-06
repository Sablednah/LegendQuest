package me.sablednah.legendquest.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class CombatModifiers extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private int power;
    private int dodge;
	private Entity damager;
	private Entity victim;
	private boolean ranged;
    
    public CombatModifiers( int power, int dodge, Entity damager, Entity victim, boolean ranged) {
        this.power = power;
        this.dodge = dodge;
        this.damager = damager;
        this.victim = victim;
        this.ranged = ranged;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public int getPower() {
        return power;
    }

    public int getDodge() {
        return dodge;
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
    

	/**
	 * @return the damager
	 */
	public Entity getDamager() {
		return damager;
	}

	/**
	 * @param damager the damager to set
	 */
	public void setDamager(Entity damager) {
		this.damager = damager;
	}

	/**
	 * @return the victim
	 */
	public Entity getVictim() {
		return victim;
	}

	/**
	 * @param victim the victim to set
	 */
	public void setVictim(Entity victim) {
		this.victim = victim;
	}
	
	public boolean isRanged() {
		return ranged;
	}

	public void setRanged(boolean ranged) {
		this.ranged = ranged;
	}

}
