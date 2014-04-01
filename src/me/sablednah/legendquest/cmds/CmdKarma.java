package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdKarma extends CommandTemplate implements CommandExecutor {

    public Main lq;

    public CmdKarma(final Main p) {
        this.lq = p;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        // TODO Auto-generated method stub
        // get the enum for this command
        final Cmds cmd = Cmds.valueOf("KARMA");

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
            sender.sendMessage(lq.configLang.statKarma + ": " + pc.karma);
            return true;
        } else {
            sender.sendMessage(lq.configLang.characterNotFound + targetName);
            return true;
        }
    }
}
