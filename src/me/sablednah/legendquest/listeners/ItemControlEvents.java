package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemControlEvents implements Listener {

	public Main	lq;

	public ItemControlEvents(final Main p) {
		this.lq = p;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAttack(final EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		if (!(e.getDamager() instanceof Player)) {
			return;
		}

		final Player p = (Player) e.getDamager();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		final PC pc = lq.players.getPC(p);
		final Material itemUsed = p.getItemInHand().getType();

		if (itemUsed != null) {
			if (lq.configData.dataSets.get("weapons").contains(itemUsed) || 
					lq.configData.dataSets.get("tools").contains(itemUsed) || 
					lq.configData.dataSets.get("utility").contains(itemUsed)) {
				if (!pc.allowedWeapon(itemUsed) && !pc.allowedTool(itemUsed)) {
					p.sendMessage(lq.configLang.cantUseWeapon);
					event.setCancelled(true);
				}
			}
		}
	}

	// prevent tool use
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event) {
		final Player p = event.getPlayer();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		final PC pc = lq.players.getPC(p);
		final Material itemUsed = p.getItemInHand().getType();
		if (itemUsed != null) {
			if (lq.configData.dataSets.get("weapons").contains(itemUsed) || 
					lq.configData.dataSets.get("tools").contains(itemUsed) || 
					lq.configData.dataSets.get("utility").contains(itemUsed)) {
				if (!pc.allowedWeapon(itemUsed) && !pc.allowedTool(itemUsed)) {
					p.sendMessage(lq.configLang.cantUseTool);
					event.setCancelled(true);
				}
			}
		}
	}

	// Stop Bow Fire
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBowFire(final EntityShootBowEvent event) {
		// we only want to control players here...
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		final Player p = (Player) event.getEntity();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		final PC pc = lq.players.getPC(p);

		if (!pc.allowedWeapon(Material.BOW)) {
			p.sendMessage(lq.configLang.cantUseWeapon);
			event.setCancelled(true);
		}
	}

	// Stop Egg 'Fire'
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEggFire(final PlayerEggThrowEvent event) {
		final Player p = event.getPlayer();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		final PC pc = lq.players.getPC(p);

		if (!pc.allowedWeapon(Material.EGG)) {
			p.sendMessage(lq.configLang.cantUseWeapon);
			event.getEgg().remove();
		}
	}

	// prevent "use" actions
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteract(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		final PC pc = lq.players.getPC(p);
		final Material itemUsed = p.getItemInHand().getType();
		final Action act = event.getAction();
		if (itemUsed != null) {
			if (Main.debugMode) {
				System.out.print("Using item:" + itemUsed.toString());
			}
			if (act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK) {
				if (Main.debugMode) {
					System.out.print("Right click: " + act.toString());
				}
				if (Main.debugMode) {
					System.out.print("is in dataset weapons: " + lq.configData.dataSets.get("weapons").contains(itemUsed));
				}
				if (Main.debugMode) {
					System.out.print("is in dataset tools: " + lq.configData.dataSets.get("tools").contains(itemUsed));
				}
				if (Main.debugMode) {
					System.out.print("is in dataset utility: " + lq.configData.dataSets.get("utility").contains(itemUsed));
				}
				if (lq.configData.dataSets.get("weapons").contains(itemUsed) || lq.configData.dataSets.get("tools").contains(itemUsed) || lq.configData.dataSets.get("utility").contains(itemUsed)) {
					if (Main.debugMode) {
						System.out.print("is in dataset ");
					}
					if (Main.debugMode) {
						System.out.print("allowed weapon:  " + pc.allowedWeapon(itemUsed));
					}
					if (Main.debugMode) {
						System.out.print("allowed tool:  " + pc.allowedTool(itemUsed));
					}
					if (!pc.allowedWeapon(itemUsed) && !pc.allowedTool(itemUsed)) {
						if (Main.debugMode) {
							System.out.print("tool blocked");
						}
						p.sendMessage(lq.configLang.cantUseTool);
						event.setCancelled(true);
					}
				} else {
					if (Main.debugMode) {
						System.out.print("not in dataset - item allowed");
					}
				}
			}
		}
	}

	// prevent "fish" actions
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onGonnaNeedABiggerBoat(final PlayerFishEvent event) {
		final Player p = event.getPlayer();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		final PC pc = lq.players.getPC(p);
		final Material itemUsed = Material.FISHING_ROD;
		if (itemUsed != null) {
			if (lq.configData.dataSets.get("weapons").contains(itemUsed) || lq.configData.dataSets.get("tools").contains(itemUsed) || lq.configData.dataSets.get("utility").contains(itemUsed)) {
				if (!pc.allowedWeapon(itemUsed) && !pc.allowedTool(itemUsed)) {
					p.sendMessage(lq.configLang.cantUseTool);
					event.setCancelled(true);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInvClick(final InventoryClickEvent event) {
		final Player p = (Player) event.getWhoClicked();

		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		final PC pc = lq.players.getPC(p);
		// detect auto armour equip...
		if (event.isShiftClick()) {
			lq.debug.fine("Shift Click used by " + p.getDisplayName());
			if (Main.debugMode) {
				System.out.print("Shift-click: action = " + event.getAction());
				System.out.print("Shift-click: rawslot = " + event.getRawSlot());
				System.out.print("Shift-click: slot = " + event.getSlot());
				System.out.print("Shift-click: clicktype = " + event.getClick());
				System.out.print("Shift-click: slottype = " + event.getSlotType());
				System.out.print("Shift-click: currentitem = " + event.getCurrentItem());
				System.out.print("Shift-click: cursor = " + event.getCursor());
				System.out.print("Shift-click: inventory type = " + event.getInventory().getType());
			}

			if (event.getInventory().getType().equals(InventoryType.CRAFTING) || event.getInventory().getType().equals(InventoryType.PLAYER)) {
				if (!event.getSlotType().equals(SlotType.ARMOR)) {
					if (lq.configData.dataSets.get("armour").contains(event.getCurrentItem().getType())) {
						boolean allowed2 = pc.allowedArmour(event.getCurrentItem().getType());
						if (!allowed2) {
							p.sendMessage(lq.configLang.cantEquipArmour);
							event.setCancelled(true);
							p.updateInventory();
						}
					}
				}
			}
		}
		
		if (event.getCursor() != null) {
			// player has item on the cursor
			final Material itemID = event.getCursor().getType();
			switch (event.getSlotType()) {
				case ARMOR: // and is trying to equip it
					boolean allowed = pc.allowedArmour(itemID);
					if (!allowed) {
						p.sendMessage(lq.configLang.cantEquipArmour);
						event.setCancelled(true);
						p.updateInventory();
					}
					break;
			}
		}
	}

	// check for armour validity when inventory is closed
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInvClose(InventoryCloseEvent event) {
		// check if this is a real players inventory...
		if (Main.debugMode) {
			System.out.print("checking inventory on close: start");
		}
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		if (Main.debugMode) {
			System.out.print("checking inventory on close: in player pass");
		}
		if (event.getPlayer().getName() == null) {
			return;
		}
		if (Main.debugMode) {
			System.out.print("checking inventory on close: player has name");
		}
		final Player p = (Player) event.getPlayer();

		if (Main.debugMode) {
			System.out.print("checking inventory on close valid world ");
		}
		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}
		if (Main.debugMode) {
			System.out.print("checking inventory on close: getting PC");
		}

		final PC pc = lq.players.getPC(p);
		// pc.scheduleCheckInv();
		if (Main.debugMode) {
			System.out.print("doing check inventory on close");
		}
		pc.checkInv();
	}

	/*
	 * @EventHandler(priority = EventPriority.MONITOR) public void onUse(PlayerInteractEvent event) {
	 * System.out.print(event.getEventName()); System.out.print(event.getAction()); System.out.print(event.getItem());
	 * System.out.print(event.getPlayer()); System.out.print(event.hasItem()); System.out.print(event.isCancelled());
	 * System.out.print(PlayerInteractEvent.getHandlerList().toString()); }
	 */

	@EventHandler()
	public void onArmourUse(PlayerInteractEvent event) {
		if (((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))) {
			if (event.hasItem() && event.getItem() != null) {
				if (!lq.configData.dataSets.get("armour").contains(event.getItem().getType())) {
					return;
				}
				final Player p = (Player) event.getPlayer();
				if (!lq.validWorld(p.getWorld().getName())) {
					return;
				}
				final PC pc = lq.players.getPC(p);
				if (!pc.allowedArmour(event.getItem().getType())) {
					pc.scheduleCheckInv();
					// pc.checkInv();
				}
			}
		}
	}

	// Stop Projectile 'Fire'
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onProjectile(final ProjectileLaunchEvent event) {
		final Projectile bullit = event.getEntity();
		if (!(bullit.getShooter() instanceof Player)) {
			return;
		}
		final Player p = (Player) bullit.getShooter();
		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}
		final PC pc = lq.players.getPC(p);

		switch (bullit.getType()) {
			case ARROW:
				if (!pc.allowedWeapon(Material.BOW)) {
					p.sendMessage(lq.configLang.cantUseWeapon);
					event.setCancelled(true);
				}
				break;
			case EGG:
				if (!pc.allowedWeapon(Material.EGG)) {
					p.sendMessage(lq.configLang.cantUseWeapon);
					event.setCancelled(true);
				}
				break;
			case SNOWBALL:
				if (!pc.allowedWeapon(Material.SNOW_BALL)) {
					p.sendMessage(lq.configLang.cantUseWeapon);
					event.setCancelled(true);
				}
				break;
		}
	}
}
