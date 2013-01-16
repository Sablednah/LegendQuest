package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdClass implements CommandExecutor {

	public Main	lq;

	public CmdClass(Main p) {
		this.lq = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		// get the enum for this command
		Cmds cmd = Cmds.valueOf("CLASS");
		boolean isPlayer = (sender instanceof Player);

		if (args.length < cmd.minArgLength()) {
			sender.sendMessage(cmd.toString() + ": " + lq.configMain.invalidArgumentsCommand);
			return true;
		}

		if (!isPlayer && !cmd.canConsole()) {
			sender.sendMessage(cmd.toString() + ": " + lq.configMain.invalidPlayerCommand);
			return true;
		}
		sender.sendMessage("Executing "+cmd.toString()+" command");

		return false;
	}

}
