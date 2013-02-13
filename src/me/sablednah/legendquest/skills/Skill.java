package me.sablednah.legendquest.skills;

import org.bukkit.configuration.ConfigurationSection;

public class Skill {
	
	public String name;
	
	public int delay;
	public int duration;
	public int cooldown;
	
	public int manaCost;
	public int regentID;
	
	public int levelRequired;
	public int skillPoints;
	 
	public int lastUsed;
	public int delayCounter;
	public int durationCounter;
	public int cooldownCounter;
	
	
	public Skill(ConfigurationSection skillInfo) {
		name = skillInfo.getName();
		
		delay = skillInfo.getInt("delay");
		duration = skillInfo.getInt("duration");
		cooldown = skillInfo.getInt("cooldown");
		levelRequired = skillInfo.getInt("level");
		skillPoints = skillInfo.getInt("cost");
		
		System.out.print("skill loaded: " + name+"|"+skillPoints+"|"+levelRequired+"|"+delay+"|"+duration+"|"+cooldown);
	}
}
