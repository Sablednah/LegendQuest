package me.sablednah.legendquest.experience;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SetExp {
	private static boolean version1_8() {
		String ver = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		if (ver.startsWith("1.7")) {
			return false;
		}
		if (ver.startsWith("1.6")) {
			return false;
		}
		return true;
	}

	public static int getExpAtLevel(int lvl) {
		if (version1_8()) {
			if (lvl <= 15) {
				return (2 * lvl) + 7;
			}
			if ((lvl >= 16) && (lvl <= 30)) {
				return (5 * lvl) - 38;
			}
			return (9 * lvl) - 158;
		} else {
			if (lvl > 15) {
				return 17 + (lvl - 15) * 3;
			}
			if (lvl > 29) {
				return 62 + (lvl - 30) * 7;
			}
			return 17;
		}
	}

	public static int getExpToLevel(int lvl) {
		int curLvl = 0;
		int xp = 0;

		while (curLvl < lvl) {
			xp += getExpAtLevel(curLvl);
			curLvl++;
		}
		if (xp < 0) {
			xp = Integer.MAX_VALUE;
		}
		return xp;
	}

	public static int getExpUntilNextLevel(Player player) {
		int exp = (int) Math.round(getXpAtLevel(player) * player.getExp());
		int nextLevel = player.getLevel();
		return getExpAtLevel(nextLevel) - exp;
	}

	public static int getLevelOfXpAmount(int xp) {
		if (xp < 0) {
			throw new IllegalArgumentException("Negative experience!");
		}
		int lvl = 0;
		int amount = xp;
		while (amount > 0) {
			int expToLevel = getExpAtLevel(lvl);
			amount -= expToLevel;
			if (amount > 0) {
				lvl++;
			}
		}
		return lvl;
	}

	public static int getTotalExperience(Player player) {
		int xp = Math.round(getXpAtLevel(player) * player.getExp());
		int curLvl = player.getLevel();
		while (curLvl > 0) {
			curLvl--;
			xp += getExpAtLevel(curLvl);
		}
		if (xp < 0) {
			xp = Integer.MAX_VALUE;
		}
		return xp;
	}

	public static void setTotalExperience(Player player, int xp) {
		if (xp < 0) {
			throw new IllegalArgumentException("Experience is negative!");
		}
		player.setExp(0.0F);
		player.setLevel(0);
		player.setTotalExperience(0);

		int total = xp;
		while (total > 0) {
			int expToLevel = getXpAtLevel(player);
			total -= expToLevel;
			if (total >= 0) {
				player.giveExp(expToLevel);
			} else {
				total += expToLevel;
				player.giveExp(total);
				total = 0;
			}
		}
	}

	private static int getXpAtLevel(Player p) {
		return getExpAtLevel(p.getLevel());
	}

}
