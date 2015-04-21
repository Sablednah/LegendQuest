package me.sablednah.legendquest.cmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.races.Race;
import me.sablednah.legendquest.skills.SkillDataStore;
import me.sablednah.legendquest.skills.SkillType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class TabComplete implements TabCompleter {

	public Main	lq;

	public TabComplete(Main plugin) {
		lq = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		String cmdName = "";
		String[] newArglist;
		List<String> outputList = new ArrayList<String>();

		if (cmd.getName().equalsIgnoreCase("lq")) {
			if ((args.length > 0)) {
				if (args.length > 1) {
					newArglist = Arrays.copyOfRange(args, 1, args.length);
				} else {
					newArglist = new String[0];
				}
				cmdName = args[0].toLowerCase();
			} else {
				newArglist = args;
			}

		} else {
			newArglist = args;
			cmdName = cmd.getName().toLowerCase();
		}

		boolean isPlayer = (sender instanceof Player);
		PC pc = null;
		if (isPlayer) {
			pc = lq.players.getPC(((Player) sender).getUniqueId());
		}

		if (cmdName.equals("class")) {
			if (isPlayer) {
				outputList = lq.classes.getClasses(pc.race.name, (Player) sender, null);
			} else {
				for (ClassType cls : lq.classes.getClassTypes().values()) {
					outputList.add(cls.name.toLowerCase());
				}
			}
		} else if (cmdName.equals("race")) {
			for (Race rc : lq.races.getRaces().values()) {
				outputList.add(rc.name.toLowerCase());
			}
		} else if (cmdName.equals("skill")) {
			if (isPlayer) {
				for (SkillDataStore s : pc.race.availableSkills) {
					if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
						outputList.add(s.name.toLowerCase());
					}
				}
				for (SkillDataStore s : pc.race.outsourcedSkills) {
					if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
						outputList.add(s.name.toLowerCase());
					}
				}
				if (pc.subClass != null) {
					for (SkillDataStore s : pc.subClass.availableSkills) {
						if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
							outputList.add(s.name.toLowerCase());
						}
					}
					for (SkillDataStore s : pc.subClass.outsourcedSkills) {
						if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
							outputList.add(s.name.toLowerCase());
						}
					}
				}
				for (SkillDataStore s : pc.mainClass.availableSkills) {
					if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
						outputList.add(s.name.toLowerCase());
					}
				}
				for (SkillDataStore s : pc.mainClass.outsourcedSkills) {
					if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
						outputList.add(s.name.toLowerCase());
					}
				}
			} else {
				for (ClassType cls : lq.classes.getClassTypes().values()) {
					for (SkillDataStore s : cls.availableSkills) {
						if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
							outputList.add(s.name.toLowerCase());
						}
					}
					for (SkillDataStore s : cls.outsourcedSkills) {
						if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
							outputList.add(s.name.toLowerCase());
						}
					}
				}
				for (Race cls : lq.races.getRaces().values()) {
					for (SkillDataStore s : cls.availableSkills) {
						if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
							outputList.add(s.name.toLowerCase());
						}
					}
					for (SkillDataStore s : cls.outsourcedSkills) {
						if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
							outputList.add(s.name.toLowerCase());
						}
					}
				}
			}
		} else if (cmdName.equals("admin")) {
			if (newArglist.length==1) {
				outputList.add("xp");
				outputList.add("level");
				outputList.add("race");
				outputList.add("class");
				outputList.add("karma");
				outputList.add("effect");
				outputList.add("reset");
			} else {  //  if (newArglist.length==2) 
				for (Player pl : lq.getServer().getOnlinePlayers()) {
					outputList.add(pl.getName());
				}
			}
		} else {
			outputList.add("class");
			outputList.add("race");
			outputList.add("skill");
			outputList.add("admin");
		}
		String lastarg = "";
		if (newArglist.length>0) {
			lastarg = newArglist[newArglist.length - 1];
		}
		if (!lastarg.isEmpty()) {
			return StringUtil.copyPartialMatches(lastarg, outputList, new ArrayList<String>());
		} else {
			return outputList;
		}
	}
}
