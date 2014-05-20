package me.sablednah.legendquest.skills;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import me.sablednah.legendquest.Main;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class SkillInfo {

	private String					name;
	public SkillType				type;
	public String					description;
	public String					author;
	public double					version;

	public int						buildup			= 0;
	public int						delay			= 0;
	public int						duration		= 0;
	public int						cooldown		= 0;
	public int						manaCost		= 0;
	public ItemStack				consumes		= null;
	public int						levelRequired	= 0;
	public int						skillPoints		= 0;

	public HashMap<String, Object>	vars			= new HashMap<String, Object>();

	public SkillInfo(ConfigurationSection conf) {
		readConfigInfo(conf);
	}

	public SkillInfo(String author, String name, String description, SkillType type, double version){
		this.name = name;
		this.version = version;
		this.type = type;
		this.author = author;
		this.description = description;		
	}
	
	public SkillInfo(String author, String name, String description, SkillType type, double version, int buildup, int delay, int duration, int cooldown, int manaCost, ItemStack consumes, int levelRequired, int skillPoints, HashMap<String, Object> vars) {
		this.name = name;
		this.version = version;
		this.type = type;
		this.author = author;
		this.description = description;
		this.buildup = buildup;
		this.delay = delay;
		this.duration = duration;
		this.cooldown = cooldown;
		this.manaCost = manaCost;
		this.levelRequired = levelRequired;
		this.skillPoints = skillPoints;
		if (consumes != null) {
			this.consumes = consumes;
		}
		this.vars = vars;
	}

	public SkillInfo(String author, String name, String description, SkillType type, double version, int buildup, int delay, int duration, int cooldown, int manaCost, String consumes, int levelRequired, int skillPoints, String[] dblnames,
			double[] dblvalues, String[] intnames, int[] intvalues, String[] strnames, String[] strvalues) throws BadSkillFormat {
		this.name = name;
		this.version = version;
		this.type = type;
		this.author = author;
		this.description = description;
		this.buildup = buildup;
		this.delay = delay;
		this.duration = duration;
		this.cooldown = cooldown;
		this.manaCost = manaCost;
		this.levelRequired = levelRequired;
		this.skillPoints = skillPoints;
		if (consumes != null && !consumes.isEmpty()) {
			this.consumes = new ItemStack(Material.getMaterial(consumes));
		}
		readVars(dblnames, dblvalues, intnames, intvalues, strnames, strvalues);
	}

	private void readVars(String[] dblnames, double[] dblvalues, String[] intnames, int[] intvalues, String[] strnames, String[] strvalues) throws BadSkillFormat {
		if (dblnames != null && dblnames.length > 0) {
			for (int i = 0; i < dblnames.length; i++) {
				vars.put(dblnames[i], dblvalues[i]);
			}
		}
		if (intnames != null && intnames.length > 0) {
			for (int i = 0; i < intnames.length; i++) {
				vars.put(intnames[i], intvalues[i]);
			}
		}
		if (strnames != null && strnames.length > 0) {
			for (int i = 0; i < strnames.length; i++) {
				vars.put(strnames[i], strvalues[i]);
			}
		}
	}

	public void readConfigBasicInfo(ConfigurationSection conf) {
		if (conf != null) {
			// bthis.name = skillInfo.getName();
			if (conf.contains("name")) {
				this.name = conf.getString("name");
			}
			if (conf.contains("author")) {
				this.author = conf.getString("author");
			}
			if (conf.contains("description")) {
				this.description = conf.getString("description");
			}
			if (conf.contains("type")) {
				this.type = SkillType.valueOf(conf.getString("type"));
			}
			if (conf.contains("version")) {
				this.version = conf.getInt("version");
			}
		}
	}

	public void readConfigInfo(ConfigurationSection conf) {
		if (conf != null) {
			// bthis.name = skillInfo.getName();
			if (conf.contains("buildup")) {
				this.buildup = conf.getInt("buildup");
			}
			if (conf.contains("delay")) {
				this.delay = conf.getInt("delay");
			}
			if (conf.contains("duration")) {
				this.duration = conf.getInt("duration");
			}
			if (conf.contains("cooldown")) {
				this.cooldown = conf.getInt("cooldown");
			}
			if (conf.contains("level")) {
				this.levelRequired = conf.getInt("level");
			}
			if (conf.contains("cost")) {
				this.skillPoints = conf.getInt("cost");
			}
			if (conf.contains("manaCost")) {
				this.manaCost = conf.getInt("manaCost");
			}
			if (conf.contains("consumes")) {
				this.consumes = new ItemStack(Material.getMaterial(conf.getString("consumes")));
			}
			if (conf.contains("vars")) {
				Map<String, Object> tmpvar = conf.getConfigurationSection("vars").getValues(false);
				Iterator<Entry<String, Object>> entries = tmpvar.entrySet().iterator();
				while (entries.hasNext()) {
					Entry<String, Object> entry = entries.next();
					ConfigurationSection data = (ConfigurationSection) entry.getValue();
					if (Main.debugMode) {
						System.out.print("Loading " + this.name + " Skill vars: " + entry.getKey() + " - " + data);
					}
					vars.put(entry.getKey(), data);
				}

			}
		}
		if (Main.debugMode) {
			System.out.print("skill defaults loaded: " + this.name + "|" + this.skillPoints + "|" + this.levelRequired);
			System.out.print(this.buildup + "|" + this.delay + "|" + this.duration + "|" + this.cooldown);
			System.out.print(this.vars.toString());
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SkillType getType() {
		return type;
	}

	public void setType(SkillType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public double getVersion() {
		return version;
	}

	public void setVersion(double version) {
		this.version = version;
	}

	public int getBuildup() {
		return buildup;
	}

	public void setBuildup(int buildup) {
		this.buildup = buildup;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public int getManaCost() {
		return manaCost;
	}

	public void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}

	public ItemStack getConsumes() {
		return consumes;
	}

	public void setConsumes(ItemStack consumes) {
		this.consumes = consumes;
	}

	public int getLevelRequired() {
		return levelRequired;
	}

	public void setLevelRequired(int levelRequired) {
		this.levelRequired = levelRequired;
	}

	public int getSkillPoints() {
		return skillPoints;
	}

	public void setSkillPoints(int skillPoints) {
		this.skillPoints = skillPoints;
	}

	public HashMap<String, Object> getVars() {
		return vars;
	}

	public void setVars(HashMap<String, Object> vars) {
		this.vars = vars;
	}
	
}