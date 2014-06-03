package me.sablednah.legendquest.events;

import me.sablednah.legendquest.mechanics.Attribute;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AbilityCheckEvent extends Event {

	private static final HandlerList	handlers	= new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private int			value;
	private PC			pc;
	private Attribute	attribute;

	public AbilityCheckEvent(PC pc, Attribute att, int value) {
		this.pc = pc;
		this.setValue(value);
		this.attribute = att;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * @return add bonus
	 */
	public int addBonus(int mod) {
		value = value + mod;
		return value;
	}

	/**
	 * @return add bonus
	 */
	public int removePenalty(int mod) {
		value = value - mod;
		return value;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @return the pc
	 */
	public PC getPc() {
		return pc;
	}

	/**
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return attribute;
	}

}
