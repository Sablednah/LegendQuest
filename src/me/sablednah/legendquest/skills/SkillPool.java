package me.sablednah.legendquest.skills;

import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.EventDispatcher;
import me.sablednah.legendquest.events.SkillEvent;

public class SkillPool {
    
    public final List<SkillDefinition> skills;
    public final HashMap<String, SkillDefinition> skillList = new HashMap<String, SkillDefinition>();
    private final Main lq;
    private final EventDispatcher ed;
    private final String skillfolder;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public SkillPool(Main plugin) {
        this.lq = plugin;
        this.ed = new EventDispatcher();
        skillfolder = lq.getDataFolder().getAbsolutePath() + File.separator + "skills";
        File sf = new File(skillfolder);
        if (!sf.exists()) {
            sf.mkdirs();
        }
        
        this.skills = new ArrayList(new SkillLoader(new File[] { sf }).list());
        /* for (SkillDefinition def : this.skills) {
         * instantiateSkill(def);
         * } */
        
        for (SkillDefinition def : this.skills) {
            String name = def.getSkillInfo().name;
            System.out.print("Pooling skill: "+name);
            skillList.put(name.toLowerCase(), def);
            instantiateSkill(def);
        }
    }
    
    public Skill instantiateSkill(SkillDefinition def) {
        return instantiateSkill(def,null);
    }
    
    public Skill instantiateSkill(SkillDefinition def, ConfigurationSection skillInfo) {
        System.out.println("[LegendQuest] Enabling Skill: " + def.getSkillInfo().name + " v" + def.getSkillInfo().version + " by " + def.getSkillInfo().author);
        
        Skill s = null;
        try {
            s = def.getSource().load(def);
        } catch (InstantiationException ie) {
            System.out.println("[LQ] InstantiationException while enabling skill: " + def.getSkillInfo().name);
            System.out.println(ie.getMessage());
            ie.printStackTrace();
        } catch (Exception e) {
            System.out.println("[LQ] Error while enabling skill: " + def.getSkillInfo().name);
            e.printStackTrace();
        }
        
        if (!(s instanceof Skill)) {
            System.out.println("[LQ] Error while enabling skill: " + def.getSkillInfo().name);
        } else {
            initSkill(lq, s, def.getSkillInfo(),skillInfo);
        }
        return s;
    }
    
    private void initSkill(Main lq, Skill skill, SkillInfo info, ConfigurationSection config) {
        System.out.println("[lq] initSkill: " + info.name );
        skill.initialize(lq, this, info, config);
    }
    
//    private void initSkill(Main lq, Skill skill, SkillInfo info) {
//        initSkill(lq, skill, info, null);
//    }
    
    protected void reloadSkill(Skill skill) {
        if ((skill == null) || (skill.getInfo() == null)) {
            return;
        }
        
        skill.onEnable();
        skill.loadOptions();
    }
    
    protected final EventDispatcher getEventDispatcher() {
        return this.ed;
    }
    
    public void dispatchEvent(SkillEvent event) {
        this.ed.fire(event);
    }
    
    public void dispatchEvent(SkillEvent event, EventListener listener) {
        this.ed.fire(event, listener);
    }
}