package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

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
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemControlEvents implements Listener {

	public Main	lq;

	public ItemControlEvents(Main p) {
		this.lq = p;
	}

	// check for armour validity when inventory is closed
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInvClose(InventoryCloseEvent event) {
		// check if this is a real players inventory...
		if (event.getPlayer() instanceof Player && event.getPlayer().getName() != null) {
			return;
		}
		Player p = (Player) event.getPlayer();
		PC pc = lq.players.getPC(p);
		pc.checkInv();
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInvClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		PC pc = lq.players.getPC(p);
		// detect auto armour equip...
		if (event.isShiftClick()) {
			lq.debug.fine("Shift Click used in admin armour by " + p.getDisplayName());
		}
		if (event.getCursor() != null) {
			// player has item on the cursor
			int itemID = event.getCursor().getTypeId();
			switch (event.getSlotType()) {
				case ARMOR: // and is trying to equip it
					if (!pc.allowedArmour(itemID)) {
						p.sendMessage(lq.configLang.cantEquipArmour);
						event.setCancelled(true);
						p.updateInventory();
					}
					break;
			}
		}
	}

	// Stop Bow Fire
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBowFire(EntityShootBowEvent event) {
		// we only want to control players here...
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getEntity();
		PC pc = lq.players.getPC(p);

		if (!pc.allowedWeapon(lq.bowID)) {
			p.sendMessage(lq.configLang.cantUseWeapon);
			event.setCancelled(true);
		}
	}

	// Stop Egg 'Fire'
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEggFire(PlayerEggThrowEvent event) {
		Player p = event.getPlayer();
		PC pc = lq.players.getPC(p);

		if (!pc.allowedWeapon(lq.eggID)) {
			p.sendMessage(lq.configLang.cantUseWeapon);
			event.getEgg().remove();
		}
	}

	// Stop Projectile 'Fire'
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onProjectile(ProjectileLaunchEvent event) {
		Projectile bullit = event.getEntity();
		if (!(bullit.getShooter() instanceof Player)) {
			return;
		}
		Player p = (Player) bullit.getShooter();
		PC pc = lq.players.getPC(p);

		switch (bullit.getType()) {
			case ARROW:
				if (!pc.allowedWeapon(lq.bowID)) {
					p.sendMessage(lq.configLang.cantUseWeapon);
					event.setCancelled(true);
				}
				break;
			case EGG:
				if (!pc.allowedWeapon(lq.eggID)) {
					p.sendMessage(lq.configLang.cantUseWeapon);
					event.setCancelled(true);
				}
				break;
			case SNOWBALL:
				if (!pc.allowedWeapon(lq.snowballID)) {
					p.sendMessage(lq.configLang.cantUseWeapon);
					event.setCancelled(true);
				}
				break;
		}
	}

	// prevent "use" actions
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		PC pc = lq.players.getPC(p);
		int itemUsed = p.getItemInHand().getTypeId();
		Action act = event.getAction();
		if (itemUsed > 0) {
			if (act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK) {
				if (lq.configData.dataSets.get("weapons").contains(itemUsed) || lq.configData.dataSets.get("tools").contains(itemUsed)   || lq.configData.dataSets.get("utilities").contains(itemUsed) ) {
					if (!pc.allowedWeapon(itemUsed) && !pc.allowedTool(itemUsed)) {
						p.sendMessage(lq.configLang.cantUseTool);
						event.setCancelled(true);
					}
				}
			}
		}
	}

	// prevent tool use
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		PC pc = lq.players.getPC(p);
		int itemUsed = p.getItemInHand().getTypeId();
		if (itemUsed > 0) {
			if (lq.configData.dataSets.get("weapons").contains(itemUsed) || lq.configData.dataSets.get("tools").contains(itemUsed)   || lq.configData.dataSets.get("utilities").contains(itemUsed) ) {
				if (!pc.allowedWeapon(itemUsed) && !pc.allowedTool(itemUsed)) {
					p.sendMessage(lq.configLang.cantUseTool);
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAttack(EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		if (!(e.getDamager() instanceof Player)) {
			return;
		}

		Player p = (Player) e.getDamager();
		PC pc = lq.players.getPC(p);
		int itemUsed = p.getItemInHand().getTypeId();

		if (itemUsed > 0) {
			if (lq.configData.dataSets.get("weapons").contains(itemUsed) || lq.configData.dataSets.get("tools").contains(itemUsed)  || lq.configData.dataSets.get("utilities").contains(itemUsed) ) {
				if (!pc.allowedWeapon(itemUsed) && !pc.allowedTool(itemUsed)) {
					p.sendMessage(lq.configLang.cantUseWeapon);
					event.setCancelled(true);
				}
			}
		}
	}
}
