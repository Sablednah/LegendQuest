package me.sablednah.legendquest.races;

import java.util.List;

import org.bukkit.Material;

import me.sablednah.legendquest.skills.SkillDataStore;

public class Race {

	public String				filename;
	public String				name;
	public String				plural;
	public Double				size;
	public boolean				defaultRace;
	public int					statStr;
	public int					statDex;
	public int					statInt;
	public int					statWis;
	public int					statCon;
	public int					statChr;
	public int					baseHealth;

	public float				baseSpeed = 0.2F;
	
	public int					baseMana;
	public double				manaPerSecond;

	public String				perm;

	public List<Material>		allowedTools;
	public List<Material>		allowedArmour;
	public List<Material>		allowedWeapons;

	public List<Material>		dissallowedTools;
	public List<Material>		dissallowedArmour;
	public List<Material>		dissallowedWeapons;

	public boolean				allowCrafting;
	public boolean				allowSmelting;
	public boolean				allowBrewing;
	public boolean				allowEnchating;
	public boolean				allowRepairing;
	public boolean				allowTaming;

	// frequency used for NPC chance.
	public int					frequency;

	// list of race "groups" such as good,evil,orcoid,humanoid.
	// Used by classes as allowed races - e.g. Paladin could be allowed to all good races.
	public List<String>			groups;

	public int					skillPoints;
	public double				skillPointsPerLevel;
	public List<SkillDataStore>	availableSkills;
	public List<SkillDataStore>	outsourcedSkills;

	public double				xpAdjustKill	= 0.0D;
	public double				xpAdjustSmelt	= 0.0D;
	public double				xpAdjustMine	= 0.0D;

}
