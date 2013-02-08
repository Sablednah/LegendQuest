package me.sablednah.legendquest.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import me.sablednah.legendquest.Main;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	public Main					lq;
	public String				filename;
	private File				configFile;
	public YamlConfiguration	config;

	public Config(Main p, String f) {
		this.lq = p;
		this.filename = f;
		this.configFile = new File(p.getDataFolder(), this.filename);
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
		if (!configFile.exists()) {
			lq.logWarn("Config file not found, creating default.");
			copyConfigFromJar();
		}
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
			this.lq.logSevere("Could not save config to " + this.filename + " " + ex);
		}
	}

	public String getConfigItem(String name, String defaultConfig) {
		lq.debug.fine(name + " is :" + defaultConfig + " |str");
		config.addDefault(name, defaultConfig);
		String output = config.getString(name, defaultConfig);
		return output;
		}

	public int getConfigItem(String name, int defaultConfig) {
		lq.debug.fine(name + " is :" + defaultConfig + " |int");
		config.addDefault(name, defaultConfig);
		int output = config.getInt(name, defaultConfig);
		return output;
	}

	public boolean getConfigItem(String name, boolean defaultConfig) {
		lq.debug.fine(name + " is :" + defaultConfig + " |boolean");
		config.addDefault(name, defaultConfig);
		boolean output = config.getBoolean(name, defaultConfig);
		return output;
	}
	
	public double getConfigItem(String name, double defaultConfig) {
		lq.debug.fine(name + " is :" + defaultConfig + " |boolean");
		config.addDefault(name, defaultConfig);
		double output = config.getDouble(name, defaultConfig);
		return output;
	}

	public List<?> getConfigItem(String name, List<?> defaultConfig) {
		lq.debug.fine(name + " is :" + defaultConfig + " |list");
		config.addDefault(name, defaultConfig);
		List<?> output = config.getList(name, defaultConfig);
		return output;
	}
	
	public ConfigurationSection getConfigItem(String name) {
		ConfigurationSection output = config.getConfigurationSection(name);
		return output;
	}
	
	public void copyConfigFromJar() {
		OutputStream outStream = null;
		try {
			configFile.createNewFile();
			InputStream templateIn = lq.getResource("res/"+filename);
		    
			outStream = new FileOutputStream(configFile);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = templateIn.read(bytes)) != -1) {
				outStream.write(bytes, 0, read);
			}
			templateIn.close();
			outStream.flush();
			outStream.close();
			lq.log("Config created: " + configFile.getName());
		} catch (Exception e) {
			lq.logWarn("Failed to create file: " + configFile.getName());
			lq.logWarn(e.getMessage());
			if (outStream != null)
				try {
					outStream.flush();
					outStream.close();
				} catch (IOException eio) {
					eio.printStackTrace();
				}
		}
	}
}
