package me.sablednah.legendquest.cmds;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.Classes;
import me.sablednah.legendquest.config.LangConfig;
import me.sablednah.legendquest.config.MainConfig;
import me.sablednah.legendquest.config.SkillConfig;
import me.sablednah.legendquest.db.DataSync;
import me.sablednah.legendquest.playercharacters.PCs;
import me.sablednah.legendquest.races.Races;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

public class RootCommand implements CommandExecutor {
    
    public Main lq;
    
    public RootCommand(final Main p) {
        this.lq = p;
    }
    
	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        String cmd;
        
        if (!(args.length > 0)) {
            cmd = "help";
        } else {
            cmd = args[0];
            if (cmd.equalsIgnoreCase("test")) {
            	if (args[1].equalsIgnoreCase("xp")) {
            		float Exp = Float.parseFloat(args[2]);
                    int ExpOrbs = (int) Exp;
                    World world = ((Player)sender).getWorld();
                    ((ExperienceOrb)world.spawn(((Player)sender).getTargetBlock((HashSet<Byte>) null, 200).getLocation(), ExperienceOrb.class)).setExperience( ExpOrbs );
            		return true;
            	} else {
            	sender.sendMessage("effect test");
            	Effect eff = Effect.valueOf(args[1]);
				Location l = ((Player)sender).getTargetBlock((HashSet<Byte>) null, 64).getLocation();
            	Utils.playEffect(eff, l, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            	return true;
            	}
            }
        }
        
        lq.debug.fine("cmd: " + cmd);
        
        String[] newArglist;
        if (args.length > 1) {
            newArglist = Arrays.copyOfRange(args, 1, args.length);
        } else {
            newArglist = new String[0];
        }
        
        lq.debug.fine("args.length: " + args.length);
        lq.debug.fine("newArglist.length: " + newArglist.length);
        
        final boolean isPlayer = (sender instanceof Player);
        
        lq.debug.fine("isPlayer: " + isPlayer);
        
        try {
            final Cmds c = Cmds.valueOf(cmd.toUpperCase());
            
            lq.debug.fine("Cmds: " + c);
            lq.debug.fine("Cmds: " + c.toString());
            lq.debug.fine("test: " + (c == Cmds.STATS));
            
            // player check here
            if (!isPlayer && !c.canConsole()) {
                // player only command used from console - reject and end command
                sender.sendMessage(cmd + ": " + lq.configLang.invalidPlayerCommand);
                // we're sending our own "failed" message so say it worked ok to prevent default
                return true;
            }
            
            CommandExecutor newcmd = null;
            
            switch (c) {
                case HELP:
                    // TODO add proper help messages
                    sendMultilineMessage(sender, lq.configLang.helpCommand);
                    return true;
                case RELOAD:
                	
                	for (Player player : lq.getServer().getOnlinePlayers()) {
            			UUID uuid = player.getUniqueId();
            			lq.players.removePlayer(uuid);
            		}
            		lq.datasync.shutdown();
                    
            		lq.configMain = new MainConfig(lq);
                    lq.configLang = new LangConfig(lq);
                    lq.configSkills = new SkillConfig(lq);
                    
                    lq.players = null;
                    lq.datasync = null;
                    lq.classes = null;
                    lq.races = null;

                    lq.races = new Races(lq);
                    lq.classes = new Classes(lq);
            		lq.datasync = new DataSync(lq);
                    lq.players = new PCs(lq);
                	
                    sender.sendMessage(lq.configLang.commandReloaded);
                    return true;
                case RACE:
                    newcmd = new CmdRace(lq);
                    break;
                case CLASS:
                    newcmd = new CmdClass(lq);
                    break;
                case STATS:
                    newcmd = new CmdStats(lq);
                    break;
                case ROLL:
                    newcmd = new CmdRoll(lq);
                    break;
                case KARMA:
                    newcmd = new CmdKarma(lq);
                    break;
                case HP:
                    newcmd = new CmdHP(lq);
                    break;
                case LINK:
                    newcmd = new CmdLink(lq);
                    break;
                case UNLINK:
                    newcmd = new CmdUnlink(lq);
                    break;
                case SKILL:
                    newcmd = new CmdSkill(lq);
                    break;
                case PARTY:
                    newcmd = new CmdParty(lq);
                    break;
                case FLAG:
                    newcmd = new CmdFlag(lq);
                    break;
                case ADMIN:
                    newcmd = new CmdAdmin(lq);
                    break;                    
            }
            
            lq.debug.fine("newcmd: " + newcmd);
            
            if (newcmd != null) {
                return newcmd.onCommand(sender, command, label, newArglist);
            }
            
        } catch (final IllegalArgumentException e) {
            String error = lq.configLang.invalidCommand + cmd + " :(";
            sender.sendMessage(error);
            lq.debug.error(error);
            // e.printStackTrace();
            return false;
        }
        
        return false;
    }
    
    public void sendMultilineMessage(final CommandSender send, final String message) {
        if (send != null && message != null) {
            final String[] s = message.split("\n");
            for (final String m : s) {
                send.sendMessage(m);
            }
        }
    }
    
}
