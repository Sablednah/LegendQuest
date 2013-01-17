package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdStats extends CommandTemplate implements CommandExecutor {
	
	public Main	lq;

	public CmdStats(Main p) {
		this.lq = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		// get the enum for this command
		Cmds cmd = Cmds.valueOf("STATS");

		if (!validateCmd(lq, cmd, sender, args)) { return true; }
		
		sender.sendMessage("Executing "+cmd.toString()+" command");

		boolean isPlayer = (sender instanceof Player);
		String targetName = null;
		
		if (isPlayer) {
			targetName = sender.getName();
		} else {
			if (args.length>0) {
				targetName=args[0];
			} else {
				sender.sendMessage(cmd.toString() + ": " + lq.configMain.invalidArgumentsCommand);
				return true;
			}
		}
		
		PC pc = null;
		if (targetName!=null) {
			pc = lq.players.getPC(targetName);
		}
		if (pc!=null) {
			sender.sendMessage(pc.charname + "("+targetName+")");
			sender.sendMessage("STR: "+pc.getStatStr());
			sender.sendMessage("DEX: "+pc.getStatDex());
			sender.sendMessage("INT: "+pc.getStatInt());
			sender.sendMessage("WIS: "+pc.getStatWis());
			sender.sendMessage("CON: "+pc.getStatCon());
			sender.sendMessage("CHR: "+pc.getStatChr());
			sender.sendMessage("Race: "+pc.race.name);
			sender.sendMessage("Class: "+pc.mainClass.name);
			
			Player p= Bukkit.getServer().getPlayer(targetName);
			
			if (p!=null) {
				sender.sendMessage("Health: "+p.getHealth() + " / " + p.getMaxHealth());
			} else {
				sender.sendMessage("Health: "+pc.health + " / " + pc.maxHP);
			}

			return true;
		} else {
			sender.sendMessage(lq.configMain.characterNotFound + targetName);
			return true;
		}
	}
}
