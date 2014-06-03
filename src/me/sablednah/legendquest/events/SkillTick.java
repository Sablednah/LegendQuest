package me.sablednah.legendquest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillTick extends Event {

	private static final HandlerList	handlers	= new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private Player			player;

	public SkillTick(Player p) {
		this.player = p;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player p) {
		this.player = p;
	}
}
