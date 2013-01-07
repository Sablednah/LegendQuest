package me.sablednah.legendquest.playercharacters;

import java.util.HashMap;

import me.sablednah.legendquest.classes.ClassType;

public class PC {
	public String charname;
	public String player;
	public ClassType mainClass;
	public ClassType subClass;
	public HashMap<String,Integer> xpEarnt = new HashMap<String, Integer>();
	public int maxHP;
	public int health;
	public int skillpoints;
	
}
