package me.sablednah.legendquest;

import java.util.logging.Logger;

import me.sablednah.legendquest.classes.Classes;
import me.sablednah.legendquest.cmds.CmdClass;
import me.sablednah.legendquest.cmds.CmdRace;
import me.sablednah.legendquest.cmds.CmdSkill;
import me.sablednah.legendquest.cmds.CmdStats;
import me.sablednah.legendquest.cmds.RootCommand;
import me.sablednah.legendquest.config.DataConfig;
import me.sablednah.legendquest.config.LangConfig;
import me.sablednah.legendquest.config.MainConfig;
import me.sablednah.legendquest.db.DataSync;
import me.sablednah.legendquest.listeners.DamageEvents;
import me.sablednah.legendquest.listeners.ItemControlEvents;
import me.sablednah.legendquest.listeners.PlayerEvents;
import me.sablednah.legendquest.playercharacters.PCs;
import me.sablednah.legendquest.races.Races;
import me.sablednah.legendquest.utils.DebugLog;
import me.sablednah.legendquest.utils.ManaTicker;

import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public Logger			logger;
	public MainConfig		configMain;
	public LangConfig		configLang;
	public DataConfig		configData;
	public Races			races;
	public Classes			classes;
	public DataSync			datasync;
	public PCs				players;
	public DebugLog			debug;

	public static final int	MAX_XP		= 58245;
	public static final int	MAX_LEVEL	= 150;

	public int				bowID		= Material.BOW.getId();
	public int				eggID		= Material.EGG.getId();
	public int				snowballID	= Material.SNOW_BALL.getId();

	public void onDisable() {
		debug.closeLog();
		datasync.shutdown();

		log(configLang.shutdown);
	}

	public void onEnable() {
		this.logger = getLogger();
		getDataFolder().mkdirs();

		// enable debugger.
		debug = new DebugLog(this);

		// TODO remove this in live setup.
		debug.setDebugMode();

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

		// Time to read the Race and class files.
		races = new Races(this);
		classes = new Classes(this);

		// start database data writing task
		datasync = new DataSync(this);

		// now load players
		players = new PCs(this);

		// Let listen for events shall we?
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerEvents(this), this);
		pm.registerEvents(new DamageEvents(this), this);
		pm.registerEvents(new ItemControlEvents(this), this);

		// setup commands
		getCommand("lq").setExecutor(new RootCommand(this));
		getCommand("race").setExecutor(new CmdRace(this));
		getCommand("class").setExecutor(new CmdClass(this));
		getCommand("stats").setExecutor(new CmdStats(this));
		getCommand("skill").setExecutor(new CmdSkill(this));

		// Mana ticker
		this.getServer().getScheduler().runTaskTimer(this, new ManaTicker(this), 20, 20);
		
	}

	public void log(String msg) {
		logger.info(msg);
		debug.info("[serverlog] " + msg);
	}

	public void logSevere(String msg) {
		logger.severe(msg);
		debug.severe("[serverlog] " + msg);
	}

	public void logWarn(String msg) {
		logger.warning(msg);
		debug.warning("[serverlog] " + msg);
	}

}
