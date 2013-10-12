package me.sablednah.legendquest.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.skills.LQTime;
import me.sablednah.legendquest.skills.Skill;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.Vector;

public class SkillConfig {  // extends Config
    
    private static Main lq;
    private static File pFile;
    private static File pwFile;
    private static YamlConfiguration pConfig;
    private static YamlConfiguration pwConfig;
    private static Set<Skill> pList = new HashSet<Skill>();
    
    public SkillConfig(Main plugin) {
        lq = plugin;
        pwFile = new File(plugin.getDataFolder(), "skills.yml");
        pwConfig = YamlConfiguration.loadConfiguration(pwFile);
    }
    
    public static boolean addPower(Skill skill) {
        if (!pList.contains(skill)) {
            pList.add(skill);
            return true;
        }
        return false;
    }
    
    public static void createTogglePerm(String skill) {
        Permission perm = new Permission("legendquest.enable." + skill, PermissionDefault.TRUE);
        if (!Bukkit.getServer().getPluginManager().getPermissions().contains(perm))
            Bukkit.getServer().getPluginManager().addPermission(perm);
    }
    
    public static File getConfigFile(String skill) {
        String skillfolder = lq.getDataFolder().getAbsolutePath() + File.separator + "skills";
        File pwFolder = new File(skillfolder, "skillConfig");
        return new File(pwFolder, skill + ".yml");
    }
    
    public static Set<String> getOptions(String skill) {
        pConfig = getPowerConfig(skill);
        return pConfig.getKeys(true);
    }
    
    public static String getOptionType(String power, String option) {
        String type = getOptionValue(power, option).getClass().getSimpleName();
        if (type.equalsIgnoreCase("string")) {
            try {
                new LQTime(pConfig.getString(option));
                type = "Time";
            } catch (Exception e) {
                type = "String";
            }
        }
        return type;
    }
    
    public static Object getOptionValue(String power, String option) {
        pConfig = getPowerConfig(power);
        return pConfig.get(option);
    }
    
    public static Skill getPower(String name) {
        for (Skill skill : pList) {
            if (skill.getTag().equalsIgnoreCase(name)) {
                return skill;
            }
        }
        return null;
    }
    
    public static YamlConfiguration getPowerConfig(String power) {
        pFile = getConfigFile(power);
        return YamlConfiguration.loadConfiguration(pFile);
    }
    
    public static Set<Skill> getPowerList() {
        return pList;
    }
    
    public static boolean removePower(Skill skill) {
        if (pList.contains(skill)) {
            pList.remove(skill);
            return true;
        }
        return false;
    }
    
    public static void setOption(Skill skill, String option, Object value) {
        if ((value instanceof Boolean)) {
            skill.option(option, ((Boolean) value).booleanValue());
        }
        else if ((value instanceof Double)) {
            skill.option(option, ((Double) value).doubleValue());
        }
        else if ((value instanceof Integer)) {
            skill.option(option, ((Integer) value).intValue());
        }
        else if ((value instanceof ItemStack)) {
            skill.option(option, (ItemStack) value);
        }
        else if ((value instanceof Long)) {
            skill.option(option, ((Long) value).longValue());
        }
        else if ((value instanceof LQTime)) {
            skill.option(option, (LQTime) value);
        }
        else if ((value instanceof String)) {
            skill.option(option, (String) value);
        }
        else if ((value instanceof Vector)) {
            skill.option(option, (Vector) value);
        }
        skill.saveConfig();
    }
    
    public static void save() {
        try {
            pwConfig.save(pwFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}