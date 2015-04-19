package me.sablednah.legendquest.events;

import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CoreSkillCheckEvent extends Event {

	private static final HandlerList	handlers	= new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean		valid;
	private PC			pc;
	private CoreSkill	skill;
	private Material	material;
	private EntityType	entitytype;

	public CoreSkillCheckEvent(PC pc, CoreSkill skill, boolean valid) {
		this.pc = pc;
		this.setValid(valid);
		this.skill = skill;
		this.material = null;
		this.entitytype = null;
	}

	public CoreSkillCheckEvent(PC pc, CoreSkill skill, boolean valid, Material material) {
		this.pc = pc;
		this.setValid(valid);
		this.skill = skill;
		this.material = material;
		this.entitytype = null;
	}

	public CoreSkillCheckEvent(PC pc, CoreSkill skill, boolean valid, EntityType entitytype) {
		this.pc = pc;
		this.setValid(valid);
		this.skill = skill;
		this.material = null;
		this.entitytype = entitytype;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * @return the value
	 */
	public boolean isValid() {
		return this.valid;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
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
	public CoreSkill getCoreSkill() {
		return skill;
	}

	/**
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * @return the entitytype
	 */
	public EntityType getEntitytype() {
		return entitytype;
	}

	public enum CoreSkill {
		CRAFT,
		TAME,
		REPAIR,
		ENCHANT,
		BREW,
		SMELT,
		TOOL,
		WEAPON,
		ARMOUR;
	}
}
