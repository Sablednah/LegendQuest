package me.sablednah.legendquest.skills;

import org.bukkit.configuration.ConfigurationSection;

public class LqSkill extends Skill {

	public String command;
	public String commandPermission;
	
	public LqSkill(ConfigurationSection skillInfo) {
		super(skillInfo);
	}	
}
