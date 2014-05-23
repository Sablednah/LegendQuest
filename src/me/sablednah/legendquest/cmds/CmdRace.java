package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.races.Race;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdRace extends CommandTemplate implements CommandExecutor {
    
    public Main lq;
    
    public CmdRace(final Main p) {
        this.lq = p;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        // get the enum for this command
        final Cmds cmd = Cmds.valueOf("RACE");
        
        if (!validateCmd(lq, cmd, sender, args)) {
            return true;
        }
        
        // send console the list list
        if (!(sender instanceof Player)) {
            sendRaceList(sender, null);
            return true;
        }
        
        final Player p = (Player) sender;
        
        if (!lq.validWorld(p.getWorld().getName())) {
        	p.sendMessage(lq.configLang.invalidWorld);
        	return true;
        }

        final PC pc = lq.players.getPC(p);
        
        if (args.length < 1) { // why am i worried about negative argument length ? le-sigh
            // ok - just list the players race name here.
            sender.sendMessage(lq.configLang.youAreCurrently + ": " + pc.race.name);
            return true;
        } else {
            final String raceName = args[0].toLowerCase();
            if (raceName.equalsIgnoreCase("list")) {
                sendRaceList(sender, pc);
                return true;
            } else {
                final Race r = lq.races.getRace(raceName);
                if (r == null) {
                    sender.sendMessage(lq.configLang.raceInvalid + ": " + raceName);
                    return true;
                } else {
                    if (pc.raceChanged) {
                        sender.sendMessage(lq.configLang.raceChangeNotAllowed + ": " + pc.race.name);
                        return true;
                    } else {
                        lq.debug.fine("Perm.: " + r.perm);
                        if (r.perm != null) {
                            lq.debug.fine("has Perm.: " + p.hasPermission(r.perm));
                            if (!p.hasPermission(r.perm)) {
                                sender.sendMessage(lq.configLang.raceNotAllowed);
                                return true;
                            }
                        }
                        
                        pc.race = r;
                        pc.raceChanged = true;
                        lq.players.addPlayer(p.getUniqueId(), pc);
                        lq.players.savePlayer(p.getUniqueId());
                        
                        pc.scheduleHealthCheck();
                        pc.checkInv();
                        pc.skillSet = pc.getUniqueSkills(true);
                        
                        sender.sendMessage(lq.configLang.raceChanged + ": " + raceName);
                        return true;
                    }
                }
            }
        }
    }
    
    private void sendRaceList(final CommandSender sender, final PC pc) {
        sender.sendMessage(lq.configLang.raceList);
        String strout;
        for (final Race rc : lq.races.getRaces().values()) {
            if (pc != null) {
                if (!(rc.perm == null || rc.perm.equalsIgnoreCase("") || ((Player) sender).isPermissionSet(rc.perm))) {
                    continue;
                }
            }
            strout = " - " + rc.name;
            if (rc.defaultRace) {
                strout += " *";
            }
            if (pc != null && pc.race.equals(rc)) {
                strout += " <";
            }
            sender.sendMessage(strout);
        }
        
    }
}