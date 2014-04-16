package me.sablednah.legendquest.skills;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

public class SkillInfo
{
    
    public String name;
    public SkillType type;
    public String description;
    public String author;
    public double version;
    public String concept;
    public String[] requiredPowers;
    
    public UUID owner;
    
    public int delay;
    public int duration;
    public int cooldown;
    
    public int manaCost;
    public int regentID;
    
    public int levelRequired;
    public int skillPoints;
    
    public SkillInfo(final String author, final String name, final String description, final SkillType type, final double version, final String concept, final String[] requiredPowers)
    {
        this.name = name;
        this.version = version;
        this.type = type;
        this.author = author;
        this.description = description;
        this.concept = concept;
        this.requiredPowers = requiredPowers;
    }
    
    public void readConfigInfo(final ConfigurationSection conf) {
        if (conf == null) {
            this.delay = 0;
            this.duration = 0;
            this.cooldown = 0;
            this.levelRequired = 0;
            this.skillPoints = 0;
        } else {
            //bthis.name = skillInfo.getName();
            this.delay = conf.getInt("delay");
            this.duration = conf.getInt("duration");
            this.cooldown = conf.getInt("cooldown");
            this.levelRequired = conf.getInt("level");
            this.skillPoints = conf.getInt("cost");
        }
        System.out.print("skill info loaded: " + this.name + "|" + this.skillPoints + "|" + this.levelRequired + "|" + this.delay + "|" + this.duration + "|" + this.cooldown);
    }
    
}