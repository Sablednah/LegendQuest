package me.sablednah.legendquest.races;

import java.util.List;

public class Race {

	public String filename;
	public String name;
	public String plural;
	public Double size;
	public boolean defaultRace;
	public int statStr;
	public int statDex;
	public int statInt;
	public int statWis;
	public int statCon;
	public int statChr;
	public int baseHealth;
	
	public String perm;
	
	// frequency used for NPC chance.
	public int frequency;

	// list of race "groups"  such as good,evil,orcoid,humanoid.  
	// Used by classes as allowes races - e.g. Paladin could be allowed to all good races.  
	public List<String> groups;

}
