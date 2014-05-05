package me.sablednah.legendquest.cmds;

import java.util.HashMap;
import java.util.Map.Entry;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CmdLink extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdLink(Main p) {
		this.lq = p;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// get the enum for this command
		final Cmds cmd = Cmds.valueOf("LINK");
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
		if (holding==null) {
			p.sendMessage(lq.configLang.skillLinkEmptyHand);
			return true;
		}
		
		String actionName = args[0].toLowerCase();
		if (actionName.equalsIgnoreCase("list")) {
			sendBindingList(p, pc);
			return true;
		}  else {
			sender.sendMessage(actionName+lq.configLang.skillLinked+holding.getType().toString());
			pc.addLink(holding.getType(), actionName);
			return true;
		}
	}
	
	public void sendBindingList(Player sender,PC pc) {
		HashMap<Material, String> list = pc.skillLinkings;
		String out = lq.configLang.skillLinkList;
		for (Entry<Material, String> entry : list.entrySet()) {
			out += "[ "+(entry.getKey().toString() + " : " + entry.getValue())+ " ] ";
		}
		sender.sendMessage(out);
	}
}
