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
	
	public PC (String pName) {
		this.player = pName;
		this.charname = pName;
		this.mainClass = null;
		this.subClass = null;
		this.maxHP = 20;
		this.health = 20;
		this.skillpoints = 0;
	}
}
