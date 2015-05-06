package me.sablednah.legendquest.utils.plugins;

import java.util.List;

import me.sablednah.legendquest.magicitems.Main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class MagicItemsPlugin {

	public static List<ItemStack> getEquipment(Entity e) {
		return getMI().mechanics.getEquipment(e);
	}

	public static int getAttackMod(List<ItemStack> items) {
		return getMI().mechanics.getAttackMod(items);
	}

	public static int getDefenceMod(List<ItemStack> items) {
		return getMI().mechanics.getDefenceMod(items);
	}

	public static int getDodgeMod(List<ItemStack> items) {
		return getMI().mechanics.getDodgeMod(items);
	}

	public static int getStatMod(List<ItemStack> items, String attr) {
		return getMI().mechanics.getStatMod(items,attr);
	}

	private static Main getMI() {
		return (Main)Bukkit.getPluginManager().getPlugin("LegendQuestMagicItems");
	}
}
