package me.sablednah.legendquest.utils.plugins;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class MagicItemsQuarentine {

	public static List<ItemStack> getEquipment(Entity e) {
		if (hasMI()) {
			return MagicItemsPlugin.getEquipment(e);
		}
		return null;
	}

	public static int getAttackMod(List<ItemStack> items) {
		if (hasMI()) {
			return MagicItemsPlugin.getAttackMod(items);
		}
		return 0;
	}

	public static int getDefenceMod(List<ItemStack> items) {
		if (hasMI()) {
			return MagicItemsPlugin.getDefenceMod(items);
		}
		return 0;
	}

	public static int getDodgeMod(List<ItemStack> items) {
		if (hasMI()) {
			return MagicItemsPlugin.getDodgeMod(items);
		}
		return 0;
	}

	public static int getStatMod(List<ItemStack> items, String attr) {
		if (hasMI()) {
			return MagicItemsPlugin.getStatMod(items,attr);
		}
		return 0;
	}

	private static boolean hasMI() {
		return Bukkit.getPluginManager().isPluginEnabled("LegendQuestMagicItems");
	}
}
