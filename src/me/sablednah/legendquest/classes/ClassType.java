package me.sablednah.legendquest.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import me.sablednah.legendquest.skills.SkillDataStore;

public class ClassType {

	public String						name;
	public String						filename;
	// frequency used for NPC chance.
	public int							frequency;

	// Allowed races for this class
	public List<String>					allowedRaces;
	public List<String>					allowedGroups;
	public List<String>					requires;
	public List<String>					requiresOne;

	public boolean						defaultClass;
	public int							statStr;
	public int							statDex;
	public int							statInt;
	public int							statWis;
	public int							statCon;
	public int							statChr;
	public double						healthPerLevel;
	public double						manaPerLevel;
	public int							manaBonus;
	public int							manaPerSecond;

	public String						perm;

	public List<Material>				allowedTools;
	public List<Material>				allowedArmour;
	public List<Material>				allowedWeapons;

	public List<Material>				dissallowedTools;
	public List<Material>				dissallowedArmour;
	public List<Material>				dissallowedWeapons;

	public boolean						allowCrafting;
	public boolean						allowSmelting;
	public boolean						allowBrewing;
	public boolean						allowEnchating;
	public boolean						allowRepairing;

	public int							skillPoints;
	public double						skillPointsPerLevel;
	public ArrayList<SkillDataStore>	availableSkills;
	public ArrayList<SkillDataStore>	outsourcedSkills;

}
