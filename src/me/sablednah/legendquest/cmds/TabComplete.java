package me.sablednah.legendquest.cmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.effects.Effects;
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
				Player p = (Player) sender;
				boolean addit = true;
				for (SkillDataStore s : pc.race.availableSkills) {
					if (s.type != null && s.type.equals(SkillType.ACTIVE)) {						
						addit = true;
						if (s.needPerm !=null && !s.needPerm.isEmpty() ) {
							if (p == null || !p.isOnline() || !p.hasPermission(s.needPerm)) {
								addit = false;
							}
						}
						if (addit) { outputList.add(s.name.toLowerCase()); }
					}
				}
				for (SkillDataStore s : pc.race.outsourcedSkills) {
					if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
						addit = true;
						if (s.needPerm !=null && !s.needPerm.isEmpty() ) {
							if (p == null || !p.isOnline() || !p.hasPermission(s.needPerm)) {
								addit = false;
							}
						}
						if (addit) { outputList.add(s.name.toLowerCase()); }
					}
				}
				if (pc.subClass != null) {
					for (SkillDataStore s : pc.subClass.availableSkills) {
						if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
							addit = true;
							if (s.needPerm !=null && !s.needPerm.isEmpty() ) {
								if (p == null || !p.isOnline() || !p.hasPermission(s.needPerm)) {
									addit = false;
								}
							}
							if (addit) { outputList.add(s.name.toLowerCase()); }
						}
					}
					for (SkillDataStore s : pc.subClass.outsourcedSkills) {
						if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
							addit = true;
							if (s.needPerm !=null && !s.needPerm.isEmpty() ) {
								if (p == null || !p.isOnline() || !p.hasPermission(s.needPerm)) {
									addit = false;
								}
							}
							if (addit) { outputList.add(s.name.toLowerCase()); }
						}
					}
				}
				for (SkillDataStore s : pc.mainClass.availableSkills) {
					if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
						addit = true;
						if (s.needPerm !=null && !s.needPerm.isEmpty() ) {
							if (p == null || !p.isOnline() || !p.hasPermission(s.needPerm)) {
								addit = false;
							}
						}
						if (addit) { outputList.add(s.name.toLowerCase()); }
					}
				}
				for (SkillDataStore s : pc.mainClass.outsourcedSkills) {
					if (s.type != null && s.type.equals(SkillType.ACTIVE)) {
						addit = true;
						if (s.needPerm !=null && !s.needPerm.isEmpty() ) {
							if (p == null || !p.isOnline() || !p.hasPermission(s.needPerm)) {
								addit = false;
							}
						}
						if (addit) { outputList.add(s.name.toLowerCase()); }
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
		} else if (cmdName.equals("admin") || cmdName.equals("lqadmin")) {
// System.out.print("newArglist.length="+newArglist.length);
			if (newArglist.length==1) {
				outputList.add("xp");
				outputList.add("level");
				outputList.add("race");
				outputList.add("class");
				outputList.add("karma");
				outputList.add("effect");
				outputList.add("reset");
			}  else if (newArglist.length==3) {
				if (newArglist[0].equalsIgnoreCase("race")) {
					for (Race rc : lq.races.getRaces().values()) {
						outputList.add(rc.name.toLowerCase());
					}
				} else if (newArglist[0].equalsIgnoreCase("class")) {
					if (isPlayer) {
						outputList = lq.classes.getClasses(pc.race.name, (Player) sender, null);
					} else {
						for (ClassType cls : lq.classes.getClassTypes().values()) {
							outputList.add(cls.name.toLowerCase());
						}
					}					
				} else if (newArglist[0].equalsIgnoreCase("effect")) {
					for(Effects effect  : Effects.values()) {
						outputList.add(effect.toString());
					}
				} else if (newArglist[0].equalsIgnoreCase("xp") || newArglist[0].equalsIgnoreCase("karma")) {
					outputList.add("1");
					outputList.add("10");
					outputList.add("100");
					outputList.add("1000");
					outputList.add("-1");
					outputList.add("-10");
					outputList.add("-100");
					outputList.add("-1000");
				} else if (newArglist[0].equalsIgnoreCase("level")) {
					outputList.add("0");
					outputList.add("1");
					for (int l=5;l<154;l+=5) {
						outputList.add(String.valueOf(l));
					}
				} else { 
					for (Player pl : lq.getServer().getOnlinePlayers()) {
						outputList.add(pl.getName());
					}
				}
			} else { 
				for (Player pl : lq.getServer().getOnlinePlayers()) {
					outputList.add(pl.getName());
				}
			}
		} else if (cmdName.equals("party")) {
			if (newArglist.length==1) {
				outputList.add("create");
				outputList.add("join");
				outputList.add("approve");
			} else {
				for (Player pl : lq.getServer().getOnlinePlayers()) {
					outputList.add(pl.getName());
				}
			}			
		} else if (cmdName.equals("stats")) {
			if (newArglist.length>1) {
				outputList.add("full");
			} else {
				for (Player pl : lq.getServer().getOnlinePlayers()) {
					outputList.add(pl.getName());
				}
			}			
		} else {
			outputList.add("admin");
			outputList.add("class");
			outputList.add("party");
			outputList.add("race");
			outputList.add("skill");
			outputList.add("stats");
			if (cmdName.isEmpty()) {
				return outputList;
			} else {
				return StringUtil.copyPartialMatches(cmdName, outputList, new ArrayList<String>());
			}
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
