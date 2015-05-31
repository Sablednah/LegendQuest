package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdFlag extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdFlag(final Main p) {
		this.lq = p;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		// get the enum for this command
		final Cmds cmd = Cmds.FLAG;

		if (!validateCmd(lq, cmd, sender, args)) {
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(lq.configLang.invalidArgumentsCommand);
			return true;
		}

		String targetName = null;

		if (args.length < 3) {
			if ((sender instanceof Player)) {
				targetName = ((Player) sender).getName();
			} else {
				// need key + value + player
				sender.sendMessage(lq.configLang.invalidArgumentsCommand);
				return true;
			}
		} else {
			if (sender instanceof Player) {
				if (sender.isOp()) {
					targetName = args[2];					
				} else {
					sender.sendMessage(lq.configLang.invalidArgumentsCommand);
					return true;
				}
			} else {
				targetName = args[2];
			}
		}
		String key = args[0];
		String value = args[1];

		if (!(key.equalsIgnoreCase("hidesidebar")  || key.equalsIgnoreCase("test"))) {
			sender.sendMessage(lq.configLang.invalidArgumentsCommand);
			return true;
		}
		
		PC pc = null;
		if (targetName != null) {
			pc = lq.players.getPC(targetName);
		}

		if (pc != null) {
			pc.putData(key, value);
			sender.sendMessage(key + " = " + value);
			return true;
		} else {
			sender.sendMessage(lq.configLang.invalidArgumentsCommand);
			return true;
		}

//		return false;

	}
}