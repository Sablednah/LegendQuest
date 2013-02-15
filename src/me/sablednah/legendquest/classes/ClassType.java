package me.sablednah.legendquest.classes;

import java.util.ArrayList;
import java.util.List;

import me.sablednah.legendquest.skills.Skill;

public class ClassType {

    public String name;
    public String filename;
    // frequency used for NPC chance.
    public int frequency;

    // Allowed races for this class
    public List<String> allowedRaces;
    public List<String> allowedGroups;
    public List<String> requires;
    public List<String> requiresOne;

    public boolean defaultClass;
    public int statStr;
    public int statDex;
    public int statInt;
    public int statWis;
    public int statCon;
    public int statChr;
    public double healthPerLevel;
    public double manaPerLevel;
    public int manaBonus;
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
    
    public int skillPoints;
    public double skillPointsPerLevel;
    public List<Skill> skills;
    public ArrayList<Skill> availableSkills;

}
