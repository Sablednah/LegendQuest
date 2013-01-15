package me.sablednah.legendquest;

import java.util.logging.Logger;

import me.sablednah.legendquest.classes.Classes;
import me.sablednah.legendquest.config.LangConfig;
import me.sablednah.legendquest.config.MainConfig;
import me.sablednah.legendquest.db.DataSync;
import me.sablednah.legendquest.playercharacters.PCs;
import me.sablednah.legendquest.races.Races;
import me.sablednah.legendquest.utils.DebugLog;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public Logger logger;
	public MainConfig configMain;
	public LangConfig configLang;
	public Races races;
	public Classes classes;
	public DataSync datasync;
	public PCs players;
	public DebugLog debug;

	public void onDisable() {
		debug.closeLog();
		datasync.shutdown();
		
		log(configLang.shutdown );	
	}

	
	public void onEnable() {
		this.logger = getLogger(); 
		getDataFolder().mkdirs();
		 
		//enable debugger.
		debug = new DebugLog(this);

		// TODO remove this in live setup.
		debug.setDebugMode();

		//load main core settings
		configMain = new MainConfig(this);
		
		if (configMain.debugMode) {
			debug.setDebugMode();
		}
		
		// Get localised text from config
		configLang = new LangConfig(this);
		
		//Notify loading has begun...
		log(configLang.startup);

		//Time to read the Race and class files.
		races = new Races(this);
		classes = new Classes(this);
		
		//start database data writing task
		datasync = new DataSync(this);
		
		//now load players
		players = new PCs(this);
		
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
