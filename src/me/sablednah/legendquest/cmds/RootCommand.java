package me.sablednah.legendquest.cmds;

import java.util.Arrays;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.Classes;
import me.sablednah.legendquest.races.Races;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RootCommand implements CommandExecutor {

	public Main	lq;

	public RootCommand(Main p) {
		this.lq = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmd;

		if (!(args.length > 0)) {
			cmd = "help";
		} else {
			cmd = args[0];
		}
		
		lq.debug.fine("cmd: " + cmd);
		
		String[] newArglist;
		if (args.length > 1) {
			newArglist = Arrays.copyOfRange(args, 1, args.length);
		} else {
			newArglist = new String[0];
		}

		lq.debug.fine("args.length: " + args.length);
		lq.debug.fine("newArglist.length: " + newArglist.length);
		
		boolean isPlayer = (sender instanceof Player);
		
		lq.debug.fine("isPlayer: " + isPlayer);

		try {
			Cmds c = Cmds.valueOf(cmd.toUpperCase());
			
			lq.debug.fine("Cmds: " + c);
			lq.debug.fine("Cmds: " + c.toString());
			lq.debug.fine("test: " + (c==Cmds.STATS));

			// player check here
			if (!isPlayer && !c.canConsole()) {
				// player only command used from console - reject and end command
				sender.sendMessage(cmd + ": " + lq.configLang.invalidPlayerCommand);
				// we're sending our own "failed" message so say it worked ok to prevent default
				return true;
			}

			CommandExecutor newcmd = null;

			switch (c) {
				case HELP:
					// TODO add proper help messages
					sendMultilineMessage(sender, lq.configLang.helpCommand);
					return true;
				case RELOAD:
					lq.configMain.reloadConfig();
					lq.configLang.reloadConfig();
					lq.classes = null;
					lq.races = null;
					lq.races = new Races(lq);
					lq.classes = new Classes(lq);
					sender.sendMessage(lq.configLang.commandReloaded);
					return true;
				case RACE:
					newcmd = new CmdRace(lq);
					break;
				case CLASS:
					newcmd = new CmdClass(lq);
					break;
				case STATS:
					newcmd = new CmdStats(lq);
					break;
			}

			lq.debug.fine("newcmd: " + newcmd);

			if (newcmd != null) {
				return newcmd.onCommand(sender, command, label, newArglist);
			}

		} catch (IllegalArgumentException e) {
			sender.sendMessage(lq.configLang.invalidCommand + cmd + " :(");
			e.printStackTrace();
			return false;
		}

		return false;
	}

	public void sendMultilineMessage(CommandSender send, String message) {
		if (send != null && message != null) {
			String[] s = message.split("\n");
			for (String m : s) {
				send.sendMessage(m);
			}
		}
	}

}
