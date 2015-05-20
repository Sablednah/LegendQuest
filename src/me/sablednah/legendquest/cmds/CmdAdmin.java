package me.sablednah.legendquest.cmds;

import java.util.HashSet;
import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.experience.SetExp;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.races.Race;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAdmin extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdAdmin(Main p) {
		this.lq = p;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// get the enum for this command
		Cmds cmd = Cmds.valueOf("ADMIN");
		if (!validateCmd(lq, cmd, sender, args)) {
			return true;
		}

		// from here on is command specific code.
		Player p = null;
		// PC pc = null;

		if ((sender instanceof Player)) {
			p = (Player) sender;

			// do we need this? Only lets admin "admin" if they are in an LQ world....
			if (!lq.validWorld(p.getWorld().getName())) {
				p.sendMessage(lq.configLang.invalidWorld);
				return true;
			}
			// pc = lq.players.getPC(p);
		}

		if (args[0].equalsIgnoreCase("race")) {
			// ok we're doing something with race
			if (args.length < 3) {
				sender.sendMessage(lq.configLang.invalidArgumentsCommand);
				return true;
			} else {
				// look for a classname
				String playername = args[2];
				Race r = lq.races.getRace(args[1]);
				if (r == null) {
					playername = args[1];
					r = lq.races.getRace(args[2]);
					if (r == null) {
						sender.sendMessage(lq.configLang.raceInvalid + ": " + args[1] + " / " + args[2]);
						return true;
					}
				}

				Player targetPlayer = lq.getServer().getPlayer(playername);

				if (targetPlayer == null || !targetPlayer.isOnline()) {
					sender.sendMessage(lq.configLang.invalidArgumentsCommand + ": " + args[1] + " / " + args[2]);
					return true;
				}
				PC targetPC = lq.players.getPC(targetPlayer);
				/*
				targetPC.race = r;
				targetPC.raceChanged = true;
				lq.players.addPlayer(targetPlayer.getUniqueId(), targetPC);
				lq.players.savePlayer(targetPlayer.getUniqueId());

				targetPC.scheduleHealthCheck();
				targetPC.checkInv();
				targetPC.skillSet = targetPC.getUniqueSkills(true);
				*/
				boolean nopay = true;
				if (args.length > 3) {
					if (args[3].equalsIgnoreCase("charge") || args[3].equalsIgnoreCase("pay")) {
						nopay = false;
					}
				}
				targetPC.changeRace(r, nopay);
				
				sender.sendMessage(lq.configLang.raceChanged + ": " + r.name);
				targetPlayer.sendMessage(lq.configLang.raceChangedAdmin + ": " + r.name);
				return true;

			}
		} else if (args[0].equalsIgnoreCase("class")) {
			// ok we're doing something with class
			if (args.length < 3) {
				sender.sendMessage(lq.configLang.invalidArgumentsCommand);
				return true;
			} else {
				// look for a classname
				boolean main = true;
				if (args.length > 3) {
					// check for "sub" modifier
					if (args[1].equalsIgnoreCase("sub") || args[2].equalsIgnoreCase("sub") || args[3].equalsIgnoreCase("sub")) {
						main = false;
						String[] newArglist = new String[args.length - 1];
						int i = 0;
						for (String arg : args) {
							if (!arg.equalsIgnoreCase("sub")) {
								newArglist[i] = arg;
								i++;
							}
						}
						args = newArglist;
					}
				}

				String playername = args[2];
				ClassType cl = lq.classes.getClass(args[1]);
				if (cl == null) {
					playername = args[1];
					cl = lq.classes.getClass(args[2]);
					if (cl == null) {
						sender.sendMessage(lq.configLang.classInvalid + ": " + args[1] + " / " + args[2]);
						return true;
					}
				}

				
				if (Main.debugMode) {
					System.out.print("Args.length: " + args.length);
					for (int zz=0; zz<args.length; zz++) {
						System.out.print("Args: " + zz + " | " + args[zz] + " | ");						
					}
				}
				
				boolean nopay = true;
				if (main) {
					if (args.length > 3) {
						if (args[3].equalsIgnoreCase("charge") || args[3].equalsIgnoreCase("pay")) {
							nopay = false;
						}
					}
				} else {
					if (args.length > 4) {
						if (args[4].equalsIgnoreCase("charge") || args[4].equalsIgnoreCase("pay")) {
							nopay = false;
						}
					}
				}
				
				Player targetPlayer = lq.getServer().getPlayer(playername);

				if (targetPlayer == null || !targetPlayer.isOnline()) {
					sender.sendMessage(lq.configLang.invalidArgumentsCommand + ": " + args[1] + " / " + args[2]);
					return true;
				}
				PC targetPC = lq.players.getPC(targetPlayer);

				targetPC.changeClass(cl, !main, nopay);

				sender.sendMessage(lq.configLang.classChanged + ": " + cl.name);
				targetPlayer.sendMessage(lq.configLang.classChangedAdmin + ": " + cl.name);
				return true;
			}
		} else if (args[0].equalsIgnoreCase("xp")) {
			if (args.length < 3) {
				sender.sendMessage(lq.configLang.invalidArgumentsCommand);
				return true;
			} else {
				// look for xp+name
				int xp = 0;
				String playername;
				if (NumberUtils.isNumber(args[1])) {
					xp = Integer.parseInt(args[1]);
					playername = args[2];
				} else {
					if (!NumberUtils.isNumber(args[2])) {
						sender.sendMessage(lq.configLang.invalidArgumentsCommand + ": " + args[1] + " " + args[2]);
						return true;
					}
					xp = Integer.parseInt(args[2]);
					playername = args[1];
				}

				Player targetPlayer = lq.getServer().getPlayer(playername);

				if (targetPlayer == null || !targetPlayer.isOnline()) {
					sender.sendMessage(lq.configLang.invalidArgumentsCommand + ": " + args[1] + " / " + args[2]);
					return true;
				}
				
				int realxp = (int)(xp / (lq.configMain.scaleXP/100.0D));
				
				PC targetPC = lq.players.getPC(targetPlayer);
				targetPC.giveXP(realxp);
				sender.sendMessage(lq.configLang.xpChangeAdmin + targetPlayer.getDisplayName() + " / " + xp);
				return true;

			}
		} else if (args[0].equalsIgnoreCase("level") || args[0].equalsIgnoreCase("lvl")) {
			if (args.length < 3) {
				sender.sendMessage(lq.configLang.invalidArgumentsCommand);
				return true;
			} else {
				// look for xp+name
				int level = 0;
				String playername;
				if (NumberUtils.isNumber(args[1])) {
					level = Integer.parseInt(args[1]);
					playername = args[2];
				} else {
					if (!NumberUtils.isNumber(args[2])) {
						sender.sendMessage(lq.configLang.invalidArgumentsCommand + ": " + args[1] + " " + args[2]);
						return true;
					}
					level = Integer.parseInt(args[2]);
					playername = args[1];
				}

				Player targetPlayer = lq.getServer().getPlayer(playername);

				if (targetPlayer == null || !targetPlayer.isOnline()) {
					sender.sendMessage(lq.configLang.invalidArgumentsCommand + ": " + args[1] + " / " + args[2]);
					return true;
				}
								
				PC targetPC = lq.players.getPC(targetPlayer);
				SetExp.setTotalExperience(targetPlayer, SetExp.getExpToLevel(level));
				targetPC.scheduleXPSave();
				sender.sendMessage(lq.configLang.xpChangeAdmin + targetPlayer.getDisplayName() + " / " + SetExp.getExpToLevel(level) + " (level: "+level+")");
				return true;

			}
		} else if (args[0].equalsIgnoreCase("karma")) {
			if (args.length < 3) {
				sender.sendMessage(lq.configLang.invalidArgumentsCommand);
				return true;
			} else {
				// look for a classname
				int xp = 0;
				String playername;
				if (NumberUtils.isNumber(args[1])) {
					xp = Integer.parseInt(args[1]);
					playername = args[2];
				} else {
					if (!NumberUtils.isNumber(args[2])) {
						sender.sendMessage(lq.configLang.invalidArgumentsCommand + ": " + args[1] + " " + args[2]);
						return true;
					}
					xp = Integer.parseInt(args[2]);
					playername = args[1];
				}

				Player targetPlayer = lq.getServer().getPlayer(playername);

				if (targetPlayer == null || !targetPlayer.isOnline()) {
					sender.sendMessage(lq.configLang.invalidArgumentsCommand + ": " + args[1] + " / " + args[2]);
					return true;
				}
				
				PC targetPC = lq.players.getPC(targetPlayer);
				long realkarma = targetPC.karma;
				realkarma = realkarma+xp;
				targetPC.karma = realkarma; 				
				sender.sendMessage(targetPlayer.getDisplayName() + " karma now " + realkarma);
				return true;

			}
		} else if (args[0].equalsIgnoreCase("effect")) {
			if (args.length < 2) {
				sender.sendMessage(lq.configLang.invalidArgumentsCommand);
				return true;
			} else {
				Effects e = null;
				try {
					e = Effects.valueOf(args[1].toUpperCase());
					if (e == null) {
						sender.sendMessage(lq.configLang.invalidArgumentsCommand);
						return true;
					}
				} catch (IllegalArgumentException exept) {
					sender.sendMessage(lq.configLang.invalidArgumentsCommand);
					return true;
				}
				if (args.length < 6) { // no co-ords needs player target
					if (p != null) {
						int duration = 5000;
						if (args.length > 2) {
							duration = Integer.parseInt(args[2])*1000;
						}
						int radius = 5;
						if (args.length > 3) {
							duration = Integer.parseInt(args[3]);
						}
						Location l = p.getTargetBlock((HashSet<Byte>) null, 200).getLocation();
						lq.effectManager.addPendingProcess(new EffectProcess(e, duration, OwnerType.LOCATATION, l,radius));
						return true;

					} else {
						sender.sendMessage(lq.configLang.invalidArgumentsCommand);
						return true;
					}
				} else {
					int duration = 5000;
					if (args.length > 6) {
						duration = Integer.parseInt(args[6])*1000;
					}
					int radius = 5;
					if (args.length > 7) {
						duration = Integer.parseInt(args[7]);
					}
					int x = 0, y = 0, z = 0;
					String world = args[2];
					//if (NumberUtils.isNumber(args[3])) {
						x = Integer.parseInt(args[3]);
					//}
					//if (NumberUtils.isNumber(args[4])) {
						y = Integer.parseInt(args[4]);
					//}
					//if (NumberUtils.isNumber(args[5])) {
						z = Integer.parseInt(args[5]);
					//}
					World w = lq.getServer().getWorld(world);
					if (w == null) {
						sender.sendMessage(lq.configLang.invalidArgumentsCommand);
						return true;
					}
					Location l = new Location(w, x, y, z);
//					System.out.print(l);
					EffectProcess ep = new EffectProcess(e, duration, OwnerType.LOCATATION, l,radius);
//					System.out.print(ep);
					lq.effectManager.addPendingProcess(ep);
					return true;
				}
			}
		} else if (args[0].equalsIgnoreCase("reset")) {
			if (args.length < 2) {
				sender.sendMessage(lq.configLang.invalidArgumentsCommand);
				return true;
			} else {
				String playername = args[1];
				Player targetPlayer = lq.getServer().getPlayer(playername);

				if (targetPlayer == null || !targetPlayer.isOnline()) {
					sender.sendMessage(lq.configLang.invalidArgumentsCommand + ": " + args[1] );
					return true;
				}
				PC targetPC = lq.players.getPC(targetPlayer);
				targetPC.reset();
				sender.sendMessage(targetPlayer.getDisplayName() + " reset.");
				return true;

			}
		}
		return false;
	}
}
