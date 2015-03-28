package me.sablednah.legendquest.utils.plugins;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import me.clip.deluxechat.placeholders.DeluxePlaceholderHook;
import me.clip.deluxechat.placeholders.PlaceholderHandler;
import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.mechanics.Attribute;
import me.sablednah.legendquest.party.Party;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;

public class DeluxeChatClass implements Listener {
	public Main	lq;

	public DeluxeChatClass(Main lq) {
		this.lq = lq;

		/**
		 * When a placeholder hook is registered, DeluxeChat will request a value based on the placeholder identifier
		 * for the specific plugin
		 * 
		 * placeholders are automatically registered in a specific format.
		 * 
		 * format: %<pluginname>_<Itentifier>% the plugin name will always be forced to lower case example:
		 * %deluxetags_tag%
		 * 
		 * When the onPlaceholderRequest is called for your plugin, you can simply return the value specific to the
		 * placeholder identifier requiring a value for the player.
		 */
		boolean registered = PlaceholderHandler.registerPlaceholderHook((Plugin) lq, new DeluxePlaceholderHook() {
			@Override
			public String onPlaceholderRequest(Player player, String id) {
				Main lq = (Main) Bukkit.getPluginManager().getPlugin("LegendQuest");
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
						return (player.getLevel() > lq.configMain.max_level) ? "Mastered" : "";
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
//				} else if (id.equalsIgnoreCase("sheet")) {
//					if (pc != null) {
//						if (pc.subClass != null)
//							return pc.subClass.name;
//					}
//					return "";
				}
				return "&cThis is not the tag you where looking for!";
			}
		});

		if (registered) {
			lq.log("DeluxeChat placeholder handler registered!");
		}

	}

}
