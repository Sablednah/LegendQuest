package me.sablednah.legendquest.cmds;

import java.util.ArrayList;
import java.util.List;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.races.Race;
import me.sablednah.legendquest.skills.Skill;
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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// get the enum for this command
		Cmds cmd = Cmds.valueOf("SKILL");
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

		if (args.length < 1) { // why am i worried about negative argument length ? le-sigh
			// ok - just list the players skill names here.
			String msg;
			if (pc.skillsSelected != null && !pc.skillsSelected.isEmpty()) {
				List<String> skillnames = new ArrayList<String>();
				for (Skill s : pc.skillsSelected) {
					skillnames.add(s.name);
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
					sender.sendMessage("Will try to buy " +skillToBuy);
				}
				return true;
			} else {
				sender.sendMessage("Will try to use "+actionName);
				return true;
			}
		}
	}

	private void sendSkillList(CommandSender sender, PC pc) {
		sender.sendMessage(lq.configLang.skillsList);
		String strout;

		if (pc == null || !(sender instanceof Player)) {
			// send a full list
			for (ClassType cls : lq.classes.getClassTypes().values()) {
				for (Skill s: cls.availableSkills) {
					strout = " - " + s.name + " ["+lq.configLang.statLevelShort+" "+s.levelRequired+" | "+lq.configLang.statSp+" "+s.skillPoints+"]";
					sender.sendMessage(strout);
				}
			}
			for (Race cls : lq.races.getRaces().values()) {
				for (Skill s: cls.availableSkills) {
					strout = " - " + s.name + " ["+lq.configLang.statLevelShort+" "+s.levelRequired+" | "+lq.configLang.statSp+" "+s.skillPoints+"]";
					sender.sendMessage(strout);
				}
			}
		} else {
			pc.checkSkills();
			// get skills allowed for this player
			List<Skill> selected = pc.skillsSelected;
			
			for (Skill s: pc.race.availableSkills) {
				strout = " - " + s.name + " ["+lq.configLang.statLevelShort+" "+s.levelRequired+" | "+lq.configLang.statSp+" "+s.skillPoints+"]";
				if (selected.contains(s)) {
					strout +=" *";
				}
				sender.sendMessage(strout);
			}
			for (Skill s: pc.mainClass.availableSkills) {
				strout = " - " + s.name + " ["+lq.configLang.statLevelShort+" "+s.levelRequired+" | "+lq.configLang.statSp+" "+s.skillPoints+"]";
				if (selected.contains(s)) {
					strout +=" *";
				}
				sender.sendMessage(strout);
			}
			if (pc.subClass!=null) {
				for (Skill s: pc.subClass.availableSkills) {
					strout = " - " + s.name + " ["+lq.configLang.statLevelShort+" "+s.levelRequired+" | "+lq.configLang.statSp+" "+s.skillPoints+"]";
					if (selected.contains(s)) {
						strout +=" *";
					}
					sender.sendMessage(strout);
				}
			}
		}
	}
}
