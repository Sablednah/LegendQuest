package me.sablednah.legendquest.races;

import java.util.List;

import me.sablednah.legendquest.skills.Skill;

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
    public int baseMana;
    public int manaPerSecond;

    public String perm;

    public List<Integer> allowedTools;
    public List<Integer> allowedArmour;
    public List<Integer> allowedWeapons;

    public List<Integer> dissallowedTools;
    public List<Integer> dissallowedArmour;
    public List<Integer> dissallowedWeapons;

    public boolean stopCrafting;
    public boolean stopSmelting;
    public boolean stopBrewing;
    public boolean stopEnchating;
    public boolean stopRepairing;
    
    // frequency used for NPC chance.
    public int frequency;

    // list of race "groups" such as good,evil,orcoid,humanoid.
    // Used by classes as allowes races - e.g. Paladin could be allowed to all good races.
    public List<String> groups;

    public int skillPoints;
    public double skillPointsPerLevel;
    public List<Skill> availableSkills;

}
