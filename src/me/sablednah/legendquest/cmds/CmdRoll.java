package me.sablednah.legendquest.cmds;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.mechanics.Attribute;
import me.sablednah.legendquest.mechanics.Difficulty;
import me.sablednah.legendquest.mechanics.Mechanics;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdRoll extends CommandTemplate implements CommandExecutor {
    
    public Main lq;
    
    public CmdRoll(final Main p) {
        this.lq = p;
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        // TODO Auto-generated method stub
        // get the enum for this command
        final Cmds cmd = Cmds.valueOf("ROLL");
        
        if (!validateCmd(lq, cmd, sender, args)) {
            return true;
        }
        
        final boolean isPlayer = (sender instanceof Player);
        String targetName = null;
        Integer targetNum = null;
        Attribute attr = null;
        
        if (args.length > 2) {
            if (targetNum == null) {
            try {
                if (Utils.isInteger(args[2])) {
                    targetNum = Integer.parseInt(args[2]);
                }
                targetNum = Difficulty.valueOf(args[2].toUpperCase()).getDifficulty();
            } catch (Exception e) {
                // do nothing
                //lq.debug.fine(e.getMessage());
                //lq.debug.thrown("CmdRoll", "onCommand", e);
            }
            }
            if (targetName == null) {
                if (lq.getServer().getPlayer(args[2]) != null) {
                    targetName = lq.getServer().getPlayer(args[2]).getName();
                }
            }
            if (attr == null) {
                try {
                    attr = Attribute.valueOf(args[2].toUpperCase());
                } catch (Exception e) {
                    // do nothing
                    //lq.debug.fine(e.getMessage());
                    //lq.debug.thrown("CmdRoll", "onCommand", e);
                }
            }
        }
        
            if (args.length > 1) {
                if (targetNum == null) {
                try {
                    if (Utils.isInteger(args[1])) {
                        targetNum = Integer.parseInt(args[1]);
                    }
                    targetNum = Difficulty.valueOf(args[1].toUpperCase()).getDifficulty();
                } catch (Exception e) {
                    // do nothing
                    //lq.debug.fine(e.getMessage());
                    //lq.debug.thrown("CmdRoll", "onCommand", e);
                }
                }
                if (targetName == null) {
                    if (lq.getServer().getPlayer(args[1]) != null) {
                        targetName = lq.getServer().getPlayer(args[1]).getName();
                    }
                }
                if (attr == null) {
                    try {
                        attr = Attribute.valueOf(args[1].toUpperCase());
                    } catch (Exception e) {
                        // do nothing
                        //lq.debug.fine(e.getMessage());
                        //lq.debug.thrown("CmdRoll", "onCommand", e);
                    }
                }
                } 
            
        if (args.length > 0) {
            if (targetNum == null) {
                try {
                    if (Utils.isInteger(args[1])) {
                        targetNum = Integer.parseInt(args[0]);
                    }
                    targetNum = Difficulty.valueOf(args[0].toUpperCase()).getDifficulty();
                } catch (Exception e) {
                    // do nothing
                    //lq.debug.fine(e.getMessage());
                    //lq.debug.thrown("CmdRoll", "onCommand", e);
                }
            }
            if (targetName == null) {
                if (lq.getServer().getPlayer(args[0]) != null) {
                    targetName = lq.getServer().getPlayer(args[0]).getName();
                }
            }
            if (attr == null) {
                try {
                    attr = Attribute.valueOf(args[0].toUpperCase());
                } catch (Exception e) {
                    // do nothing
                    //lq.debug.fine(e.getMessage());
                    //lq.debug.thrown("CmdRoll", "onCommand", e);
                }
            }
        }
        
        if (targetNum==null) {
            targetNum = 10;
        }
        
        if (targetName == null) {
            
            if (isPlayer) {
                targetName = sender.getName();
            }
        }
        
        PC pc = null;
        if (targetName != null) {
            pc = lq.players.getPC(targetName);
        }
        int result = 10;
        if (pc != null) {
            result = pc.skillTest(targetNum, attr);
        } else {
            result = Mechanics.diceRoll(targetNum);
        }
        //lq.configLang.characterNotFound
        
        if (result>=0) {
            sender.sendMessage(lq.configLang.commandRollSucess+" (" + result +")");
        } else {
            sender.sendMessage(lq.configLang.commandRollFail+" (" + (0-result) +")");
        }
        lq.debug.fine("Tested player '"+targetName+"' using "+attr+" and dificulty "+targetNum + " : result = "+result);
        return true;
    }
}
