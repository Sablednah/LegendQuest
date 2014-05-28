package me.sablednah.legendquest.db;

import java.util.UUID;

public class HealthStore {
	private double health;
	private double maxhealth;
	private UUID uuid;
	/**
	 * @return the health
	 */
	
	public HealthStore(UUID uuid, double health, double maxhealth) {
		this.uuid=uuid;
		this.health=health;
		this.maxhealth=maxhealth;
	}
	
	public double getHealth() {
		return health;
	}
	/**
	 * @param health the health to set
	 */
	public void setHealth(double health) {
		this.health = health;
	}
	/**
	 * @return the maxhealth
	 */
	public double getMaxhealth() {
		return maxhealth;
	}
	/**
	 * @param maxhealth the maxhealth to set
	 */
	public void setMaxhealth(double maxhealth) {
		this.maxhealth = maxhealth;
	}
	/**
	 * @return the uuid
	 */
	public UUID getUuid() {
		return uuid;
	}
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
}
