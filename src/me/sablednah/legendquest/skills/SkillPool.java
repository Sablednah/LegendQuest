package me.sablednah.legendquest.skills;

import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.EventDispatcher;
import me.sablednah.legendquest.events.SkillEvent;

public class SkillPool {

	public final List<SkillDefinition>				skills;
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
	}

	public void initSkills() {
		for (SkillDefinition def : this.skills) {
			String name = def.getSkillInfo().name;
			System.out.print("Pooling skill: " + name);
			Skill sk = instantiateSkill(def);
			skillList.put(name.toLowerCase(), sk);
		}
	}

	public Skill instantiateSkill(SkillDefinition def) {
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
			initSkill(lq, s, def.getSkillInfo());
		}
		return s;
	}

	private void initSkill(Main lq, Skill skill, SkillInfo info) {
		System.out.println("[lq] initSkill: " + info.name);
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