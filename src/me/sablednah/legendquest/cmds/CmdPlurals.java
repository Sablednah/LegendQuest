package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdPlurals implements CommandExecutor {
    
    public Main lq;
    
    public CmdPlurals(final Main p) {
        this.lq = p;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        CommandExecutor newcmd = null;
        String[] newArglist = new String[1];
        newArglist[0]="list";
        if (label.equalsIgnoreCase("skills")) {
            newcmd = new CmdSkill(lq);
            return newcmd.onCommand(sender, command, label, newArglist);
        } else if (label.equalsIgnoreCase("classes")) {
            newcmd = new CmdClass(lq);
            return newcmd.onCommand(sender, command, label, newArglist);
        } else if (label.equalsIgnoreCase("races")) {
            newcmd = new CmdRace(lq);
            return newcmd.onCommand(sender, command, label, newArglist);
        } else if (label.equalsIgnoreCase("links")) {
            newcmd = new CmdLink(lq);
            return newcmd.onCommand(sender, command, label, newArglist);
        }  else if (label.equalsIgnoreCase("binds")) {
            newcmd = new CmdLink(lq);
            return newcmd.onCommand(sender, command, label, newArglist);
        }
        return false;
    }    
}
