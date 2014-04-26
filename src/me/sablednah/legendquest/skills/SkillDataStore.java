package me.sablednah.legendquest.skills;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class SkillDataStore {

	public String					name;
	public SkillType				type;
	public String					description;
	public String					author;
	public double					version;

	public int						buildup				= 0;
	public int						delay				= 0;
	public int						duration			= 0;
	public int						cooldown			= 0;

	public int						manaCost			= 0;
	public ItemStack				consumes			= null;

	public int						levelRequired		= 0;
	public int						skillPoints			= 0;
	public HashMap<String, Object>	vars				= new HashMap<String, Object>();

	public String					permission;
	public String					startCommand;
	public String					endCommand;
	
	public Location					lastUseLoc			= null;

	public long						lastUse				= 0;
	public long						lastBuildupStart	= 0;
	public long						lastDelayStart		= 0;
	public long						lastDurationStart	= 0;
	public long						lastCooldownStart	= 0;
	
	public boolean						isCanceled	= false;
	public boolean						isActive	= false;
	
	public SkillDataStore(ConfigurationSection conf) {
		readConfigInfo(conf);
	}

	public SkillDataStore(SkillInfo defaults) {
		this.name = defaults.name;
		this.version = defaults.version;
		this.type = defaults.type;
		this.author = defaults.author;
		this.description = defaults.description;
		this.buildup = defaults.buildup;
		this.delay = defaults.delay;
		this.duration = defaults.duration;
		this.cooldown = defaults.cooldown;
		this.manaCost = defaults.manaCost;
		this.levelRequired = defaults.levelRequired;
		this.skillPoints = defaults.skillPoints;
		this.consumes = defaults.consumes;
		this.vars = defaults.vars;
	}

	public void readConfigInfo(final ConfigurationSection conf) {
		if (conf != null) {
			// bthis.name = skillInfo.getName();
			if (conf.contains("permission")) {
				this.permission = conf.getString("permission");
			}
			if (conf.contains("startCommand")) {
				this.startCommand = conf.getString("startCommand");
			}
			if (conf.contains("endCommand")) {
				this.endCommand = conf.getString("endCommand");
			}
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
					Object data = (Object) entry.getValue();
					System.out.print("Loading " + this.name + " Skill vars: " + entry.getKey() + " - " + data);
					vars.put(entry.getKey(), data);
				}

			}
		}
		System.out.print("skill dataset loaded: " + this.name + "|" + this.skillPoints + "|" + this.levelRequired + "|" + this.delay + "|" + this.duration + "|" + this.cooldown);
	}

	public SkillPhase checkPhase() {
		if (this.type == SkillType.PASSIVE) {
			return SkillPhase.ACTIVE;
		}

		long time = System.currentTimeMillis();
		long timeline = lastUse;

		if (time < timeline) { // last used in future!?!
			return SkillPhase.READY;
		}

		timeline = timeline + buildup;
		if (time < timeline) {// skill is building up
			return SkillPhase.BUILDING;
		}

		timeline = timeline + delay;
		if (time < timeline) {// skill is delayed
			return SkillPhase.DELAYED;
		}

		timeline = timeline + duration;
		if (time < timeline) {// skill is active
			return SkillPhase.ACTIVE;
		}

		timeline = timeline + cooldown;
		if (time < timeline) {// skill is coolingdown
			return SkillPhase.COOLDOWN;
		}

		return SkillPhase.READY;
	}
}