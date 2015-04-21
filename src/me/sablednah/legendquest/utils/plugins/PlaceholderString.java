package me.sablednah.legendquest.utils.plugins;

import java.text.DecimalFormat;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.mechanics.Attribute;
import me.sablednah.legendquest.party.Party;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.entity.Player;

public class PlaceholderString {

	public static String getSting(Player player, String id, Main lq) {
		PC pc = lq.getPlayers().getPC(player);

		if (id.equalsIgnoreCase("race")) {
			if (pc != null) {
				return pc.race.name;
			}
			return "";
		} else if (id.equalsIgnoreCase("karma")) {
			if (pc != null) {
				return pc.karmaName();
			}
			return "";
		} else if (id.equalsIgnoreCase("class")) {
			if (pc != null) {
				return pc.mainClass.name;
			}
			return "";
		} else if (id.equalsIgnoreCase("exp")) {
			if (player != null) {
				return Integer.toString(player.getTotalExperience());
			}
			return "";
		} else if (id.equalsIgnoreCase("hp")) {
			DecimalFormat df = new DecimalFormat("#.0");
			if (pc != null) {
				return df.format(pc.getHealth());
			} else {
				if (player != null) {
					return df.format(player.getHealth());
				}
			}
			return "";
		} else if (id.equalsIgnoreCase("hpbar")) {
			if (pc != null) {
				return Utils.barGraph(pc.getHealth(), pc.getMaxHealth(), 20, lq.configLang.statHealth, "");
			} else {
				if (player != null) {
					return Utils.barGraph(player.getHealth(), player.getMaxHealth(), 20, lq.configLang.statHealth, "");
				}
			}
			return "";
		} else if (id.equalsIgnoreCase("level")) {
			if (player != null) {
				return Integer.toString(player.getLevel());
			}
			return "";
		} else if (id.equalsIgnoreCase("mana")) {
			DecimalFormat df = new DecimalFormat("#");
			if (pc != null) {
				return df.format(pc.mana);
			}
			return "";
		} else if (id.equalsIgnoreCase("manabar")) {
			if (pc != null) {
				return Utils.barGraph(pc.mana, pc.getMaxMana(), 20, lq.configLang.statMana, "");
			}
			return "";
		} else if (id.equalsIgnoreCase("mastered")) {
			if (player != null) {
				return (pc.hasMastered(pc.mainClass.name)) ? "Mastered" : "";
			}
		} else if (id.equalsIgnoreCase("party")) {
			Party p = lq.partyManager.getParty(player.getUniqueId());
			if (p != null) {
				return p.partyName;
			}

			return "";
		} else if (id.equalsIgnoreCase("subclass")) {
			if (pc != null) {
				if (pc.subClass != null)
					return pc.subClass.name;
			}
			return "";
		} else if (id.equalsIgnoreCase("statline")) {
			String mod = "";
			String outputline = "";
			if (pc != null) {
				outputline = lq.configLang.statSTR + ": " + pc.getStatStr();
				if (pc.getAttributeModifier(Attribute.STR) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.STR);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.STR);
				}
				outputline += " (" + mod + ") ";
				outputline += lq.configLang.statDEX + ": " + pc.getStatDex();

				if (pc.getAttributeModifier(Attribute.DEX) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.DEX);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.DEX);
				}
				outputline += " (" + mod + ") ";

				outputline += lq.configLang.statCON + ": " + pc.getStatCon();

				if (pc.getAttributeModifier(Attribute.CON) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.CON);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.CON);
				}
				outputline += " (" + mod + ") ";

				outputline += lq.configLang.statINT + ": " + pc.getStatInt();

				if (pc.getAttributeModifier(Attribute.INT) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.INT);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.INT);
				}
				outputline += " (" + mod + ") ";

				outputline += lq.configLang.statWIS + ": " + pc.getStatWis();

				if (pc.getAttributeModifier(Attribute.WIS) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.WIS);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.WIS);
				}
				outputline += " (" + mod + ") ";

				outputline += lq.configLang.statCHR + ": " + pc.getStatChr();

				if (pc.getAttributeModifier(Attribute.CHR) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.CHR);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.CHR);
				}
				outputline += " (" + mod + ")";
			}
			return outputline;
		} else if (id.equalsIgnoreCase("shortstatline")) {
			String outputline = "";
			if (pc != null) {
				outputline = lq.configLang.statSTR + ": " + String.format("%1$3s", pc.getStatStr()) + " ";
				outputline += lq.configLang.statDEX + ": " + String.format("%1$3s", pc.getStatDex()) + " ";
				outputline += lq.configLang.statCON + ": " + String.format("%1$3s", pc.getStatCon()) + " ";
				outputline += lq.configLang.statINT + ": " + String.format("%1$3s", pc.getStatInt()) + " ";
				outputline += lq.configLang.statWIS + ": " + String.format("%1$3s", pc.getStatWis()) + " ";
				outputline += lq.configLang.statCHR + ": " + String.format("%1$3s", pc.getStatChr());
			}
			return outputline;
		} else if (id.equalsIgnoreCase("statlineheaders")) {
			String outputline = "";
			if (pc != null) {
				outputline = lq.configLang.statSTR + " ";
				outputline += lq.configLang.statDEX + " ";
				outputline += lq.configLang.statCON + " ";
				outputline += lq.configLang.statINT + " ";
				outputline += lq.configLang.statWIS + " ";
				outputline += lq.configLang.statCHR;
			}
			return outputline;
		} else if (id.equalsIgnoreCase("statlinenumbers")) {
			String outputline = "";
			if (pc != null) {
				outputline = String.format("%1$3s", pc.getStatStr()) + " ";
				outputline += String.format("%1$3s", pc.getStatDex()) + " ";
				outputline += String.format("%1$3s", pc.getStatCon()) + " ";
				outputline += String.format("%1$3s", pc.getStatInt()) + " ";
				outputline += String.format("%1$3s", pc.getStatWis()) + " ";
				outputline += String.format("%1$3s", pc.getStatChr());
			}
			return outputline;
		} else if (id.equalsIgnoreCase("statlinemods")) {
			String mod = "";
			String outputline = "";
			if (pc != null) {
				outputline = "";
				if (pc.getAttributeModifier(Attribute.STR) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.STR);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.STR);
				}
				outputline += String.format("%1$3s", mod)+" ";
				if (pc.getAttributeModifier(Attribute.DEX) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.DEX);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.DEX);
				}
				outputline += String.format("%1$3s", mod)+" ";
				if (pc.getAttributeModifier(Attribute.CON) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.CON);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.CON);
				}
				outputline += String.format("%1$3s", mod)+" ";
				if (pc.getAttributeModifier(Attribute.INT) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.INT);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.INT);
				}
				outputline += String.format("%1$3s", mod)+" ";
				if (pc.getAttributeModifier(Attribute.WIS) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.WIS);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.WIS);
				}
				outputline += String.format("%1$3s", mod)+" ";
				if (pc.getAttributeModifier(Attribute.CHR) >= 0) {
					mod = "+" + pc.getAttributeModifier(Attribute.CHR);
				} else {
					mod = "" + pc.getAttributeModifier(Attribute.CHR);
				}
				outputline += String.format("%1$3s", mod);				
			}
			return outputline;
		} else if (id.equalsIgnoreCase("str")) {
			if (pc != null) {
				return lq.configLang.statSTR;
			}
			return "";
		} else if (id.equalsIgnoreCase("dex")) {
			if (pc != null) {
				return lq.configLang.statDEX;
			}
			return "";
		} else if (id.equalsIgnoreCase("con")) {
			if (pc != null) {
				return lq.configLang.statCON;
			}
			return "";
		} else if (id.equalsIgnoreCase("int")) {
			if (pc != null) {
				return lq.configLang.statINT;
			}
			return "";
		} else if (id.equalsIgnoreCase("wis")) {
			if (pc != null) {
				return lq.configLang.statWIS;
			}
			return "";
		} else if (id.equalsIgnoreCase("chr")) {
			if (pc != null) {
				return lq.configLang.statCHR;
			}
			return "";
		} else if (id.equalsIgnoreCase("strmod")) {
			if (pc != null) {
				return String.valueOf(pc.getAttributeModifier(Attribute.STR));
			}
			return "";
		} else if (id.equalsIgnoreCase("dexmod")) {
			if (pc != null) {
				return String.valueOf(pc.getAttributeModifier(Attribute.DEX));
			}
			return "";
		} else if (id.equalsIgnoreCase("conmod")) {
			if (pc != null) {
				return String.valueOf(pc.getAttributeModifier(Attribute.CON));
			}
			return "";
		} else if (id.equalsIgnoreCase("intmod")) {
			if (pc != null) {
				return String.valueOf(pc.getAttributeModifier(Attribute.INT));
			}
			return "";
		} else if (id.equalsIgnoreCase("wismod")) {
			if (pc != null) {
				return String.valueOf(pc.getAttributeModifier(Attribute.WIS));
			}
			return "";
		} else if (id.equalsIgnoreCase("chrmod")) {
			if (pc != null) {
				return String.valueOf(pc.getAttributeModifier(Attribute.CHR));
			}
			return "";
		}
		return "&cThis is not the tag you where looking for!";
	}
}
