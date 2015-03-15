package me.sablednah.legendquest.skills;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.EventDispatcher;
import me.sablednah.legendquest.events.SkillEvent;

public class SkillPool {

	public List<SkillDefinition>			skills;
	public HashMap<String, SkillDefinition>	skillDefs		= new HashMap<String, SkillDefinition>();
	public HashMap<String, Skill>			skillList		= new HashMap<String, Skill>();
	public HashMap<String, SkillDataStore>	permsSkillList	= new HashMap<String, SkillDataStore>();

	private Main							lq;
	private EventDispatcher					ed;
	private String							skillfolder;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SkillPool(Main plugin) {
		this.lq = plugin;
		this.ed = new EventDispatcher();
		skillfolder = lq.getDataFolder().getAbsolutePath() + File.separator + "skills";
		File sf = new File(skillfolder);
		if (!sf.exists()) {
			sf.mkdirs();
		}

		this.skills = Collections.synchronizedList(new ArrayList(new SkillLoader(new File[] { sf }).list()));
		Iterator<SkillDefinition> iter = this.skills.iterator();
		while (iter.hasNext()) {
			SkillDefinition def = iter.next();
			String str = "Found skill in jar: "+def.getSkillInfo().getName().toLowerCase();
			if (lq.configMain.debugMode) { System.out.println(str); }
			lq.debug.info(str);
			skillDefs.put(def.getSkillInfo().getName().toLowerCase(), def);
		}
	}

	public void initSkills() {
		for (Iterator<SkillDefinition> iter = this.skills.iterator(); iter.hasNext();) {
			SkillDefinition d = iter.next();
			initSkill(d);
		}
	}

	public void initSkill(SkillDefinition def) {
		initSkill(def, def.getSkillInfo().getName().toLowerCase());
	}

	public void initSkill(String skill, String name) {
		SkillDefinition def = skillDefs.get(skill.toLowerCase());
		if (def != null) {
			initSkill(def, name.toLowerCase());
		} else {
			lq.debug.error(skill + " not found.");
		}
	}

	public void initSkill(SkillDefinition def, String name) {

		lq.debug.info("Pooling skill: " + name + " | " + def.getSkillInfo().getName().toLowerCase());
		SkillDefinition def2 = null;
		if (!name.equalsIgnoreCase(def.getSkillInfo().getName())) {
			SkillInfo si2 = new SkillInfo(def.getSkillInfo().getAuthor(), name, def.getSkillInfo().getDescription(), def.getSkillInfo().getType(), def.getSkillInfo().getVersion(),
					def.getSkillInfo().getBuildup(),def.getSkillInfo().getDelay(),def.getSkillInfo().getDuration(),def.getSkillInfo().getCooldown(),def.getSkillInfo().getPay(),def.getSkillInfo().getXp(),def.getSkillInfo().getManaCost(),
					def.getSkillInfo().getConsumes(),def.getSkillInfo().getLevelRequired(),def.getSkillInfo().getSkillPoints(),def.getSkillInfo().getVars(), 
					def.getSkillInfo().getKarmaCost(), def.getSkillInfo().getKarmaReward(), def.getSkillInfo().getKarmaRequired() ); 
			def2 = new SkillDefinition(def.getSkillClass(), def.getSource(), si2);
			Skill sk = instantiateSkill(def2);
			skillList.put(name.toLowerCase(), sk);
		} else {
			Skill sk = instantiateSkill(def);
			skillList.put(name.toLowerCase(), sk);
		}
	}

	public Skill instantiateSkill(SkillDefinition def) {
		String str = "[LegendQuest] Enabling Skill: " + def.getSkillInfo().getName() + " v" + def.getSkillInfo().version + " by " + def.getSkillInfo().author;

		if (lq.configMain.debugMode) { System.out.println(str); }
		lq.debug.info(str);

		Skill s = null;
		try {
			s = def.getSource().load(def);
		} catch (InstantiationException ie) {
			// System.out.println("[LQ] InstantiationException while enabling skill: " + def.getSkillInfo().name +
			// " see logfile");
			lq.debug.error("InstantiationException while enabling skill: " + def.getSkillInfo().getName());
			// System.out.println(ie.getMessage());
			// ie.printStackTrace();
			lq.debug.error(ie.getMessage());
			lq.debug.error(ie.getStackTrace().toString());
		} catch (Exception e) {
			// System.out.println("[LQ] Error while enabling skill: " + def.getSkillInfo().name);
			// e.printStackTrace();
			lq.debug.error("Error while enabling skill: " + def.getSkillInfo().getName());
			lq.debug.error(e.getMessage());
			lq.debug.error(e.getStackTrace().toString());
		}

		if (!(s instanceof Skill)) {
			// System.out.println("[LQ] Error while enabling skill: " + def.getSkillInfo().name);
			lq.debug.error("Error while enabling skill: " + def.getSkillInfo().getName());
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

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}