package me.sablednah.legendquest.cmds;

import java.text.DecimalFormat;
import java.util.Map;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.experience.SetExp;
import me.sablednah.legendquest.mechanics.Attribute;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdStats extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdStats(final Main p) {
		this.lq = p;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		// get the enum for this command
		final Cmds cmd = Cmds.valueOf("STATS");

		if (!validateCmd(lq, cmd, sender, args)) {
			return true;
		}

		final boolean isPlayer = (sender instanceof Player);
		String targetName = null;
		boolean full = false;

		if (args.length > 0) {
			if (args.length == 1) {
				targetName = args[0];
				if (targetName.equalsIgnoreCase("full")) {
					if (isPlayer) {
						if (!lq.validWorld(((Player) sender).getWorld().getName())) {
							((Player) sender).sendMessage(lq.configLang.invalidWorld);
							return true;
						}
						targetName = sender.getName();
						full = true;
					} else {
						sender.sendMessage(cmd.toString() + ": " + lq.configLang.invalidArgumentsCommand);
						return true;
					}					
				}
			} else {
				full = true;
				if (args[0].equalsIgnoreCase("full")) {
					targetName = args[1];
				} else {
					targetName = args[0];
				}
			}
		} else {
			if (isPlayer) {
				if (!lq.validWorld(((Player) sender).getWorld().getName())) {
					((Player) sender).sendMessage(lq.configLang.invalidWorld);
					return true;
				}
				targetName = sender.getName();
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
			String outputline;
			sender.sendMessage(lq.configLang.playerStats);

			sender.sendMessage(lq.configLang.playerName + ": " + pc.charname + " (" + targetName + ")");
			if (lq.races.getRaces().size() > 1) {
				sender.sendMessage(lq.configLang.statRace + ": " + pc.race.name);
			}

			outputline = lq.configLang.statClass + ": " + pc.mainClass.name + ": " + lq.configLang.statLevelShort + " " + SetExp.getLevelOfXpAmount(pc.currentXP);
			if (full) {
				outputline += " (" + pc.currentXP + ")";
			}
			sender.sendMessage(outputline);

			if (!lq.configMain.disableStats) {
				String mod = "";
				String output = "";
				
				outputline = lq.configLang.statSTR + ": " + pc.getStatStr();
				if (full) {
					if (pc.getAttributeModifier(Attribute.STR) >= 0) {
						mod = "+" + pc.getAttributeModifier(Attribute.STR);
					} else {
						mod = "" + pc.getAttributeModifier(Attribute.STR);
					}
					outputline += " (" + mod + ")";
				}
				if (lq.configMain.verboseStats) { sender.sendMessage(outputline); } else { output += outputline + " "; }

				
				outputline = lq.configLang.statDEX + ": " + pc.getStatDex();
				if (full) {
					if (pc.getAttributeModifier(Attribute.DEX) >= 0) {
						mod = "+" + pc.getAttributeModifier(Attribute.DEX);
					} else {
						mod = "" + pc.getAttributeModifier(Attribute.DEX);
					}
					outputline += " (" + mod + ")";
				}
				if (lq.configMain.verboseStats) { sender.sendMessage(outputline); } else { output += outputline + " "; }

				outputline = lq.configLang.statCON + ": " + pc.getStatCon();
				if (full) {
					if (pc.getAttributeModifier(Attribute.CON) >= 0) {
						mod = "+" + pc.getAttributeModifier(Attribute.CON);
					} else {
						mod = "" + pc.getAttributeModifier(Attribute.CON);
					}
					outputline += " (" + mod + ")";
				}
				if (lq.configMain.verboseStats) { sender.sendMessage(outputline); } else { output += outputline + " "; }

				outputline = lq.configLang.statINT + ": " + pc.getStatInt();
				if (full) {
					if (pc.getAttributeModifier(Attribute.INT) >= 0) {
						mod = "+" + pc.getAttributeModifier(Attribute.INT);
					} else {
						mod = "" + pc.getAttributeModifier(Attribute.INT);
					}
					outputline += " (" + mod + ")";
				}
				if (lq.configMain.verboseStats) { sender.sendMessage(outputline); } else { output += outputline + " "; }

				outputline = lq.configLang.statWIS + ": " + pc.getStatWis();
				if (full) {
					if (pc.getAttributeModifier(Attribute.WIS) >= 0) {
						mod = "+" + pc.getAttributeModifier(Attribute.WIS);
					} else {
						mod = "" + pc.getAttributeModifier(Attribute.WIS);
					}
					outputline += " (" + mod + ")";
				}
				if (lq.configMain.verboseStats) { sender.sendMessage(outputline); } else { output += outputline + " "; }

				outputline = lq.configLang.statCHR + ": " + pc.getStatChr();
				if (full) {
					if (pc.getAttributeModifier(Attribute.CHR) >= 0) {
						mod = "+" + pc.getAttributeModifier(Attribute.CHR);
					} else {
						mod = "" + pc.getAttributeModifier(Attribute.CHR);
					}
					outputline += " (" + mod + ")";
				}
				if (lq.configMain.verboseStats) { sender.sendMessage(outputline); } else { output += outputline + " "; }


				if (!output.isEmpty()) {
					sender.sendMessage(output);
				}
			}

			sender.sendMessage("--------------------");
			if (full) {
				sender.sendMessage(lq.configLang.statKarma + ": " + pc.karmaName() + " (" + pc.karma + ")");
				sender.sendMessage("--------------------");
			}

			DecimalFormat df = new DecimalFormat("#.00");
			sender.sendMessage(Utils.barGraph(pc.getHealth(), pc.maxHP, 20, lq.configLang.statHealth, (" " + df.format(pc.getHealth()) + " / " + df.format(pc.maxHP))));
			if (pc.getMaxMana()>1) {
				sender.sendMessage(Utils.barGraph(pc.mana, pc.getMaxMana(), 20, lq.configLang.statMana, (" " + pc.mana + " / " + pc.getMaxMana())));
			}
			if (pc.getMaxSkillPointsLeft()>1) {
				sender.sendMessage(lq.configLang.skillPoints + ": " + pc.getSkillPointsLeft() + " (" + pc.getSkillPointsSpent() + "/" + pc.getMaxSkillPointsLeft() + ")");
			}
			if (full) {
				sender.sendMessage("--------------------");
				sender.sendMessage(lq.configLang.storedExperience);

				for (final Map.Entry<String, Integer> entry : pc.xpEarnt.entrySet()) {
					sender.sendMessage(entry.getKey().toLowerCase() + ": " + lq.configLang.statLevelShort + " " + SetExp.getLevelOfXpAmount(entry.getValue()) + " (" + lq.configLang.statXP + ": " + entry.getValue() + ")");
				}
			}

			return true;
		} else {
			sender.sendMessage(lq.configLang.characterNotFound + targetName);
			return true;
		}
	}
}
