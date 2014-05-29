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

    public Main lq;
    public String filename;
    private final File configFile;
    public YamlConfiguration config;

    public Config(final Main p, final String f) {
        this.lq = p;
        this.filename = f;
        this.configFile = new File(p.getDataFolder(), this.filename);
        if (!configFile.exists()) {
            lq.logWarn("Config file not found, creating default.");
            copyConfigFromJar();
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public void copyConfigFromJar() {
        OutputStream outStream = null;
        try {
            configFile.createNewFile();
            final InputStream templateIn = lq.getResource("res/" + filename);

            outStream = new FileOutputStream(configFile);
            int read = 0;
            final byte[] bytes = new byte[1024];
            while ((read = templateIn.read(bytes)) != -1) {
                outStream.write(bytes, 0, read);
            }
            templateIn.close();
            outStream.flush();
            outStream.close();
            lq.log("Config created: " + configFile.getName());
        } catch (final Exception e) {
            lq.logWarn("Failed to create file: " + configFile.getName());
            lq.logWarn(e.getMessage());
            if (outStream != null) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (final IOException eio) {
                    eio.printStackTrace();
                }
            }
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public ConfigurationSection getConfigItem(final String name) {
        final ConfigurationSection output = config.getConfigurationSection(name);
        return output;
    }

    public boolean getConfigItem(final String name, final boolean defaultConfig) {
        config.addDefault(name, defaultConfig);
        final boolean output = config.getBoolean(name, defaultConfig);
        lq.debug.fine(name + " is: " + output + " [boolean]");
        return output;
    }

    public double getConfigItem(final String name, final double defaultConfig) {
        config.addDefault(name, defaultConfig);
        final double output = config.getDouble(name, defaultConfig);
        lq.debug.fine(name + " is: " + output + " [double]");
        return output;
    }

    public int getConfigItem(final String name, final int defaultConfig) {
        config.addDefault(name, defaultConfig);
        final int output = config.getInt(name, defaultConfig);
        lq.debug.fine(name + " is: " + output + " [int]");
        return output;
    }

    public List<?> getConfigItem(final String name, final List<?> defaultConfig) {
        config.addDefault(name, defaultConfig);
        final List<?> output = config.getList(name, defaultConfig);
        lq.debug.fine(name + " is: " + output + " [list]");
        return output;
    }

    public String getConfigItem(final String name, final String defaultConfig) {
        config.addDefault(name, defaultConfig);
        final String output = config.getString(name, defaultConfig);
        lq.debug.fine(name + " is: " + output + " [string]");
        return output;
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
        } catch (final IOException ex) {
            this.lq.logSevere("Could not save config to " + this.filename + " " + ex);
        }
    }
}
