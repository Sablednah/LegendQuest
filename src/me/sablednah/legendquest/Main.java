package me.sablednah.legendquest;

import java.util.logging.Logger;

import me.sablednah.legendquest.classes.Classes;
import me.sablednah.legendquest.config.LangConfig;
import me.sablednah.legendquest.config.MainConfig;
import me.sablednah.legendquest.db.DataSync;
import me.sablednah.legendquest.playercharacters.PCs;
import me.sablednah.legendquest.races.Races;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public Logger logger;
	public MainConfig configMain;
	public LangConfig configLang;
	public Races races;
	public Classes classes;
	public DataSync datasync;
	public PCs players;
	
	public Main(){
		this.logger = getLogger();
	}

	public void onDisable() {
		
	}

	
	public void onEnable() {
		// Get localises text from config
		configLang = new LangConfig(this);
		
		//Notify loading has begun...
		logger.info(configLang.startup);
		
		//load main core settings
		configMain = new MainConfig(this);
		
		//Time to read the Race and class files.
		races = new Races(this);
		classes = new Classes(this);
		
		//start database data writeing task
		datasync = new DataSync(this);
		
		//now load players
		players = new PCs(this);
		
	}
	
}
