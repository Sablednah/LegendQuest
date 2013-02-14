package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTemplate {

    public boolean validateCmd(final Main lq, final Cmds cmd, final CommandSender sender, final String[] args) {
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
