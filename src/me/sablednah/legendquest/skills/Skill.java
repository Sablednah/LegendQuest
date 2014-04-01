package me.sablednah.legendquest.skills;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.SkillDisableEvent;
import me.sablednah.legendquest.events.SkillEnableEvent;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public abstract class Skill implements EventListener, Listener {
    
    public int lastUsed;
    public int delayCounter;
    public int durationCounter;
    public int cooldownCounter;
    
    protected Main lq;
    private SkillPool parent;
    private SkillInfo info;
    protected FileConfiguration config;

    public Skill skill = this;
    
    private Map<String, Object> options = new HashMap<String, Object>();
    
    public ItemStack[] ACT = new ItemStack[5];
    public boolean[] BLN = new boolean[10];
    public double[] DBL = new double[5];
    public int[] INT = new int[5];
    public ItemStack[] ITEM = new ItemStack[5];
    public int[] TIME = new int[5];
    
  
    public abstract boolean onEnable();
    
    public abstract void loadOptions();
    
    public void onDisable() {}
    
    public abstract CommandResult onCommand();
    
    public final SkillInfo getInfo() {
        return this.info;
    }

    public final void setInfo(SkillInfo inf) {
        this.info = inf;
    }
    
    public final String getTag() {
        return getClass().getSimpleName();
    }
    
    public final SkillPool getSkillHandler() {
        return this.parent;
    }
    
    protected final void initialize(Main plugin, SkillPool parent, SkillInfo info, ConfigurationSection conf) {
        this.parent = parent;
        this.lq = plugin;
        this.info = info;
        this.config = YamlConfiguration.loadConfiguration(getSkillConfig());
        
        System.out.println("Enabling skill " + info.name);        
        try {
            if (!onEnable()) {
                disable();
                return;
            }
        } catch (Exception e) {
            System.out.println("Error enabling skill " + info.name);
            e.printStackTrace();
            disable();
            return;
        }
        loadOptions();
        parent.dispatchEvent(new SkillEnableEvent(this));
        if (Arrays.asList(getClass().getInterfaces()).contains(Listener.class)) {
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        }
        parent.getEventDispatcher().add(this);
    }



    
    public void disable() {
        kill();
    }
    
    public void reload() {
        this.parent.reloadSkill(this);
    }
    
    private final void kill() {
        try {
            onDisable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.parent.getEventDispatcher().remove(this);
        if (Arrays.asList(getClass().getInterfaces()).contains(Listener.class)) {
            HandlerList.unregisterAll(this);
        }
        this.parent.dispatchEvent(new SkillDisableEvent(this));
        // TODO mark skill as killed - and/or remove
    }
    
    private final File getSkillConfig() {
        File configFile = new File(getSkillDirectory(), getTag() + ".yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return configFile;
    }
    
    private final File getSkillDirectory() {
        String skillfolder = lq.getDataFolder().getAbsolutePath() + File.separator + "skills";
        File dir = new File(skillfolder, "config");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    
    public void setUser(String name) {
       info.owner = name;
    }

    public void setUser(Player name) {
        setUser(name.getName());
    }
    
    public PC getUser() {
        return lq.players.getPC(info.owner);
    }
    
    public Player getUserPlayer() {
        return Bukkit.getPlayer(info.owner);
    }
    
    public PC getPC(String player) {
        return lq.players.getPC(player);
    }
    
    public PC getPC(Player player) {
        return lq.players.getPC(player);
    }
    
    public boolean option(String option, boolean value) {
        checkOption(option, Boolean.valueOf(value));
        return this.config.getBoolean(option, value);
    }
    
    public double option(String option, double value) {
        checkOption(option, Double.valueOf(value));
        return this.config.getDouble(option, value);
    }
    
    public int option(String option, int value) {
        checkOption(option, Integer.valueOf(value));
        return this.config.getInt(option, value);
    }
    
    public int option(String option, LQTime value) {
        checkOption(option, value.asString());
        return new LQTime(this.config.getString(option, value.asString())).toTicks();
    }
    
    public ItemStack option(String option, ItemStack value) {
        checkOption(option, value);
        return this.config.getItemStack(option, value);
    }
    
    public long option(String option, long value) {
        checkOption(option, Long.valueOf(value));
        return this.config.getLong(option, value);
    }
    
    public String option(String option, String value) {
        checkOption(option, value);
        return this.config.getString(option, value);
    }
    
    public Vector option(String option, Vector value) {
        checkOption(option, value);
        return this.config.getVector(option, value);
    }
    
    private void checkOption(String option, Object value) {
        if (!this.config.contains(option)) {
            this.config.createSection(option);
            this.config.set(option, value);
            saveConfig();
        } else {
            value = this.config.get(option);
        }
        this.options.put(option, value);
    }
    
    public Object getOption(String option) {
        if (this.options.containsKey(option)) {
            return this.options.get(option);
        }
        return null;
    }
    
    public void runTask(BukkitRunnable task, long delay, long period) {
        try {
            task.runTaskTimer(this.lq, delay, period);
        } catch (IllegalStateException localIllegalStateException) {}
    }
    
    public void saveConfig() {
        try {
            this.config.save(getSkillConfig());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setOption(String option, Object value) {
        if (!this.config.contains(option)) {
            this.config.createSection(option);
        }
        this.config.set(option, value);
        this.options.put(option, value);
        saveConfig();
    }
    
    public boolean validSkillUser(Player p){
        return getPC(p).validSkill(info.name); 
    }
}
