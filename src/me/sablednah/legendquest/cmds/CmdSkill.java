package me.sablednah.legendquest.cmds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.races.Race;
import me.sablednah.legendquest.skills.SkillDataStore;
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
			sendSkillList(sender, null);
			return true;
		}

		// only players left here
		Player p = (Player) sender;
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
				sendSkillList(sender, pc);
				return true;
			} else if (actionName.equalsIgnoreCase("buy")) {
				if (args.length < 2) {
					sender.sendMessage("buy what now?");
				} else {
					String skillToBuy = args[1].toLowerCase();
					sender.sendMessage("Will try to buy " + skillToBuy);
				}
				return true;
			} else {
				sender.sendMessage("Will try to use " + actionName);
				pc.useSkill(actionName);
				return true;
			}
		}
	}

	private void sendSkillList(CommandSender sender, PC pc) {
		sender.sendMessage(lq.configLang.skillsList);
		String strout;

		if (pc == null || !(sender instanceof Player)) {
			// send a full list
			HashMap<String, SkillDataStore> skillmap = new HashMap<String, SkillDataStore>();

			for (ClassType cls : lq.classes.getClassTypes().values()) {
				for (SkillDataStore s : cls.availableSkills) {
					skillmap.put(s.levelRequired + " |" + s.name, s);
				}
				for (SkillDataStore s : cls.outsourcedSkills) {
					skillmap.put(s.levelRequired + " |" + s.name, s);
				}
			}
			for (Race cls : lq.races.getRaces().values()) {
				for (SkillDataStore s : cls.availableSkills) {
					skillmap.put(s.levelRequired + " |" + s.name, s);
				}
				for (SkillDataStore s : cls.outsourcedSkills) {
					skillmap.put(s.levelRequired + " |" + s.name, s);
				}
			}
			TreeMap<String, SkillDataStore> tm = new TreeMap<String, SkillDataStore>(skillmap);
			for (Entry<String, SkillDataStore> entry : tm.entrySet()) {
				SkillDataStore s = entry.getValue();
				strout = " - " + s.name + " [" + lq.configLang.statLevelShort + " " + s.levelRequired + " | " + lq.configLang.statSp + " " + s.skillPoints + "]";
				sender.sendMessage(strout);
			}
		} else {
			// get skills allowed for this player
			Map<String, Boolean> selected = pc.skillsSelected;

			HashMap<String, SkillDataStore> skillmap = new HashMap<String, SkillDataStore>();

			System.out.print("checking: "+pc.mainClass.name);
			System.out.print("checking: "+pc.mainClass.availableSkills);
			for (SkillDataStore s : pc.mainClass.availableSkills) {
				skillmap.put(s.levelRequired + " |" + s.name, s);
			}
			System.out.print("checking: "+pc.mainClass.outsourcedSkills);
			for (SkillDataStore s : pc.mainClass.outsourcedSkills) {
				skillmap.put(s.levelRequired + " |" + s.name, s);
			}
			System.out.print("checking: "+pc.race.name);
			System.out.print("checking: "+pc.race.availableSkills);
			for (SkillDataStore s : pc.race.availableSkills) {
				skillmap.put(s.levelRequired + " |" + s.name, s);
			}
			System.out.print("checking: "+pc.race.outsourcedSkills);
			for (SkillDataStore s : pc.race.outsourcedSkills) {
				skillmap.put(s.levelRequired + " |" + s.name, s);
			}

			if (pc.subClass != null) {
				System.out.print("checking: "+pc.subClass.name);
				for (SkillDataStore s : pc.subClass.availableSkills) {
					skillmap.put(s.levelRequired + " |" + s.name, s);
				}
				for (SkillDataStore s : pc.subClass.outsourcedSkills) {
					skillmap.put(s.levelRequired + " |" + s.name, s);
				}
			}

			TreeMap<String, SkillDataStore> tm = new TreeMap<String, SkillDataStore>(skillmap);
			for (Entry<String, SkillDataStore> entry : tm.entrySet()) {
				SkillDataStore s = entry.getValue();
				strout = " - " + s.name + " [" + lq.configLang.statLevelShort + " " + s.levelRequired + " | " + lq.configLang.statSp + " " + s.skillPoints + "]";
				if (selected.containsKey(s.name)) {
					strout += " *";
				}
				sender.sendMessage(strout);
			}

		}
	}
}
