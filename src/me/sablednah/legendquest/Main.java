package me.sablednah.legendquest;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import me.sablednah.legendquest.classes.Classes;
import me.sablednah.legendquest.cmds.*;
import me.sablednah.legendquest.config.*;
import me.sablednah.legendquest.db.DataSync;
import me.sablednah.legendquest.listeners.*;
import me.sablednah.legendquest.playercharacters.PCs;
import me.sablednah.legendquest.races.Races;
import me.sablednah.legendquest.skills.SkillPool;
import me.sablednah.legendquest.utils.DebugLog;
import me.sablednah.legendquest.utils.ManaTicker;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public Logger				logger;
	public MainConfig			configMain;
	public LangConfig			configLang;
	public DataConfig			configData;
	public SkillConfig			configSkills;
	public SkillPool			skills;
	public Races				races;
	public Classes				classes;
	public DataSync				datasync;
	public PCs					players;
	public DebugLog				debug;

	// TODO switch test flag for live
	public static final Boolean	debugMode	= false;

	public void log(final String msg) {
		logger.info(msg);
		debug.info("[serverlog] " + msg);
	}

	public void logSevere(final String msg) {
		logger.severe(msg);
		debug.severe("[serverlog] " + msg);
	}

	public void logWarn(final String msg) {
		logger.warning(msg);
		debug.warning("[serverlog] " + msg);
	}

	@Override
	public void onDisable() {
		for (Player player : this.getServer().getOnlinePlayers()) {
			UUID uuid = player.getUniqueId();
			players.removePlayer(uuid);
		}
		debug.closeLog();
		datasync.shutdown();

		log(configLang.shutdown);
	}

	@Override
	public void onEnable() {
		this.logger = getLogger();
		getDataFolder().mkdirs();

		// enable debugger.
		debug = new DebugLog(this);

		// load main core settings
		configMain = new MainConfig(this);

		if (configMain.debugMode) {
			debug.setDebugMode();
		}

		// Get localised text from config
		configLang = new LangConfig(this);

		// Grab the constants
		configData = new DataConfig(this);

		// Notify loading has begun...
		log(configLang.startup);

		// load skills
		configSkills = new SkillConfig(this);
		skills = new SkillPool(this);
		skills.initSkills();

		// Time to read the Race and class files.
		races = new Races(this);
		classes = new Classes(this);

		// start database data writing task
		datasync = new DataSync(this);

		// now load players
		players = new PCs(this);

		// Lets listen for events shall we?
		final PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerEvents(this), this);
		pm.registerEvents(new DamageEvents(this), this);
		pm.registerEvents(new ItemControlEvents(this), this);
		pm.registerEvents(new AbilityControlEvents(this), this);
		pm.registerEvents(new KarmaMonitorEvents(this), this);
		pm.registerEvents(new AttributeCheckEvent(this), this);
		pm.registerEvents(new SkillLinkEvents(this), this);

		// setup commands
		getCommand("lq").setExecutor(new RootCommand(this));
		getCommand("race").setExecutor(new CmdRace(this));
		getCommand("class").setExecutor(new CmdClass(this));
		getCommand("stats").setExecutor(new CmdStats(this));
		getCommand("karma").setExecutor(new CmdKarma(this));
		getCommand("skill").setExecutor(new CmdSkill(this));
		getCommand("roll").setExecutor(new CmdRoll(this));
		getCommand("hp").setExecutor(new CmdHP(this));
		getCommand("link").setExecutor(new CmdLink(this));

		getCommand("plurals").setExecutor(new CmdPlurals(this));

		// getCommand("skills").setExecutor(new CmdPlurals(this));
		// getCommand("classes").setExecutor(new CmdPlurals(this));
		// getCommand("races").setExecutor(new CmdPlurals(this));
		// getCommand("links").setExecutor(new CmdPlurals(this));
		// getCommand("binds").setExecutor(new CmdPlurals(this));

		// Mana ticker
		getServer().getScheduler().runTaskTimer(this, new ManaTicker(this), 20, 20);

	}

	public MainConfig getConfigMain() {
		return configMain;
	}

	public void setConfigMain(MainConfig configMain) {
		this.configMain = configMain;
	}

	public LangConfig getConfigLang() {
		return configLang;
	}

	public void setConfigLang(LangConfig configLang) {
		this.configLang = configLang;
	}

	public DataConfig getConfigData() {
		return configData;
	}

	public void setConfigData(DataConfig configData) {
		this.configData = configData;
	}

	public SkillConfig getConfigSkills() {
		return configSkills;
	}

	public void setConfigSkills(SkillConfig configSkills) {
		this.configSkills = configSkills;
	}

	public SkillPool getSkills() {
		return skills;
	}

	public void setSkills(SkillPool skills) {
		this.skills = skills;
	}

	public Races getRaces() {
		return races;
	}

	public void setRaces(Races races) {
		this.races = races;
	}

	public Classes getClasses() {
		return classes;
	}

	public void setClasses(Classes classes) {
		this.classes = classes;
	}

	public PCs getPlayers() {
		return players;
	}

	public void setPlayers(PCs players) {
		this.players = players;
	}

	public boolean validWorld(String worldName) {
		ArrayList<String> worldList = configMain.worlds; 
		if (worldList == null) { return true; }
		if (worldList.isEmpty()) { return true; }
		return worldList.contains(worldName);
	}
}
