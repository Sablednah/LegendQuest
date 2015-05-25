package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.experience.ExperienceSource;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AbilityControlEvents implements Listener {

	public Main	lq;

	public AbilityControlEvents(final Main p) {
		this.lq = p;
	}

/*
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onCraft(CraftItemEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getWhoClicked();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		PC pc = lq.players.getPC(p);
		Result r = event.getResult();
		if (r != null) {
			if (!pc.canCraft()) {
				event.setResult(Result.DENY);
				p.sendMessage(lq.configLang.cantCraft);
			}
		}
	}
*/
	
/*
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void Control(PlayerInteractEvent event) {
		Player p = event.getPlayer();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		PC pc = lq.players.getPC(p);
		Block tb = event.getClickedBlock();

		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (tb != null)) {
			if (tb.getType() == Material.BREWING_STAND) {
				if (!pc.canBrew()) {
					event.setCancelled(true);
					p.sendMessage(lq.configLang.cantBrew);
				}
			}
			if (tb.getType() == Material.ENCHANTMENT_TABLE) {
				if (!pc.canEnchant()) {
					event.setCancelled(true);
					p.sendMessage(lq.configLang.cantEnchant);
				}
			}
			if (tb.getType() == Material.FURNACE) {
				if (!pc.canSmelt()) {
					event.setCancelled(true);
					p.sendMessage(lq.configLang.cantSmelt);
				}
			}
			if (tb.getType() == Material.ANVIL) {
				if (!pc.canRepair()) {
					event.setCancelled(true);
					p.sendMessage(lq.configLang.cantRepair);
				}
			}
		}
	}
*/
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void furnaceXP(FurnaceExtractEvent event) {
		Player p = event.getPlayer();
		if (p != null) {
			PC pc = lq.players.getPC(p);
			double mod = pc.getXPMod(ExperienceSource.SMELT);
			if (mod <= -100.0D) {
				// no experience
				event.setExpToDrop(0);
			} else {
				int xp = event.getExpToDrop();
				xp = (int) (xp * ((100.0D + mod) / 100.0D));
				event.setExpToDrop(xp);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void blockXP(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (p != null) {
			PC pc = lq.players.getPC(p);
			double mod = pc.getXPMod(ExperienceSource.MINE);
			if (mod <= -100.0D) {
				// no experience
				event.setExpToDrop(0);
			} else {
				int xp = event.getExpToDrop();
				xp = (int) (xp * ((100.0D + mod) / 100.0D));
				event.setExpToDrop(xp);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTameEntity(EntityTameEvent event) {
		Player p = (Player) event.getOwner();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		PC pc = lq.players.getPC(p);

		if (Main.debugMode) {
			System.out.print("Tame attempt - tame event - " + p.getName());
		}
		EntityType et = event.getEntityType();
		if (et != null) {
			if (!pc.canTame(et)) {
				p.sendMessage(lq.configLang.cantTame + " (" + et.name() + ")");
				event.setCancelled(true);
				return;
			}
		} /* else {
			if (!pc.canTame()) {
				p.sendMessage(lq.configLang.cantTame);
				event.setCancelled(true);
				return;
			}			
		} */
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onCraftItem(final CraftItemEvent e) {
		// ItemStack item = e.getCurrentItem();
		HumanEntity he = e.getWhoClicked();
		if (he instanceof Player) {
			Player p = (Player) he;
			if (!lq.validWorld(p.getWorld().getName())) {
				return;
			}

			PC pc = lq.players.getPC(p);
			ItemStack r = e.getRecipe().getResult();

			if (r != null) {
				final Material itemUsed = r.getType();
				if( itemUsed != Material.AIR) {
System.out.print("cheking craft for "+itemUsed.name());
					if (!pc.canCraft(itemUsed)) {
						p.sendMessage(lq.configLang.cantCraft + " (" + itemUsed.name() + ")");
						e.setCancelled(true);
						return;
					}
				}
			} /*  else {
				if (!pc.canCraft()) {
					p.sendMessage(lq.configLang.cantCraft);
					e.setCancelled(true);
					return;
				}
			} */
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBrew(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getType() != InventoryType.BREWING) {
			return;
		}
		if (!(e.getWhoClicked() instanceof Player)) {
			return; // not a player somehow
		}
		Player p = (Player) e.getWhoClicked();
		ItemStack cursor = e.getCursor();

		PC pc = lq.players.getPC(p);
		
		int slot = e.getRawSlot();

//		System.out.print("IMC: can brew - " + cursor.getType().toString() + " (Slot:" + e.getSlot() + ":"+slot+")");
//		System.out.print("IMC: can brew - " + e.getSlotType().toString()+")");
		
		Material clickedItem = null;
		
		if (e.getSlotType()==SlotType.FUEL && slot == 3) {
			clickedItem = cursor.getType();
	//		System.out.print("IMC: Slot 0 Click - " + clickedItem.toString());
		} else if ((e.getSlotType() == SlotType.QUICKBAR || e.getSlotType() == SlotType.CONTAINER) && e.isShiftClick()) {
			clickedItem = e.getCurrentItem().getType();
	//		System.out.print("IMC: shift Click - " + clickedItem.toString());
		}

		if (clickedItem != null && clickedItem != Material.AIR) {
			if (!pc.canBrew(clickedItem)) {
				p.sendMessage(lq.configLang.cantBrew + " (" + clickedItem.name() + ")");
				e.setCancelled(true);
				return;
			}
		} /*  else {
			if (!pc.canBrew()) {
				p.sendMessage(lq.configLang.cantBrew);
				e.setCancelled(true);
				return;
			}
		} */
	}

	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSmelt(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getType() != InventoryType.FURNACE) {
			return;
		}
		if (!(e.getWhoClicked() instanceof Player)) {
			return; // not a player somehow
		}
		Player p = (Player) e.getWhoClicked();
		ItemStack cursor = e.getCursor();

		PC pc = lq.players.getPC(p);
		
		int slot = e.getRawSlot();
if(Main.debugMode) {
		System.out.print("IMC: can smelt - " + cursor.getType().toString() + " (Slot: " + e.getSlot() + " | rawslot: "+slot+")");
		System.out.print("IMC: can smelt - slot type: " + e.getSlotType().toString() );
}
		Material clickedItem = null;
		if (e.getSlotType()==SlotType.CRAFTING && slot == 0) {
			clickedItem = cursor.getType();
		} else if ((e.getSlotType() == SlotType.QUICKBAR || e.getSlotType() == SlotType.CONTAINER) && e.isShiftClick()) {
			clickedItem = e.getCurrentItem().getType();
		}
		if (clickedItem != null && clickedItem != Material.AIR) {
			if (!pc.canSmelt(clickedItem)) {
				p.sendMessage(lq.configLang.cantSmelt + " (" + clickedItem.name() + ")");
				e.setCancelled(true);
				return;
			}
		}/* else {
			if (!pc.canSmelt()) {
				p.sendMessage(lq.configLang.cantSmelt);
				e.setCancelled(true);
				return;
			}
		} */
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onRepair(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getType() != InventoryType.ANVIL) {
			return;
		}
		if (!(e.getWhoClicked() instanceof Player)) {
			return; // not a player somehow
		}
		Player p = (Player) e.getWhoClicked();
		ItemStack cursor = e.getCursor();

		PC pc = lq.players.getPC(p);
		
//		int slot = e.getRawSlot();
//		System.out.print("IMC: can Repair - " + cursor.getType().toString() + " (Slot: " + e.getSlot() + " | rawslot: "+slot+")");
//		System.out.print("IMC: can Repair - slot type: " + e.getSlotType().toString() );

		Material clickedItem = null;
		if (e.getSlotType()==SlotType.CRAFTING) {
			clickedItem = cursor.getType();
		} else if ((e.getSlotType() == SlotType.QUICKBAR || e.getSlotType() == SlotType.CONTAINER) && e.isShiftClick()) {
			clickedItem = e.getCurrentItem().getType();
		}
		if (clickedItem != null && clickedItem != Material.AIR) {
			if (!pc.canRepair(clickedItem)) {
				p.sendMessage(lq.configLang.cantRepair + " (" + clickedItem.name() + ")");
				e.setCancelled(true);
				return;
			}
		}/* else {
			if (!pc.canRepair()) {
				p.sendMessage(lq.configLang.cantRepair);
				e.setCancelled(true);
				return;
			}
		}*/
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEnchant(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getType() != InventoryType.ENCHANTING) {
			return;
		}
		if (!(e.getWhoClicked() instanceof Player)) {
			return; // not a player somehow
		}
		Player p = (Player) e.getWhoClicked();
		ItemStack cursor = e.getCursor();

		PC pc = lq.players.getPC(p);
		
//		int slot = e.getRawSlot();
//		System.out.print("IMC: can Enchant - " + cursor.getType().toString() + " (Slot: " + e.getSlot() + " | rawslot: "+slot+")");
//		System.out.print("IMC: can Enchant - slot type: " + e.getSlotType().toString() );

		Material clickedItem = null;
		if (e.getSlotType()==SlotType.CRAFTING) {
			clickedItem = cursor.getType();
		} else if ((e.getSlotType() == SlotType.QUICKBAR || e.getSlotType() == SlotType.CONTAINER) && e.isShiftClick()) {
			clickedItem = e.getCurrentItem().getType();
		}
		if (clickedItem != null && clickedItem != Material.AIR && clickedItem != Material.INK_SACK) {
			if (!pc.canEnchant(clickedItem)) {
				p.sendMessage(lq.configLang.cantEnchant + " (" + clickedItem.name() + ")");
				e.setCancelled(true);
				return;
			}
		}/* else {
			if (!pc.canEnchant()) {
				p.sendMessage(lq.configLang.cantEnchant);
				e.setCancelled(true);
				return;
			}
		} */
	}
	
}
