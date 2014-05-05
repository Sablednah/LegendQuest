package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdKarma extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdKarma(final Main p) {
		this.lq = p;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		// get the enum for this command
		final Cmds cmd = Cmds.valueOf("KARMA");

		if (!validateCmd(lq, cmd, sender, args)) {
			return true;
		}

		final boolean isPlayer = (sender instanceof Player);
		String targetName = null;

		if (args.length > 0) {
			targetName = args[0];
			if (targetName.equals("test")) {
				PC pc2 = lq.players.getPC(Utils.getPlayerUUID("sablednah"));
				sender.sendMessage("5 : " + pc2.karmaName(5));
				sender.sendMessage("50 : " + pc2.karmaName(50));
				sender.sendMessage("500 : " + pc2.karmaName(500));
				sender.sendMessage("5000 : " + pc2.karmaName(5000));
				sender.sendMessage("50000 : " + pc2.karmaName(50000));
				sender.sendMessage("500000 : " + pc2.karmaName(500000));
				sender.sendMessage("5000000 : " + pc2.karmaName(5000000));
				sender.sendMessage("50000000 : " + pc2.karmaName(50000000));
				sender.sendMessage("500000000 : " + pc2.karmaName(500000000));
				sender.sendMessage("5000000000 : " + pc2.karmaName(5000000000L));
				sender.sendMessage("50000000000 : " + pc2.karmaName(50000000000L));
				sender.sendMessage("500000000000 : " + pc2.karmaName(500000000000L));
				sender.sendMessage("-5 : " + pc2.karmaName(-5));
				sender.sendMessage("-50 : " + pc2.karmaName(-50));
				sender.sendMessage("-500 : " + pc2.karmaName(-500));
				sender.sendMessage("-5000 : " + pc2.karmaName(-5000));
				sender.sendMessage("-50000 : " + pc2.karmaName(-50000));
				sender.sendMessage("-500000 : " + pc2.karmaName(-500000));
				sender.sendMessage("-5000000 : " + pc2.karmaName(-5000000));
				sender.sendMessage("-50000000 : " + pc2.karmaName(-50000000));
				sender.sendMessage("-500000000 : " + pc2.karmaName(-500000000));
				sender.sendMessage("-5000000000 : " + pc2.karmaName(-5000000000L));
				sender.sendMessage("-50000000000 : " + pc2.karmaName(-50000000000L));
				sender.sendMessage("-500000000000 : " + pc2.karmaName(-500000000000L));
				return true;
			}
		} else {
			if (isPlayer) {
		        if (!lq.validWorld(((Player)sender).getWorld().getName())) {
		        	((Player)sender).sendMessage(lq.configLang.invalidWorld);
		        	return true;
		        }
				targetName = sender.getName();
			} else {
				sender.sendMessage(cmd.toString() + ": " + lq.configLang.invalidArgumentsCommand);
				return true;
			}
		}

		PC pc = null;
		if (targetName != null) {
			pc = lq.players.getPC(Utils.getPlayerUUID(targetName));
		}
		if (pc != null) {
			sender.sendMessage(lq.configLang.statKarma + ": " + pc.karmaName() + " (" + pc.karma + ")");
			return true;
		} else {
			sender.sendMessage(lq.configLang.characterNotFound + targetName);
			return true;
		}
	}
}
