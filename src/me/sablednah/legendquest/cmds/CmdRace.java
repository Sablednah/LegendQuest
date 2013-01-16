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

	public CmdRace(Main p) {
		this.lq = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		// get the enum for this command
		Cmds cmd = Cmds.valueOf("RACE");

		if (!validateCmd(lq, cmd, sender, args)) {
			return true;
		}

		// from her on is command specific code.
		sender.sendMessage("Executing " + cmd.toString() + " command");

		Player p = (Player) sender;
		PC pc = lq.players.getPC(p);

		if (args.length < 1) { // why am i worried about negative argument length ? le-sigh
			// ok - just list the players race name here.
			sender.sendMessage(lq.configLang.youAreCurrently + ": " + pc.race.name);
			return true;
		} else {
			String raceName = args[0].toLowerCase();
			if (raceName.equalsIgnoreCase("list")) {
				sender.sendMessage(lq.configLang.raceList);
				String strout;
				for (Race rc : lq.races.races.values()) {
					strout = " - " + rc.name;
					if (rc.defaultRace) {
						strout += " *";
					}
					if (pc.race.equals(rc)) {
						strout += " <";
					}
					sender.sendMessage(strout);
				}
				return true;
			} else {
				Race r = lq.races.races.get(raceName);
				if (r == null) {
					sender.sendMessage(lq.configLang.raceScanInvalid + ": " + raceName);
					return true;
				} else {
					if (pc.raceChanged) {
						sender.sendMessage(lq.configLang.raceChangeNotAllowed + ": " + pc.race.name);
						return true;
					} else {
						pc.race = r;
						pc.raceChanged = true;
						lq.players.addPlayer(p.getName(), pc);
						lq.players.savePlayer(p.getName());
						sender.sendMessage(lq.configLang.raceChanged + ": " + raceName);
						return true;
					}
				}
			}
		}
	}
}