package me.sablednah.legendquest.utils;

// SetExp functions from Essestials project.
//  Additions by SableDnah

import org.bukkit.entity.Player;

public class SetExp {

    public static int getExpAtLevel(final int level) {
        if (level > 29) {
            return 62 + (level - 30) * 7;
        }
        if (level > 15) {
            return 17 + (level - 15) * 3;
        }
        return 17;
    }

    private static int getExpAtLevel(final Player player) {
        return getExpAtLevel(player.getLevel());
    }

    public static int getExpToLevel(final int level) {
        int currentLevel = 0;
        int exp = 0;

        while (currentLevel < level) {
            exp += getExpAtLevel(currentLevel);
            currentLevel++;
        }
        if (exp < 0) {
            exp = 2147483647;
        }
        return exp;
    }

    public static int getExpUntilNextLevel(final Player player) {
        final int exp = Math.round(getExpAtLevel(player) * player.getExp());
        final int nextLevel = player.getLevel();
        return getExpAtLevel(nextLevel) - exp;
    }

    public static int getLevelOfXpAmount(final int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("Experience is negative!");
        }
        int level = 0;
        int amount = exp;
        while (amount > 0) {
            final int expToLevel = getExpAtLevel(level);
            amount -= expToLevel;
            if (amount > 0) {
                level++;
            }
        }
        return level;
    }

    public static int getTotalExperience(final Player player) {
        int exp = Math.round(getExpAtLevel(player) * player.getExp());
        int currentLevel = player.getLevel();

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }
        if (exp < 0) {
            exp = 2147483647;
        }
        return exp;
    }

    public static void setTotalExperience(final Player player, final int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("Experience is negative!");
        }
        player.setExp(0.0F);
        player.setLevel(0);
        player.setTotalExperience(0);

        int amount = exp;
        while (amount > 0) {
            final int expToLevel = getExpAtLevel(player);
            amount -= expToLevel;
            if (amount >= 0) {
                player.giveExp(expToLevel);
            } else {
                amount += expToLevel;
                player.giveExp(amount);
                amount = 0;
            }
        }
    }
}