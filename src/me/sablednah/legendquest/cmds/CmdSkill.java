package me.sablednah.legendquest.cmds;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.races.Race;
import me.sablednah.legendquest.skills.Skill;
import me.sablednah.legendquest.skills.SkillDataStore;
import me.sablednah.legendquest.skills.SkillType;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSkill extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdSkill(Main p) {
		this.lq = p;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// get the enum for this command
		final Cmds cmd = Cmds.valueOf("SKILL");
		if (!validateCmd(lq, cmd, sender, args)) {
			return true;
		}

		// from here on is command specific code.

		// send console the full skill list
		if (!(sender instanceof Player)) {
			boolean full = false;
			if (args.length > 0) {
				if(args[0].equalsIgnoreCase("full") || args[0].equalsIgnoreCase("info") ) {
					full = true;
				}
			}
			sendSkillList(sender, null, full);
			return true;
		}

		// only players left here
		Player p = (Player) sender;

		if (!lq.validWorld(p.getWorld().getName())) {
			p.sendMessage(lq.configLang.invalidWorld);
			return true;
		}

		PC pc = lq.players.getPC(p);
		pc.checkSkills();
		if (args.length < 1) { // why am i worried about negative argument length ? le-sigh
			// ok - just list the players skill names here.
			String msg;
			if (pc.skillsSelected != null && !pc.skillsSelected.isEmpty()) {
				List<String> skillnames = new ArrayList<String>();
				for (final Entry<String, Boolean> s : pc.skillsSelected.entrySet()) {
					if (s.getValue()) {
						skillnames.add(s.getKey());
					}
				}
				msg = lq.configLang.hasSkills + " " + Utils.join(skillnames.toArray(new String[0]), ",");
			} else {
				msg = lq.configLang.noSkills;
			}
			sender.sendMessage(msg);
			return true;
		} else {
			String actionName = args[0].toLowerCase();
			if (actionName.equalsIgnoreCase("list")) {
				boolean full = false;
				if (args.length > 1) {
					if(args[1].equalsIgnoreCase("full") || args[1].equalsIgnoreCase("info") ) {
						full = true;
					}
				}
				sendSkillList(sender, pc, full);
				return true;
			} else if (actionName.equalsIgnoreCase("buy")) {
				if (args.length < 2) {
					sender.sendMessage(lq.configLang.skillPointsNoSkill);
				} else {
					String skillToBuy = args[1].toLowerCase();
					if (pc.validSkill(skillToBuy)) {
						sender.sendMessage(skillToBuy + lq.configLang.skillPointsOwned);
					} else if (pc.buySkill(skillToBuy)) {
						sender.sendMessage(lq.configLang.skillPointsBought + "'" + skillToBuy + "'");
					}
				}
				return true;
			} else if (actionName.equalsIgnoreCase("info")) {
				if (args.length < 2) {
					sender.sendMessage(lq.configLang.skillInfoNoSkill);
				} else {
					String skillToInfo = args[1].toLowerCase();
					skillinfo(skillToInfo, sender, pc);
				}
				return true;
			} else {

				if (lq.effectManager.getPlayerEffects(p.getUniqueId()).contains(Effects.STUNNED)) {
					// stunned - no skills
					sender.sendMessage(lq.configLang.skillStunned + actionName);
					return true;
				}
				sender.sendMessage(lq.configLang.skillCommandLineUse + actionName);
				pc.useSkill(actionName,args);
				return true;
			}
		}
	}

	private void skillinfo(String skillToInfo, CommandSender sender, PC pc) {
		String d = getDesc(skillToInfo, pc);
		if (d != null) {
			sender.sendMessage(skillToInfo + " : " + d);
		} else {
			sender.sendMessage(lq.configLang.skillInfoNoSkill);
		}
	}
	
	private String getDesc(String skillToInfo, PC pc) {
		Skill skill = lq.skills.skillList.get(skillToInfo);

		String d = null;
		if (skill != null) {
			d = skill.defaultOptions.description;
			int[] timings = {skill.defaultOptions.buildup,skill.defaultOptions.delay,skill.defaultOptions.duration,skill.defaultOptions.cooldown};
					
			HashMap<String, Object> vars = null;
			if (pc != null) {
				SkillDataStore sk = pc.skillSet.get(skillToInfo);
				if (sk!=null) {
					vars = sk.vars;
					d = sk.description;
					int[] tim = {sk.buildup,sk.delay,sk.duration,sk.cooldown};
					timings= tim;
				}
			} else {
				vars = skill.defaultOptions.vars;
			}
			if (vars != null) {
				for (Entry<String, Object> var : vars.entrySet()) {
					// System.out.print("var:"+var.getKey()+"="+var.getValue());
					d = d.replaceAll("\\[" + var.getKey() + "\\]", var.getValue().toString());
				}
			}
			d = d.replaceAll("\\[buildup\\]", String.valueOf(timings[0]/1000.0D));
			d = d.replaceAll("\\[delay\\]", String.valueOf(timings[1]/1000.0D));
			d = d.replaceAll("\\[duration\\]", String.valueOf(timings[2]/1000.0D));
			d = d.replaceAll("\\[cooldown\\]", String.valueOf(timings[3]/1000.0D));
		}
		return d;
	}

	private void sendSkillList(CommandSender sender, PC pc, boolean full) {
		sender.sendMessage(lq.configLang.skillsList);

		DecimalFormat df = new DecimalFormat("0000");

		if (pc == null || !(sender instanceof Player)) {
			// send a full list
			HashMap<String, SkillDataStore> skillmap = new HashMap<String, SkillDataStore>();
			HashMap<String, SkillDataStore> passivemap = new HashMap<String, SkillDataStore>();

			for (ClassType cls : lq.classes.getClassTypes().values()) {
				for (SkillDataStore s : cls.availableSkills) {
					if (s.type != null && !s.type.equals(SkillType.ACTIVE)) {
						passivemap.put(df.format(s.levelRequired) + " |" + s.name, s);
					} else {
						skillmap.put(df.format(s.levelRequired) + " |" + s.name, s);
					}
				}
				for (SkillDataStore s : cls.outsourcedSkills) {
					if (s.type != null && !s.type.equals(SkillType.ACTIVE)) {
						passivemap.put(df.format(s.levelRequired) + " |" + s.name, s);
					} else {
						skillmap.put(df.format(s.levelRequired) + " |" + s.name, s);
					}
				}
			}
			for (Race cls : lq.races.getRaces().values()) {
				for (SkillDataStore s : cls.availableSkills) {
					if (s.type != null && !s.type.equals(SkillType.ACTIVE)) {
						passivemap.put(df.format(s.levelRequired) + " |" + s.name, s);
					} else {
						skillmap.put(df.format(s.levelRequired) + " |" + s.name, s);
					}
				}
				for (SkillDataStore s : cls.outsourcedSkills) {
					if (s.type != null && !s.type.equals(SkillType.ACTIVE)) {
						passivemap.put(df.format(s.levelRequired) + " |" + s.name, s);
					} else {
						skillmap.put(df.format(s.levelRequired) + " |" + s.name, s);
					}
				}
			}

			TreeMap<String, SkillDataStore> tm = new TreeMap<String, SkillDataStore>(skillmap);
			TreeMap<String, SkillDataStore> tmpassive = new TreeMap<String, SkillDataStore>(passivemap);

			sender.sendMessage(lq.configLang.skillsListPasive);
			sender.sendMessage(showList(tmpassive, pc, full));

			sender.sendMessage(lq.configLang.skillsListActive);
			sender.sendMessage(showList(tm, pc, full));
		} else {
			// get skills allowed for this player

			HashMap<String, SkillDataStore> skillmap = new HashMap<String, SkillDataStore>();
			HashMap<String, SkillDataStore> passivemap = new HashMap<String, SkillDataStore>();

			for (SkillDataStore s : pc.race.availableSkills) {
				if (s.type != null && !s.type.equals(SkillType.ACTIVE)) {
					passivemap.put(df.format(s.levelRequired) + " |" + s.name, s);
				} else {
					skillmap.put(df.format(s.levelRequired) + " |" + s.name, s);
				}
			}
			for (SkillDataStore s : pc.race.outsourcedSkills) {
				if (s.type != null && !s.type.equals(SkillType.ACTIVE)) {
					passivemap.put(df.format(s.levelRequired) + " |" + s.name, s);
				} else {
					skillmap.put(df.format(s.levelRequired) + " |" + s.name, s);
				}
			}
			if (pc.subClass != null) {
				for (SkillDataStore s : pc.subClass.availableSkills) {
					if (s.type != null && !s.type.equals(SkillType.ACTIVE)) {
						passivemap.put(df.format(s.levelRequired) + " |" + s.name, s);
					} else {
						skillmap.put(df.format(s.levelRequired) + " |" + s.name, s);
					}
				}
				for (SkillDataStore s : pc.subClass.outsourcedSkills) {
					if (s.type != null && !s.type.equals(SkillType.ACTIVE)) {
						passivemap.put(df.format(s.levelRequired) + " |" + s.name, s);
					} else {
						skillmap.put(df.format(s.levelRequired) + " |" + s.name, s);
					}
				}
			}
			for (SkillDataStore s : pc.mainClass.availableSkills) {
				if (s.type != null && !s.type.equals(SkillType.ACTIVE)) {
					passivemap.put(df.format(s.levelRequired) + " |" + s.name, s);
				} else {
					skillmap.put(df.format(s.levelRequired) + " |" + s.name, s);
				}
			}
			for (SkillDataStore s : pc.mainClass.outsourcedSkills) {
				if (s.type != null && !s.type.equals(SkillType.ACTIVE)) {
					passivemap.put(df.format(s.levelRequired) + " |" + s.name, s);
				} else {
					skillmap.put(df.format(s.levelRequired) + " |" + s.name, s);
				}
			}

			TreeMap<String, SkillDataStore> tm = new TreeMap<String, SkillDataStore>(skillmap);
			TreeMap<String, SkillDataStore> tmpassive = new TreeMap<String, SkillDataStore>(passivemap);

			sender.sendMessage(lq.configLang.skillsListPasive);
			sender.sendMessage(showList(tmpassive, pc, full));

			sender.sendMessage(lq.configLang.skillsListActive);
			sender.sendMessage(showList(tm, pc, full));
		}
	}

	public String[] showList(TreeMap<String, SkillDataStore> tm, PC pc, boolean full) {
		Map<String, Boolean> selected = null;
		Player p = null;
		if (pc != null) {
			selected = pc.skillsSelected;
			p = pc.getPlayer();
		}

		// System.out.print(tm.entrySet().size());

		int size = tm.entrySet().size();
		if (full) {
			size = size *2;
		}

		String[] strouts = new String[size];
		int cntr = 0;
		for (Entry<String, SkillDataStore> entry : tm.entrySet()) {
			String strout = "";
			SkillDataStore s = entry.getValue();
			SkillDataStore x = null;
			if (pc != null) {
				x = pc.getSkillData(entry.getKey());
			}

			int lev = 0;
			int sp = 0;

			if (x != null) {
				lev = x.levelRequired;
				sp = x.skillPoints;
				if (pc != null) {
					x = pc.getSkillData(entry.getKey());
					if (x != null) {
						if (x.needPerm !=null && !x.needPerm.isEmpty() ) {
							if (p == null || !p.isOnline() || !p.hasPermission(x.needPerm)) {
								continue;
							}
						}
					}
				}
			} else {
				lev = s.levelRequired;
				sp = s.skillPoints;
				if (pc != null) {
					if (s != null) {
						if (s.needPerm !=null && !s.needPerm.isEmpty() ) {
							if (p == null || !p.isOnline() || !p.hasPermission(s.needPerm)) {
								continue;
							}
						}
					}
				}
			}

			strout += lq.configLang.statLevelShort + " ";
			if (lev < 10) {
				strout += " ";
			}
			if (lev < 100) {
				strout += " ";
			}
			strout += lev + " : " + s.name;
			if (sp > 0) {
				if (selected != null && selected.containsKey(s.name)) {
					strout += " [Purchased]";
				} else {
					strout += " [" + lq.configLang.statSp + " " + sp + "]";
				}
			}
			if (selected != null && selected.containsKey(s.name)) {
				strout += " *";
			} else {
				if ((s.requires != null && !s.requires.isEmpty()) || (s.requiresOne != null && !s.requiresOne.isEmpty())) {
					strout += " [Requires:";
					if (s.requires != null && !s.requires.isEmpty()) {
						strout += " All-" + s.requires.toString();
					}
					if (s.requiresOne != null && !s.requiresOne.isEmpty()) {
						strout += " One-" + s.requiresOne.toString();
					}
					strout += "}";
				}
			}
			strouts[cntr] = strout;
			cntr++;
			if (full) {
				strout="";
				strout = getDesc(s.name, pc);
				if (strout==null) {
					strout="        : ";
				}else {
					strout="        : " + strout;
				}
				strouts[cntr] = strout;
				cntr++;
			}
		}
		return strouts;
	}
}
