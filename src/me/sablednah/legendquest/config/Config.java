package me.sablednah.legendquest.config;

import java.io.File;
import java.io.IOException;

import me.sablednah.legendquest.Main;

import org.bukkit.configuration.file.YamlConfiguration;


public class Config {
	public Main plugin;
	public String filename;
	private File configFile;
	private YamlConfiguration config;
	
	public Config(Main p, String f) {
		this.plugin = p;
		this.filename = f;
		this.configFile = new File(p.getDataFolder(), this.filename);
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
	}

	public YamlConfiguration getConfig() {
		return config;
	}
	
	public void reloadConfig() {
		this.config = YamlConfiguration.loadConfiguration(this.configFile);		
	}
	
	public void saveConfig() {
		if (this.config == null) {
			return;
		}
		try {
			this.config.save(this.configFile);
		} catch (IOException ex) {
			this.plugin.logger.severe("Could not save config to " + this.filename + " " + ex);
		}
	}
	
	public String getConfigItem(String name, String defaultConfig) {
		System.out.print(name + " is :" + defaultConfig + " str");
		return config.getString(name, defaultConfig);
	}
	public int getConfigItem(String name, int defaultConfig) {
		System.out.print(name + " is :" + defaultConfig + " int");
		return config.getInt(name, defaultConfig);
	}
	public boolean getConfigItem(String name, boolean defaultConfig) {
		System.out.print(name + " is :" + defaultConfig + " boolean");
		return config.getBoolean(name, defaultConfig);
	}
	
}
