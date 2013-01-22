package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTemplate {

	public boolean validateCmd(Main lq, Cmds cmd, CommandSender sender, String[] args) {
		if (args.length < cmd.minArgLength()) {
			sender.sendMessage(cmd.toString() + ": " + lq.configLang.invalidArgumentsCommand);
			return false;
		}

		if (!(sender instanceof Player) && !cmd.canConsole()) {
			sender.sendMessage(cmd.toString() + ": " + lq.configLang.invalidPlayerCommand);
			return false;
		}
		return true;
	}
	 
}

