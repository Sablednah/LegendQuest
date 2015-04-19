package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.races.Race;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdRace extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdRace(final Main p) {
		this.lq = p;
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		// get the enum for this command
		final Cmds cmd = Cmds.valueOf("RACE");

		if (!validateCmd(lq, cmd, sender, args)) {
			return true;
		}

		// send console the list list
		if (!(sender instanceof Player)) {
			boolean full = false;
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("full")) {
					full = true;
				}
			}
			sendRaceList(sender, null, full);
			return true;
		}

		final Player p = (Player) sender;

		if (!lq.validWorld(p.getWorld().getName())) {
			p.sendMessage(lq.configLang.invalidWorld);
			return true;
		}

		final PC pc = lq.players.getPC(p);

		if (args.length < 1) { // why am i worried about negative argument length ? le-sigh
			// ok - just list the players race name here.
			sender.sendMessage(lq.configLang.youAreCurrently + ": " + pc.race.name);
			return true;
		} else {
			String raceName = args[0].toLowerCase();
			if (raceName.equalsIgnoreCase("list")) {
				boolean full = false;
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("full")) {
						full = true;
					}
				}
				sendRaceList(sender, pc,full);
				return true;
			} else if (raceName.equalsIgnoreCase("info")) {
				if (args.length < 2) {
					sender.sendMessage(lq.configLang.invalidArgumentsCommand);
				} else {
					raceName = args[1].toLowerCase();
					raceinfo(raceName, sender, pc);
				}
				return true;
			} else {

				final Race r = lq.races.getRace(raceName);
				if (r == null) {
					sender.sendMessage(lq.configLang.raceInvalid + ": " + raceName);
					return true;
				} else {
					if (pc.raceChanged) {
						if (!lq.configMain.allowRaceSwap) {
							sender.sendMessage(lq.configLang.raceChangeNotAllowed + ": " + pc.race.name);
							return true;
						}
						final boolean confirm = (args[args.length - 1].equalsIgnoreCase("confirm"));
						if (lq.configMain.percentXpKeepClassChange < 100 && !confirm) {
							sender.sendMessage(lq.configLang.raceChangeWarnXpLoss);
							sender.sendMessage(lq.configLang.raceConfirm);
							return true;
						}
					}
					lq.debug.fine("Perm.: " + r.perm);
					if (r.perm != null) {
						lq.debug.fine("has Perm.: " + p.hasPermission(r.perm));
						if (!p.hasPermission(r.perm)) {
							sender.sendMessage(lq.configLang.raceNotAllowed);
							return true;
						}
					}

					boolean result = pc.changeRace(r);
					if (result) {
						sender.sendMessage(lq.configLang.raceChanged + ": " + raceName);
					}
					return true;
				}
			}
		}
	}

	public void raceinfo(String raceName, CommandSender sender, PC pc) {
		final Race r = lq.races.getRace(raceName);
		if (r == null) {
			sender.sendMessage(lq.configLang.raceInvalid + ": " + raceName);
			return;
		} else {
			sender.sendMessage(r.name + " : " + r.description);
			sender.sendMessage(r.longdescription);
		}
	}

	private void sendRaceList(final CommandSender sender, final PC pc, boolean full) {
		sender.sendMessage(lq.configLang.raceList);
		String strout;
		for (final Race rc : lq.races.getRaces().values()) {
			if (pc != null) {
				if (!(rc.perm == null || rc.perm.equalsIgnoreCase("") || ((Player) sender).isPermissionSet(rc.perm))) {
					continue;
				}
			}
			strout = " - " + rc.name;
			if (rc.defaultRace) {
				strout += " *";
			}
			if (pc != null && pc.race.equals(rc)) {
				strout += " <";
			}
			sender.sendMessage(strout);
			if (full) {
				sender.sendMessage("   " + rc.description);
			}
		}
	}
}