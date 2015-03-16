package me.sablednah.legendquest;

/*
*    >>\.
*   /_  )`.
*  /  _)`^)`.   _.---. _
* (_,' \  `^-)""      `.\
*       |              | \
*       \              / |
*      / \  /.___.'\  (\ (_
*     < ,"||     \ |`. \`-'
*      \\ ()      )|  )/
*      |_>|>     /_] //
*        /_]        /_]
*/
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.sablednah.legendquest.classes.Classes;
import me.sablednah.legendquest.cmds.*;
import me.sablednah.legendquest.config.*;
import me.sablednah.legendquest.db.DataSync;
import me.sablednah.legendquest.effects.EffectManager;
import me.sablednah.legendquest.listeners.*;
import me.sablednah.legendquest.party.PartyManager;
import me.sablednah.legendquest.playercharacters.PCs;
import me.sablednah.legendquest.races.Races;
import me.sablednah.legendquest.skills.SkillPool;
import me.sablednah.legendquest.utils.DebugLog;
import me.sablednah.legendquest.utils.ManaTicker;
import me.sablednah.legendquest.utils.plugins.MChatClass;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

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
	public EffectManager		effectManager;
	public PartyManager			partyManager;
	public DebugLog				debug;
	public Scoreboard			board;
	public ScoreboardManager	scoreboard;
	public Objective			objClass;
	public boolean				hasVault;
	public boolean				hasMChat;
	
	// TODO switch test flag for live
	public static final Boolean	debugMode	= false;

	public void log(final String msg) {
	//	logger.info(msg);
		this.getServer().getConsoleSender().sendMessage("[LegendQuest] "+msg);
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
		//disable skills
		skills.shutdown();
		
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

		debug.log.setLevel(Level.parse(configMain.logLevel));
		
		if (configMain.debugMode) {
			debug.setDebugMode();
			debug.log.setLevel(Level.ALL);
		}

		// Get localised text from config
		configLang = new LangConfig(this);

		// Grab the constants
		configData = new DataConfig(this);

		// Notify loading has begun...
		log(configLang.startup);

		//look for vault
		hasVault = this.getServer().getPluginManager().isPluginEnabled("Vault");
        if (hasVault) {
            logger.info("Vault detected.");
        }
		
		//look for mchat
		hasMChat = this.getServer().getPluginManager().isPluginEnabled("MChat");
        if (hasMChat) {
            logger.info("mChat detected.");
            MChatClass.addVars();
        }

        // load skills
		configSkills = new SkillConfig(this);
		skills = new SkillPool(this);
		skills.initSkills();

		// Time to read the Race and class files.
		races = new Races(this);
		classes = new Classes(this);

		// start database data writing task
		datasync = new DataSync(this);

		// Start scoreboard
		/*
		if (configMain.useScoreBoard) {
			scoreboard = this.getServer().getScoreboardManager();
			board = scoreboard.getMainScoreboard();
			objClass = board.registerNewObjective("LQ", "dummy");
			objClass.setDisplaySlot(DisplaySlot.BELOW_NAME);
		}
		*/

		// now load players
		players = new PCs(this);

		// start effect manager
		effectManager = new EffectManager(this);
		
		// start party manager
		partyManager = new PartyManager(this);

		// Lets listen for events shall we?
		final PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerEvents(this), this);
		pm.registerEvents(new DamageEvents(this), this);
		pm.registerEvents(new ItemControlEvents(this), this);
		pm.registerEvents(new AbilityControlEvents(this), this);
		pm.registerEvents(new KarmaMonitorEvents(this), this);
		pm.registerEvents(new AttributeCheckEvent(this), this);
		pm.registerEvents(new SkillLinkEvents(this), this);
		pm.registerEvents(new ChatEvents(this), this);

		// setup commands
		getCommand("lq").setExecutor(new RootCommand(this));
		getCommand("race").setExecutor(new CmdRace(this));
		getCommand("class").setExecutor(new CmdClass(this));
		getCommand("stats").setExecutor(new CmdStats(this));
		getCommand("karma").setExecutor(new CmdKarma(this));
		getCommand("skill").setExecutor(new CmdSkill(this));
		getCommand("roll").setExecutor(new CmdRoll(this));
		getCommand("hp").setExecutor(new CmdHP(this));
		getCommand("party").setExecutor(new CmdParty(this));
		getCommand("link").setExecutor(new CmdLink(this));
		getCommand("unlink").setExecutor(new CmdUnlink(this));
		getCommand("admin").setExecutor(new CmdAdmin(this));

		getCommand("plurals").setExecutor(new CmdPlurals(this));

		getCommand("lq").setTabCompleter(new TabComplete(this));
		getCommand("race").setTabCompleter(new TabComplete(this));
		getCommand("class").setTabCompleter(new TabComplete(this));
		getCommand("skill").setTabCompleter(new TabComplete(this));
		
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
		if (worldList == null) {
			return true;
		}
		if (worldList.isEmpty()) {
			return true;
		}
		return worldList.contains(worldName);
	}
}
