package me.sablednah.legendquest.skills;

import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.EventDispatcher;
import me.sablednah.legendquest.events.SkillEvent;

public class SkillPool {

	public final List<SkillDefinition>				skills;
	public final HashMap<String, SkillDefinition>	skillDefs		= new HashMap<String, SkillDefinition>();
	public final HashMap<String, Skill>				skillList		= new HashMap<String, Skill>();
	public final HashMap<String, SkillDataStore>	permsSkillList	= new HashMap<String, SkillDataStore>();

	private final Main								lq;
	private final EventDispatcher					ed;
	private final String							skillfolder;

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
		Iterator<SkillDefinition> iter = this.skills.iterator();
		while (iter.hasNext()) {
			SkillDefinition def = iter.next();
			skillDefs.put(def.getSkillInfo().name.toLowerCase(), def);
		}
	}

	public void initSkills() {
		for (SkillDefinition def : this.skills) {
			initSkill(def);
		}
	}

	public void initSkill(SkillDefinition def) {
		initSkill(def, def.getSkillInfo().name);
	}

	public void initSkill(String skill, String name) {
		SkillDefinition def = skillDefs.get(skill.toLowerCase());
		initSkill(def, name);
	}

	public void initSkill(SkillDefinition def, String name) {
		lq.debug.info("Pooling skill: " + name + " | " + def.getSkillInfo().name);
		SkillDefinition def2 = null;
		if (name != def.getSkillInfo().name) {
			SkillInfo si2 = def.getSkillInfoClone();

			si2.name = name;
			def2 = new SkillDefinition(def.getSkillClass(), def.getSource(), si2);
			skills.add(def2);
			Skill sk = instantiateSkill(def2);
			skillList.put(name.toLowerCase(), sk);
		} else {
			def2 = def;
			Skill sk = instantiateSkill(def);
			skillList.put(name.toLowerCase(), sk);
		}
	}

	public Skill instantiateSkill(SkillDefinition def) {
		String str = "[LegendQuest] Enabling Skill: " + def.getSkillInfo().name + " v" + def.getSkillInfo().version + " by " + def.getSkillInfo().author;

//		System.out.println(str);
		lq.debug.info(str);

		Skill s = null;
		try {
			s = def.getSource().load(def);
		} catch (InstantiationException ie) {
//			System.out.println("[LQ] InstantiationException while enabling skill: " + def.getSkillInfo().name + " see logfile");
			lq.debug.error("InstantiationException while enabling skill: " + def.getSkillInfo().name);
//			System.out.println(ie.getMessage());
//			ie.printStackTrace();
			lq.debug.error(ie.getMessage());
			lq.debug.error(ie.getStackTrace().toString());
		} catch (Exception e) {
//			System.out.println("[LQ] Error while enabling skill: " + def.getSkillInfo().name);
//			e.printStackTrace();
			lq.debug.error("Error while enabling skill: " + def.getSkillInfo().name);
			lq.debug.error(e.getMessage());
			lq.debug.error(e.getStackTrace().toString());
		}

		if (!(s instanceof Skill)) {
//			System.out.println("[LQ] Error while enabling skill: " + def.getSkillInfo().name);
			lq.debug.error("Error while enabling skill: " + def.getSkillInfo().name);
		} else {
			initSkill(lq, s, def.getSkillInfo());
		}
		return s;
	}

	private void initSkill(Main lq, Skill skill, SkillInfo info) {
		skill.initialize(lq, info);
	}

	protected void reloadSkill(Skill skill) {
		if ((skill == null)) {
			return;
		}
		skill.onEnable();
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