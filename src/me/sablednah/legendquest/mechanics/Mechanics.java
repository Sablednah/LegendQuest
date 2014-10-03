package me.sablednah.legendquest.mechanics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import me.sablednah.legendquest.events.AbilityCheckEvent;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

public class Mechanics {
	public static int diceRoll() {
		Random rnd = new Random();
		int result = rnd.nextInt(20) + 1;
		// System.out.print("Dice roll: "+result);
		return result;
	}

	public static int diceRoll(int target) {
		Random rnd = new Random();
		int result = (rnd.nextInt(20) + 1) - target;
		return result;
	}

	public static int getPlayersAttributeModifier(PC pc, Attribute attr) {
		int stat = 10;
		if (attr != null && pc != null) {
			stat = pc.getStat(attr);
		} else {
			stat = 10;
		}
		int result = (int) (stat / 2) - 5;
		if (pc != null) {
			Player p = pc.getPlayer();
			if (p != null) {
				if (p.hasMetadata("cursetimeout")) {
					long cursetime = p.getMetadata("cursetimeout").get(0).asLong();
					if (System.currentTimeMillis() > cursetime) {
						p.removeMetadata("cursetimeout", pc.lq);
					} else {
						String att = attr.toString().toLowerCase();

						List<MetadataValue> mods = p.getMetadata(att);
						for (MetadataValue metamod : mods) {
							Integer mod = metamod.asInt();
							System.out.print("Curse: " + att + " - " + mod);
							result += mod;
						}
					}
				}
			}
		}

		AbilityCheckEvent e = new AbilityCheckEvent(pc, attr, result);
		Bukkit.getServer().getPluginManager().callEvent(e);

		result = e.getValue();

		/*
		 * if (Main.debugMode) { HandlerList andlers = e.getHandlers(); for (RegisteredListener andle :
		 * andlers.getRegisteredListeners()) { System.out.print(andle.getListener().toString()); } }
		 */
		return result;
	}

	public static int skillTest(int dif, Attribute attr, PC pc) {
		int roll = diceRoll();
		int attrnum = getPlayersAttributeModifier(pc, attr);
		/*
		 * String as = "null"; if (attr!=null) { as = attr.toString(); } String aname; if (pc!=null){ aname =
		 * pc.charname; } else { aname = "unknown"; } if (Main.debugMode){
		 * System.out.print("Test for '"+aname+"' [atr="+
		 * as+"]: D20 = "+roll+" + "+attrnum+" (mod) - "+dif+" (dif) := "+(roll + attrnum - dif)); }
		 */
		roll = roll + attrnum;
		return roll - dif;
	}

	public static int skillTest(Difficulty dif, Attribute attr, PC pc) {
		return skillTest(dif.getDifficulty(), attr, pc);
	}

	public static boolean skillTestB(int dif, Attribute attr, PC pc) {
		return skillTest(dif, attr, pc) > 0;
	}

	public static boolean skillTestB(Difficulty dif, Attribute attr, PC pc) {
		return skillTestB(dif.getDifficulty(), attr, pc);
	}

	public static boolean opposedTest(PC tester, Difficulty testDif, Attribute testAttr, PC oponent, Difficulty oppDif, Attribute oppAttr) {
		return opposedTest(tester, testDif.getDifficulty(), testAttr, oponent, oppDif.getDifficulty(), oppAttr);
	}

	public static boolean opposedTest(PC tester, int testDif, Attribute testAttr, PC oponent, int oppDif, Attribute oppAttr) {
		int testScore = skillTest(testDif, testAttr, tester);
		int oppScore = skillTest(oppDif, oppAttr, oponent);

		
// System.out.print(testScore + " - op:" +oppScore );
		
		if (testScore == oppScore) {
			int testMod = getPlayersAttributeModifier(tester, testAttr);
			int oppMod = getPlayersAttributeModifier(oponent, oppAttr);
			if (testMod >= oppMod) {
				return true;
			} else {
				return false;
			}
		} else {
			if (testScore > oppScore) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static List<Player> getParty(Player p, int range) {
		List<Player> list = new ArrayList<Player>();
		List<Entity> es = p.getNearbyEntities(range, range, range);
		for (Entity e: es) {
			if (e.getType()==EntityType.PLAYER) {
				if (!PluginUtils.canHurt(p,(Player)e)) {
					 list.add((Player)e);
				}
			}
		}
		return list;
	}
	
	public static boolean isParty(Player p, Player t, int range) {
		if (PluginUtils.canHurt(p,t)) {
			return false; 
		}
		if (p.getLocation().distanceSquared(t.getLocation())<(range*range)) {
			return true;
		}
		return false;
	}
	
}

/*
 * 
 * 
 * str att mod = damage dex att mod = armour con att mod *level = hp wis mod = enchant int mod = repair? ? chr mod =
 * tradecost
 */