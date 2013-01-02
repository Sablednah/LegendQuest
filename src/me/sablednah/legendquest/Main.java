package me.sablednah.legendquest;

import java.util.logging.Logger;

import me.sablednah.legendquest.config.MainConfig;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public Logger logger;
	public MainConfig main;
	
	public Main(){
		this.logger = getLogger();
	}

	public void onDisable() {
		
	}

	
	public void onEnable() {
		main = new MainConfig(this);
	}
	
}
