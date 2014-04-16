package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdHP extends CommandTemplate implements CommandExecutor {

    public Main lq;

    public CmdHP(final Main p) {
        this.lq = p;
    }

    @SuppressWarnings("deprecation")
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        // get the enum for this command
        final Cmds cmd = Cmds.valueOf("HP");

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
            pc = lq.players.getPC(Utils.getPlayerUUID(targetName));
        }
        if (pc != null) {
            sender.sendMessage(Utils.barGraph(pc.health, pc.maxHP, 20, lq.configLang.statHealth, (" " + pc.health + " / " + pc.maxHP)));
            sender.sendMessage(Utils.barGraph(pc.mana, pc.getMaxMana(), 20, lq.configLang.statMana, (" " + pc.mana + " / " + pc.getMaxMana())));
            return true;
        } else {
            sender.sendMessage(lq.configLang.characterNotFound + targetName);
            return true;
        }
    }
}
