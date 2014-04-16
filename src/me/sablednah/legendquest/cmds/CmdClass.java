package me.sablednah.legendquest.cmds;

import java.util.List;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.SetExp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdClass extends CommandTemplate implements CommandExecutor {

    public Main lq;

    public CmdClass(final Main p) {
        this.lq = p;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        // get the enum for this command
        final Cmds cmd = Cmds.valueOf("CLASS");
        if (!validateCmd(lq, cmd, sender, args)) {
            return true;
        }

        // from here on is command specific code.

        // send console the list list
        if (!(sender instanceof Player)) {
            sendClassList(sender, null);
            return true;
        }

        // only players left here
        final Player p = (Player) sender;
        final PC pc = lq.players.getPC(p);

        if (args.length < 1) { // why am i worried about negative argument length ? le-sigh
            // ok - just list the players class names here.
            if (pc.subClass == null) {
                sender.sendMessage(lq.configLang.youAreCurrently + ": " + pc.mainClass.name);
            } else {
                sender.sendMessage(lq.configLang.youAreCurrently + ": " + pc.mainClass.name + " (" + pc.subClass.name + ")");
            }
            return true;
        } else {
            String className = args[0].toLowerCase();
            if (className.equalsIgnoreCase("list")) {
                sendClassList(sender, pc);
                return true;
            } else {

                boolean sub = false;

                if (className.equalsIgnoreCase("sub") || className.equalsIgnoreCase("subclass")) {
                    if (args.length < 2) {
                        sender.sendMessage(lq.configLang.invalidArgumentsCommand);
                    } else {
                        className = args[1].toLowerCase();
                        sub = true;
                    }
                }

                final boolean confirm = (args[args.length - 1].equalsIgnoreCase("confirm"));

                // check classname is valid
                final ClassType cl = lq.classes.getClass(className);
                if (cl == null) {
                    sender.sendMessage(lq.configLang.classScanInvalid + ": " + className);
                    return true;
                } else {
                    // make sure they have a race...
                    if (!pc.raceChanged) {
                        sender.sendMessage(lq.configLang.classSelectRaceFirst);
                        return true;
                    } else {
                        final List<String> validClasses = lq.classes.getClasses(pc.race.name, p);

                        if (!validClasses.contains(className)) {
                            sender.sendMessage(lq.configLang.classNotAllowed);
                            return true;
                        }

                        final int xpNow = SetExp.getTotalExperience(p);

                        // check for confirmation
                        boolean valid = false;

                        if (xpNow > Main.MAX_XP) {
                            valid = true;
                        }

                        if (confirm) {
                            valid = true;
                        }
                        if (p.getLevel() < 2) {
                            valid = true;
                        }
                        if (pc.mainClass == lq.classes.defaultClass) {
                            valid = true;
                        }
                        if (sub && pc.subClass == null) {
                            valid = true;
                        }
                        if (!sub && pc.mainClass == lq.classes.defaultClass) {
                            valid = true;
                        }

                        if (!valid) {
                            sender.sendMessage(lq.configLang.classChangeWarnXpLoss);
                            sender.sendMessage(lq.configLang.classConfirm);
                            return true;
                        }

                        // only rest XP if they have some worth bothering AND they are changing class - not setting
                        // non-default

                        if (xpNow > Main.MAX_XP) {
                            valid = true;
                        }

                        int newxp = 0;
                        if (p.getLevel() > 1 && xpNow < Main.MAX_XP) {
                            lq.debug.fine("Level is: " + p.getLevel());
                            if ((!sub && pc.mainClass != lq.classes.defaultClass) || (sub && pc.subClass != null)) {
                                lq.debug.fine("resetting " + p.getName() + " XP: " + p.getTotalExperience() + " - "
                                        + ((int) (p.getTotalExperience() * (lq.configMain.percentXpKeepClassChange / 100))));

                                // reset XP
                                newxp = (int) (xpNow * (lq.configMain.percentXpKeepClassChange / 100));
                                pc.setXP(newxp);
                                lq.players.savePlayer(pc);

                            }
                        }

                        String oldClassname;
                        if (sub) {
                            oldClassname = pc.subClass.name.toLowerCase();
                            ;
                            pc.subClass = cl;
                        } else {
                            oldClassname = pc.mainClass.name.toLowerCase();
                            ;
                            pc.mainClass = cl;
                        }
                        int newclassxp = 0;
                        if (pc.xpEarnt.containsKey(cl.name.toLowerCase())) {
                            newclassxp = pc.xpEarnt.get(cl.name.toLowerCase());
                        } else {
                            newclassxp = newxp;
                        }

                        // if mastered class - save this xp and check if target class is mastered.
                        if (xpNow > Main.MAX_XP) {
                            pc.xpEarnt.put(oldClassname, xpNow);

                            if (newclassxp > Main.MAX_XP) {
                                pc.setXP(newclassxp);
                            } else {
                                pc.setXP(0);
                            }
                        } else {
                            // old class was not masteted - xp loss if any was done above.
                            pc.setXP(newclassxp);
                        }

                        lq.players.addPlayer(p.getUniqueId(), pc);
                        lq.players.savePlayer(pc);
                        pc.scheduleHealthCheck();
                        lq.players.scheduleUpdate(p.getUniqueId());
                        pc.checkInv();
                        sender.sendMessage(lq.configLang.classChanged + ": " + className);
                        lq.debug.fine(lq.configLang.classChanged + ": " + className + " - " + p.getName());
                        return true;
                    }
                }
            }
        }
    }

    private void sendClassList(final CommandSender sender, final PC pc) {
        sender.sendMessage(lq.configLang.classList);
        String strout;

        if (pc == null || !(sender instanceof Player)) {
            // send a full list
            for (final ClassType cls : lq.classes.getClassTypes().values()) {
                strout = " - " + cls.name;
                if (cls.defaultClass) {
                    strout += " *";
                }
                sender.sendMessage(strout);
            }
        } else {
            // get classes allowed for this race
            final List<String> classList = lq.classes.getClasses(pc.race.name, (Player) sender);
            if (classList != null) {
                for (final String cls : classList) {
                    if (cls.equalsIgnoreCase(pc.mainClass.name)) {
                        strout = " > ";
                    } else if (pc.subClass != null && cls.equalsIgnoreCase(pc.subClass.name)) {
                        strout = " » ";
                    } else {
                        strout = " - ";
                    }

                    strout += " - " + cls.substring(0, 1).toUpperCase() + cls.substring(1);

                    if (cls.equalsIgnoreCase(lq.classes.defaultClass.name)) {
                        strout += " *";
                    }

                    sender.sendMessage(strout);
                }
            }
        }
    }
}
