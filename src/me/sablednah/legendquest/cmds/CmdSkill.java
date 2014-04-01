package me.sablednah.legendquest.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.races.Race;
import me.sablednah.legendquest.skills.SkillDefinition;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSkill extends CommandTemplate implements CommandExecutor {
    
    public Main lq;
    
    public CmdSkill(final Main p) {
        this.lq = p;
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        // get the enum for this command
        final Cmds cmd = Cmds.valueOf("SKILL");
        if (!validateCmd(lq, cmd, sender, args)) {
            return true;
        }
        
        // from here on is command specific code.
        
        // send console the full skill list
        if (!(sender instanceof Player)) {
            sendSkillList(sender, null);
            return true;
        }
        
        // only players left here
        final Player p = (Player) sender;
        final PC pc = lq.players.getPC(p);
        pc.checkSkills();
        if (args.length < 1) { // why am i worried about negative argument length ? le-sigh
            // ok - just list the players skill names here.
            String msg;
            if (pc.skillsSelected != null && !pc.skillsSelected.isEmpty()) {
                final List<String> skillnames = new ArrayList<String>();
                for (final Entry<String, Boolean> s : pc.skillsSelected.entrySet()) {
                    if (s.getValue()) {
                        skillnames.add(s.getKey());
                    }
                }
                msg = lq.configLang.hasSkills + " " + Utils.join(skillnames.toArray(new String[0]), ",");
            } else {
                msg = lq.configLang.noSkills;
            }
            sender.sendMessage(msg);
            return true;
        } else {
            final String actionName = args[0].toLowerCase();
            if (actionName.equalsIgnoreCase("list")) {
                sendSkillList(sender, pc);
                return true;
            } else if (actionName.equalsIgnoreCase("buy")) {
                if (args.length < 2) {
                    sender.sendMessage("buy what now?");
                } else {
                    final String skillToBuy = args[1].toLowerCase();
                    sender.sendMessage("Will try to buy " + skillToBuy);
                }
                return true;
            } else {
                sender.sendMessage("Will try to use " + actionName);
                return true;
            }
        }
    }
    
    private void sendSkillList(final CommandSender sender, final PC pc) {
        sender.sendMessage(lq.configLang.skillsList);
        String strout;
        
        if (pc == null || !(sender instanceof Player)) {
            // send a full list
            for (final ClassType cls : lq.classes.getClassTypes().values()) {
                for (final SkillDefinition s : cls.availableSkills) {
                    strout = " - " + s.getSkillInfo().name + " [" + lq.configLang.statLevelShort + " " + s.getSkillInfo().levelRequired + " | " + lq.configLang.statSp + " " + s.getSkillInfo().skillPoints + "]";
                    sender.sendMessage(strout);
                }
            }
            for (final Race cls : lq.races.getRaces().values()) {
                for (final SkillDefinition s : cls.availableSkills) {
                    strout = " - " + s.getSkillInfo().name + " [" + lq.configLang.statLevelShort + " " + s.getSkillInfo().levelRequired + " | " + lq.configLang.statSp + " " + s.getSkillInfo().skillPoints + "]";
                    sender.sendMessage(strout);
                }
            }
        } else {
            pc.checkSkills();
            // get skills allowed for this player
            final Map<String, Boolean> selected = pc.skillsSelected;
            
            for (final SkillDefinition s : pc.race.availableSkills) {
                strout = " - " + s.getSkillInfo().name + " [" + lq.configLang.statLevelShort + " " + s.getSkillInfo().levelRequired + " | " + lq.configLang.statSp + " " + s.getSkillInfo().skillPoints + "]";
                if (selected.containsKey(s.getSkillInfo().name)) {
                    strout += " *";
                }
                sender.sendMessage(strout);
            }
            for (final SkillDefinition s : pc.mainClass.availableSkills) {
                strout = " - " + s.getSkillInfo().name + " [" + lq.configLang.statLevelShort + " " + s.getSkillInfo().levelRequired + " | " + lq.configLang.statSp + " " + s.getSkillInfo().skillPoints + "]";
                if (selected.containsKey(s.getSkillInfo().name)) {
                    strout += " *";
                }
                sender.sendMessage(strout);
            }
            if (pc.subClass != null) {
                for (final SkillDefinition s : pc.subClass.availableSkills) {
                    strout = " - " + s.getSkillInfo().name + " [" + lq.configLang.statLevelShort + " " + s.getSkillInfo().levelRequired + " | " + lq.configLang.statSp + " " + s.getSkillInfo().skillPoints + "]";
                    if (selected.containsKey(s.getSkillInfo().name)) {
                        strout += " *";
                    }
                    sender.sendMessage(strout);
                }
            }
        }
    }
}
