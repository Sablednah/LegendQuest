package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CmdUnlink extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdUnlink(Main p) {
		this.lq = p;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// get the enum for this command
		final Cmds cmd = Cmds.valueOf("UNLINK");
		if (!validateCmd(lq, cmd, sender, args)) {
			return true;
		}

		// from here on is command specific code.

		// reject non-player senders e.g. chats bots etc
		if (!(sender instanceof Player)) {
			return false;
		}

		// only players left here
		Player p = (Player) sender;

		if (!lq.validWorld(p.getWorld().getName())) {
			p.sendMessage(lq.configLang.invalidWorld);
			return true;
		}

		PC pc = lq.players.getPC(p);
		pc.checkSkills();

		ItemStack holding = p.getItemInHand();
		if (holding == null) {
			p.sendMessage(lq.configLang.skillLinkEmptyHand);
			return true;
		}

		sender.sendMessage(lq.configLang.skillUnlinked + holding.getType().toString());
		pc.RemoveLink(holding.getType());
		return true;
	}
}
