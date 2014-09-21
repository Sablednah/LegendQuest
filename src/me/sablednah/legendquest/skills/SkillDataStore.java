package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;

public class SkillDataStore {

	public String					name;
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
	public List<String>				requires		= new ArrayList<String>();
	public List<String>				requiresOne		= new ArrayList<String>();

	public HashMap<String, Object>	vars			= new HashMap<String, Object>();

	public String					permission;
	public List<String>				permissions;
	public String					startCommand;
	public String					endCommand;

	private Location				lastUseLoc		= null;
	private long					lastUse			= 0;
	private String[]				lastArgs		= null;
	private boolean					isCanceled		= false;
	private boolean					isActive		= false;

	private SkillPhase				phase			= SkillPhase.READY;
	public String					aliasedname		= null;

	public SkillDataStore(ConfigurationSection conf) {
		readConfigInfo(conf);
	}

	public SkillDataStore(final SkillInfo defaults) {
		this.name = defaults.getName();
		this.version = defaults.getVersion();
		this.type = defaults.getType();
		this.author = defaults.getAuthor();
		this.description = defaults.getDescription();
		this.buildup = defaults.getBuildup();
		this.delay = defaults.getDelay();
		this.duration = defaults.getDuration();
		this.cooldown = defaults.getCooldown();
		this.manaCost = defaults.getManaCost();
		this.levelRequired = defaults.getLevelRequired();
		this.skillPoints = defaults.getSkillPoints();
		this.consumes = defaults.getConsumes();
		for (Entry<String, Object> var : defaults.getVars().entrySet()) {
			this.vars.put(var.getKey(), var.getValue());
		}
	}

	public void readConfigInfo(final ConfigurationSection conf) {
		if (conf != null) {
			if (conf.contains("type")) {
				String ty = conf.getString("type");
				this.type = SkillType.valueOf(ty.toUpperCase());
			}
			if (conf.contains("description")) {
				this.description = conf.getString("description");
			}
			if (conf.contains("requires")) {
				this.requires = conf.getStringList("requires");
			}
			if (conf.contains("requiresOne")) {
				this.requiresOne = conf.getStringList("requiresOne");
			}
			if (conf.contains("perm")) {
				this.permission = conf.getString("perm");
			}
			if (conf.contains("perms")) {
				this.permissions = conf.getStringList("perms");
			}
			if (conf.contains("command")) {
				this.startCommand = conf.getString("command");
			}
			if (conf.contains("startcommand")) {
				this.startCommand = conf.getString("startcommand");
			}
			if (conf.contains("endcommand")) {
				this.endCommand = conf.getString("endcommand");
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
					if (data instanceof Double) {
						if (Main.debugMode) {
							System.out.print("Var " + entry.getKey() + ": " + data + " is double");
						}
						this.vars.put(entry.getKey(), (Double) data);
					} else if (data instanceof Integer) {
						if (Main.debugMode) {
							System.out.print("Var " + entry.getKey() + ": " + data + " is integer");
						}
						this.vars.put(entry.getKey(), (Integer) data);
					} else if (data instanceof String) {
						if (Main.debugMode) {
							System.out.print("Var " + entry.getKey() + ": " + data + " is string");
						}
						this.vars.put(entry.getKey(), (String) data);
					} else {
						if (Main.debugMode) {
							System.out.print("Var " + entry.getKey() + ": " + data + " is 'other'");
						}
						this.vars.put(entry.getKey(), data);
					}
				}
			}
		}
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

	public void startperms(Main lq, Player p) {
		if (permission != null && (!permission.isEmpty())) {
//			System.out.print("Processing perm: "+permission);
			if (lq.players.permissions.containsKey(p.getUniqueId().toString() + permission)) {
//				System.out.print("Found: "+p.getUniqueId().toString() + permission);
				if (p.hasPermission(permission)) {
//					System.out.print("Active - will remove: "+permission);
					p.removeAttachment(lq.players.permissions.get(p.getUniqueId().toString() + permission));
				}
				lq.players.permissions.remove(p.getUniqueId().toString() + permission);
			}
			PermissionAttachment attachment = p.addAttachment(lq, permission, true, (int) lq.configMain.skillTickInterval + 1);
//			System.out.print("Added : "+ permission + " - "+ attachment);
			lq.players.permissions.put(p.getUniqueId().toString() + permission, attachment);
		}
		if (permissions != null && (!permissions.isEmpty())) {
			for (String perm : permissions) {
//				System.out.print("Processing perms perm : "+perm);
				if (lq.players.permissions.containsKey(p.getUniqueId().toString() + perm)) {
//					System.out.print("Found: "+p.getUniqueId().toString() + permission);
					if (p.hasPermission(perm)) {
//						System.out.print("Active - will remove: "+ perm);
						p.removeAttachment(lq.players.permissions.get(p.getUniqueId().toString() + perm));
					}
					lq.players.permissions.remove(p.getUniqueId().toString() + perm);
				}
				PermissionAttachment attachment = p.addAttachment(lq, perm, true, (int) lq.configMain.skillTickInterval + 1);
//				System.out.print("Added : "+ perm + " - "+ attachment);
				lq.players.permissions.put(p.getUniqueId().toString() + perm, attachment);				
			}
		}
	}

	public boolean start(Main lq, PC activePlayer) {
		Player p = activePlayer.getPlayer();
		// pay the price...
		if (manaCost > 0) {
			if (!activePlayer.payMana(manaCost)) {
				p.sendMessage(lq.configLang.skillLackOfMana);
				isCanceled = true;
				lastUse = 0;
				lastArgs = null;
				activePlayer.skillSet.put(name, this);
				return false;
			}
		}

		// pay for stuff
		if (consumes != null) {
			if (!activePlayer.payItem(consumes)) {
				p.sendMessage(lq.configLang.skillLackOfItem + Utils.cleanEnumName(consumes));
				isCanceled = true;
				lastUse = 0;
				lastArgs = null;
				activePlayer.skillSet.put(name, this);
				return false;
			}
		}

		// run the start command if any.
		if (startCommand != null && (!startCommand.isEmpty())) {
			lq.getServer().dispatchCommand(p, startCommand);
		}

		Skill skillClass = lq.skills.skillList.get(name.toLowerCase());
		if (skillClass != null) {
			// CommandResult result =
			skillClass.onCommand(p);
		}

		return true;
	}

	public Location getLastUseLoc() {
		return lastUseLoc;
	}

	public void setLastUseLoc(Location lastUseLoc) {
		this.lastUseLoc = lastUseLoc;
	}

	public long getLastUse() {
		return lastUse;
	}

	public void setLastUse(long lastUse) {
		this.lastUse = lastUse;
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public SkillPhase getPhase() {
		return phase;
	}

	public void setPhase(SkillPhase phase) {
		this.phase = phase;
	}

	public String getAliasedname() {
		return aliasedname;
	}

	public void setAliasedname(String aliasedname) {
		this.aliasedname = aliasedname;
	}

	public void setlastArgs(String[] args) {
		this.lastArgs = args;
	}

	public String[] getlastArgs() {
		return this.lastArgs;
	}

}
