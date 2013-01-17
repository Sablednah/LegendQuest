package me.sablednah.legendquest.cmds;

import java.util.List;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdClass extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdClass(Main p) {
		this.lq = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		// get the enum for this command
		Cmds cmd = Cmds.valueOf("CLASS");
		if (!validateCmd(lq, cmd, sender, args)) {
			return true;
		}

		// from here on is command specific code.

		// send console the list list
		if (!(sender instanceof Player)) {
			sendClassList(sender, null);
			return true;
		}

		// only players left here
		Player p = (Player) sender;
		PC pc = lq.players.getPC(p);

		if (args.length < 1) { // why am i worried about negative argument length ? le-sigh
			// ok - just list the players class names here.
			if (pc.subClass == null) {
				sender.sendMessage(lq.configLang.youAreCurrently + ": " + pc.mainClass.name);
			} else {
				sender.sendMessage(lq.configLang.youAreCurrently + ": " + pc.mainClass.name + " (" + pc.subClass.name + ")");
			}
			return true;
		} else {
			String className = args[0].toLowerCase();
			if (className.equalsIgnoreCase("list")) {
				sendClassList(sender, pc);
				return true;
			} else {
				ClassType cl = lq.classes.getClass(className);
				if (cl == null) {
					sender.sendMessage(lq.configLang.classScanInvalid + ": " + className);
					return true;
				} else {
					if (pc.raceChanged) {
						pc.mainClass = cl;
						lq.players.addPlayer(p.getName(), pc);
						lq.players.savePlayer(p.getName());
						sender.sendMessage(lq.configLang.classChanged + ": " + className);
						return true;
					} else {
						sender.sendMessage(lq.configLang.classSelectRaceFirst);
						return true;
					}
				}
			}
		}
	}

	private void sendClassList(CommandSender sender, PC pc) {
		sender.sendMessage(lq.configLang.classList);
		String strout;

		if (pc == null || !(sender instanceof Player)) {
			// send a full list
			for (ClassType cls : lq.classes.getClassTypes().values()) {
				strout = " - " + cls.name;
				if (cls.defaultClass) {
					strout += " *";
				}
				sender.sendMessage(strout);
			}
		} else {
			// get classes allowed for this race
			List<String> classList = lq.classes.getClasses(pc.race.name, (Player) sender);
			if (classList != null) {
				for (String cls : classList) {
					strout = " - " + cls;
					if (cls.equalsIgnoreCase(lq.classes.defaultClass.name)) {
						strout += " *";
					}
					if (cls.equalsIgnoreCase(pc.mainClass.name)) {
						strout += " <";
					}
					if (pc.subClass != null && cls.equalsIgnoreCase(pc.subClass.name)) {
						strout += " «";
					}
					sender.sendMessage(strout);
				}
			}
		}
	}
}
