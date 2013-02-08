package me.sablednah.legendquest.classes;

import java.util.List;

public class ClassType {

	public String			name;
	public String			filename;
	// frequency used for NPC chance.
	public int				frequency;

	// Allowed races for this class
	public List<String>		allowedRaces;
	public List<String>		allowedGroups;
	public List<String>		requires;
	public List<String>		requiresOne;

	public boolean			defaultClass;
	public int				statStr;
	public int				statDex;
	public int				statInt;
	public int				statWis;
	public int				statCon;
	public int				statChr;
	public double			healthPerLevel;

	public String			perm;

	public List<Integer>	allowedTools;
	public List<Integer>	allowedArmour;
	public List<Integer>	allowedWeapons;

	public List<Integer>	dissallowedTools;
	public List<Integer>	dissallowedArmour;
	public List<Integer>	dissallowedWeapons;

}
