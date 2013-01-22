package me.sablednah.legendquest.cmds;

import java.util.Map;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.SetExp;

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
		
		boolean isPlayer = (sender instanceof Player);
		String targetName = null;
		
		if (isPlayer) {
			targetName = sender.getName();
		} else {
			if (args.length>0) {
				targetName=args[0];
			} else {
				sender.sendMessage(cmd.toString() + ": " + lq.configLang.invalidArgumentsCommand);
				return true;
			}
		}
		
		PC pc = null;
		if (targetName!=null) {
			pc = lq.players.getPC(targetName);
		}
		if (pc!=null) {
			sender.sendMessage(lq.configLang.playerStats);
			sender.sendMessage(lq.configLang.playerName +": " + pc.charname + "("+targetName+")");
			sender.sendMessage(lq.configLang.statSTR +": "+pc.getStatStr());
			sender.sendMessage(lq.configLang.statDEX +": "+pc.getStatDex());
			sender.sendMessage(lq.configLang.statINT +": "+pc.getStatInt());
			sender.sendMessage(lq.configLang.statWIS +": "+pc.getStatWis());
			sender.sendMessage(lq.configLang.statCON +": "+pc.getStatCon());
			sender.sendMessage(lq.configLang.statCHR +": "+pc.getStatChr());
			sender.sendMessage(lq.configLang.statRace +": "+pc.race.name);
			sender.sendMessage(lq.configLang.statClass +": "+pc.mainClass.name);
			
			Player p= Bukkit.getServer().getPlayer(targetName);
			
			if (p!=null) {
				sender.sendMessage(lq.configLang.statHealth + ": "+p.getHealth() + " / " + p.getMaxHealth());
			} else {
				sender.sendMessage(lq.configLang.statHealth + ": "+pc.health + " / " + pc.maxHP);
			}
			
			for (Map.Entry<String, Integer> entry : pc.xpEarnt.entrySet()) {
				sender.sendMessage("XP: " + entry.getKey().toLowerCase() + ": "+SetExp.getLevelOfXpAmount(entry.getValue())+" (" + entry.getValue() + ")" );
			}
			
			return true;
		} else {
			sender.sendMessage(lq.configLang.characterNotFound + targetName);
			return true;
		}
	}
}
