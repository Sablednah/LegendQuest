package me.sablednah.legendquest.cmds;

import java.util.Map;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.SetExp;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdStats extends CommandTemplate implements CommandExecutor {

    public Main lq;

    public CmdStats(final Main p) {
        this.lq = p;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        // TODO Auto-generated method stub
        // get the enum for this command
        final Cmds cmd = Cmds.valueOf("STATS");

        if (!validateCmd(lq, cmd, sender, args)) {
            return true;
        }

        final boolean isPlayer = (sender instanceof Player);
        String targetName = null;

        if (isPlayer) {
            targetName = sender.getName();
        } else {
            if (args.length > 0) {
                targetName = args[0];
            } else {
                sender.sendMessage(cmd.toString() + ": " + lq.configLang.invalidArgumentsCommand);
                return true;
            }
        }

        PC pc = null;
        if (targetName != null) {
            pc = lq.players.getPC(targetName);
        }
        if (pc != null) {
            sender.sendMessage(lq.configLang.playerStats);
            sender.sendMessage(lq.configLang.playerName + ": " + pc.charname + " (" + targetName + ")");
            sender.sendMessage(lq.configLang.statSTR + ": " + pc.getStatStr());
            sender.sendMessage(lq.configLang.statDEX + ": " + pc.getStatDex());
            sender.sendMessage(lq.configLang.statINT + ": " + pc.getStatInt());
            sender.sendMessage(lq.configLang.statWIS + ": " + pc.getStatWis());
            sender.sendMessage(lq.configLang.statCON + ": " + pc.getStatCon());
            sender.sendMessage(lq.configLang.statCHR + ": " + pc.getStatChr());
            sender.sendMessage(lq.configLang.statRace + ": " + pc.race.name);
            sender.sendMessage(lq.configLang.statClass + ": " + pc.mainClass.name);

            sender.sendMessage("--------------------");

            final Player p = Bukkit.getServer().getPlayer(targetName);

            if (p != null) {
                sender.sendMessage(Utils.barGraph(p.getHealth(), pc.getMaxHealth(), 20, lq.configLang.statHealth, (" " + p.getHealth() + " / " + p.getMaxHealth())));
            } else {
                sender.sendMessage(Utils.barGraph(pc.health, pc.maxHP, 20, lq.configLang.statHealth, (" " + pc.health + " / " + pc.maxHP)));
            }

            sender.sendMessage(Utils.barGraph(pc.mana, pc.getMaxMana(), 20, lq.configLang.statMana, (" " + pc.mana + " / " + pc.getMaxMana())));
            sender.sendMessage("--------------------");

            for (final Map.Entry<String, Integer> entry : pc.xpEarnt.entrySet()) {
                sender.sendMessage("XP: " + entry.getKey().toLowerCase() + ": " + lq.configLang.statLevelShort + " " + SetExp.getLevelOfXpAmount(entry.getValue()) + " ("
                        + entry.getValue() + ")");
            }

            // sender.sendMessage("--------------------");

            return true;
        } else {
            sender.sendMessage(lq.configLang.characterNotFound + targetName);
            return true;
        }
    }
}
