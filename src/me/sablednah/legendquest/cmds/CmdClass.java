package me.sablednah.legendquest.cmds;

import java.util.Collections;
import java.util.List;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.experience.SetExp;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdClass extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdClass(final Main p) {
		this.lq = p;
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		// get the enum for this command
		final Cmds cmd = Cmds.valueOf("CLASS");
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
		final Player p = (Player) sender;

		if (!lq.validWorld(p.getWorld().getName())) {
			p.sendMessage(lq.configLang.invalidWorld);
			return true;
		}

		final PC pc = lq.players.getPC(p);

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

				boolean sub = false;

				if (className.equalsIgnoreCase("sub") || className.equalsIgnoreCase("subclass")) {
					if (args.length < 2) {
						sender.sendMessage(lq.configLang.invalidArgumentsCommand);
					} else {
						className = args[1].toLowerCase();
						sub = true;
					}
				}

				final boolean confirm = (args[args.length - 1].equalsIgnoreCase("confirm"));

				// check classname is valid
				final ClassType cl = lq.classes.getClass(className);
				if (cl == null) {
					sender.sendMessage(lq.configLang.classInvalid + ": " + className);
					return true;
				} else {
					// make sure they have a race... (if more than one race)
					if (!pc.raceChanged && lq.races.getRaces().size() > 1) {
						sender.sendMessage(lq.configLang.classSelectRaceFirst);
						return true;
					} else {
						final List<String> validClasses = lq.classes.getClasses(pc.race.name, p, sub);

						if (!validClasses.contains(className)) {
							sender.sendMessage(lq.configLang.classNotAllowed);
							return true;
						}

						final int xpNow = SetExp.getTotalExperience(p);

						// check for confirmation
						boolean valid = false;

						if (xpNow > lq.configMain.max_xp) {
							valid = true;
						}

						if (confirm) {
							valid = true;
						}
						if (p.getLevel() < 2) {
							valid = true;
						}
						if (pc.mainClass == lq.classes.defaultClass) {
							valid = true;
						}
						if (sub && pc.subClass == null) {
							valid = true;
						}
						if (!sub && pc.mainClass == lq.classes.defaultClass) {
							valid = true;
						}

						if (!valid) {
							sender.sendMessage(lq.configLang.classChangeWarnXpLoss);
							sender.sendMessage(lq.configLang.classConfirm);
							return true;
						}

						// only rest XP if they have some worth bothering AND they are changing class - not setting
						// non-default
						pc.changeClass(cl, sub);
						sender.sendMessage(lq.configLang.classChanged + ": " + className);
						return true;
					}
				}
			}
		}
	}

	private void sendClassList(final CommandSender sender, final PC pc) {
		sender.sendMessage(lq.configLang.classList);
		String strout;

		if (pc == null || !(sender instanceof Player)) {
			// send a full list
			for (final ClassType cls : lq.classes.getClassTypes().values()) {
				if (cls.defaultClass) {
					strout = "# ";
				} else {
					strout = "  ";
				}
				strout += cls.name;

				if (cls.mainClassOnly) {
					strout += " 1";
				} else {
					if (cls.subClassOnly) {
						strout += " 2";
					}
				}

				if (cls.requires != null && !cls.requires.isEmpty()) {
					strout += " " + cls.requires.toString();
				}
				if (cls.requiresOne != null && !cls.requiresOne.isEmpty()) {
					strout += " 1x" + cls.requiresOne.toString();
				}
				sender.sendMessage(strout);
			}
		} else {
			// get classes allowed for this race
			List<String> classList = lq.classes.getClasses(pc.race.name, (Player) sender, null);
			Collections.sort(classList);
			if (classList != null) {
				for (final String cls : classList) {
					ClassType clsfile = lq.classes.getClassTypes().get(cls.toLowerCase());

					if (cls.equalsIgnoreCase(pc.mainClass.name)) {
						if (cls.equalsIgnoreCase(lq.classes.defaultClass.name)) {
							strout = ">#";
						} else {
							strout = "> ";
						}
					} else if (pc.subClass != null && cls.equalsIgnoreCase(pc.subClass.name)) {
						if (cls.equalsIgnoreCase(lq.classes.defaultClass.name)) {
							strout = "»#";
						} else {
							strout = "» ";
						}
					} else {
						if (clsfile.mainClassOnly) {
							strout = "1";
						} else {
							if (clsfile.subClassOnly) {
								strout = "2";
							} else {
								strout = "-";
							}
						}
						if (cls.equalsIgnoreCase(lq.classes.defaultClass.name)) {
							strout += "#";
						} else {
							strout += " ";
						}
					}

					strout += cls.substring(0, 1).toUpperCase() + cls.substring(1);

					if (pc.hasMastered(cls)) {
						strout += " *";
					} else {
						strout += "  ";
					}

					if (clsfile.requires != null && !clsfile.requires.isEmpty()) {
						strout += " [ ";
						for (String c : clsfile.requires) {
							strout += c;
							if (pc.hasMastered(c.toLowerCase())) {
								strout += "*";
							}
							strout += " ";
						}
						strout += "]";
					}
					if (clsfile.requiresOne != null && !clsfile.requiresOne.isEmpty()) {
						strout += " [ 1x ";
						for (String c : clsfile.requiresOne) {
							strout += c;
							if (pc.hasMastered(c.toLowerCase())) {
								strout += "*";
							}
							strout += " ";
						}
						strout += "]";
					}
					sender.sendMessage(strout);
				}
			}
		}
	}
}
