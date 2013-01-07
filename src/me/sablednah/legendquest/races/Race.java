package me.sablednah.legendquest.races;

import java.util.List;

public class Race {

	public String name;
	public String filename;
	// frequency used for NPC chance.
	public int frequency;

	// list of race "groups"  such as good,evil,orcoid,humanoid.  
	// Used by classes as allowes races - e.g. Paladin could be allowed to all good races.  
	public List<String> groups;

}
