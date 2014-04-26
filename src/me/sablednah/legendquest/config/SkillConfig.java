package me.sablednah.legendquest.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import me.sablednah.legendquest.Main;

import org.bukkit.configuration.ConfigurationSection;

public class SkillConfig extends Config {
    
    private final Map<String, Object> skillSetHolder;
    public HashMap<String, ConfigurationSection> skillSetings = new HashMap<String, ConfigurationSection>();
    
    public SkillConfig(final Main p) {
        super(p, "skills.yml");
        
        ConfigurationSection skillSetList = this.getConfigItem("skills");
        
        skillSetHolder = skillSetList.getValues(false);
        
        final Iterator<Entry<String, Object>> entries = skillSetHolder.entrySet().iterator();
        while (entries.hasNext()) {
            final Entry<String, Object> entry = entries.next();
            lq.debug.fine("Loading Skill Config: " + entry.getKey());
            ConfigurationSection data = (ConfigurationSection) entry.getValue();            
            lq.debug.fine("Skill Config data: " + data);
            skillSetHolder.put(entry.getKey(), data);
        }
    }
}
