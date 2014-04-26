package me.sablednah.legendquest.skills;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EventListener;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.SkillDisableEvent;
import me.sablednah.legendquest.events.SkillEnableEvent;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Skill implements EventListener, Listener {

	public Main					lq;
	public ConfigurationSection	config;
	public Skill				skill	= this;
	public SkillInfo			defaultOptions;

	public abstract boolean onEnable();

	public abstract void onDisable();

	public abstract CommandResult onCommand();

	public final String getSimpleName() {
		return getClass().getSimpleName();
	}

	public final String getName() {
		return defaultOptions.name;
	}

	public final SkillPool getSkillHandler() {
		return lq.skills;
	}

	protected final void initialize(Main plugin) {
		initialize(plugin, new SkillInfo(config));

	}

	protected final void initialize(Main plugin, SkillInfo si) {
		this.lq = plugin;

		// get plugin config
		this.config = lq.configSkills.skillSetings.get(si.name);
		// YamlConfiguration.loadConfiguration(getSkillConfig());
		// si contains manifest/skill details - need to copy over config
		si.readConfigInfo(this.config);
		defaultOptions = si;// new SkillInfo(config);

		System.out.println("Enabling skill " + getName());
		try {
			if (!onEnable()) {
				disable();
				return;
			}
		} catch (Exception e) {
			System.out.println("Error enabling skill " + getName());
			e.printStackTrace();
			disable();
			return;
		}
		
		lq.skills.dispatchEvent(new SkillEnableEvent(this));
		if (Arrays.asList(getClass().getInterfaces()).contains(Listener.class)) {
			Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		}
		lq.skills.getEventDispatcher().add(this);
	}

	public void disable() {
		kill();
	}

	public void reload() {
		lq.skills.reloadSkill(this);
	}

	private final void kill() {
		try {
			onDisable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		lq.skills.getEventDispatcher().remove(this);
		if (Arrays.asList(getClass().getInterfaces()).contains(Listener.class)) {
			HandlerList.unregisterAll(this);
		}
		lq.skills.dispatchEvent(new SkillDisableEvent(this));
		// TODO mark skill as killed - and/or remove
	}

	@SuppressWarnings("unused")
	@Deprecated
	private final File getSkillConfig() {
		File configFile = new File(getSkillDirectory(), getName() + ".yml");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return configFile;
	}

	@Deprecated
	private final File getSkillDirectory() {
		String skillfolder = lq.getDataFolder().getAbsolutePath() + File.separator + "skills";
		File dir = new File(skillfolder, "config");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	// utility functions
	public PC getPC(UUID uuid) {
		return lq.players.getPC(uuid);
	}

	public PC getPC(Player player) {
		return lq.players.getPC(player);
	}

	public void runTask(BukkitRunnable task, long delay, long period) {
		try {
			task.runTaskTimer(this.lq, delay, period);
		} catch (IllegalStateException localIllegalStateException) {
		}
	}

	public boolean validSkillUser(Player p) {
		return getPC(p).validSkill(getName());
	}

	public SkillInfo getDefaultOptions() {
		return defaultOptions;
	}

	public void setDefaultOptions(SkillInfo defaultOptions) {
		this.defaultOptions = defaultOptions;
	}
	public SkillDataStore getPlayerSkillData(Player p) {
		SkillDataStore skillData=null;
		if (p!=null){
			skillData=getPC(p).skillSet.get(getName());
		}
		return skillData;
	}

	public void setPlayerSkillData(SkillDataStore skillData, Player p) {
		if (p!=null){
			skillData=getPC(p).skillSet.put(getName(),skillData);
		}
	}

	public Main getLq() {
		return lq;
	}

	public void setLq(Main lq) {
		this.lq = lq;
	}

}
