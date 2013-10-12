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
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemControlEvents implements Listener {

    public Main lq;

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
        final PC pc = lq.players.getPC(p);
        final Material itemUsed = p.getItemInHand().getType();

        if (itemUsed != null) {
            if (lq.configData.dataSets.get("weapons").contains(itemUsed) || lq.configData.dataSets.get("tools").contains(itemUsed)
                    || lq.configData.dataSets.get("utility").contains(itemUsed)) {
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
        final PC pc = lq.players.getPC(p);
        final Material itemUsed = p.getItemInHand().getType();
        if (itemUsed != null) {
            if (lq.configData.dataSets.get("weapons").contains(itemUsed) || lq.configData.dataSets.get("tools").contains(itemUsed)
                    || lq.configData.dataSets.get("utility").contains(itemUsed)) {
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
        final PC pc = lq.players.getPC(p);
        final Material itemUsed = p.getItemInHand().getType();
        final Action act = event.getAction();
        if (itemUsed != null) {
            if (act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK) {
                if (lq.configData.dataSets.get("weapons").contains(itemUsed) || lq.configData.dataSets.get("tools").contains(itemUsed)
                        || lq.configData.dataSets.get("utility").contains(itemUsed)) {
                    if (!pc.allowedWeapon(itemUsed) && !pc.allowedTool(itemUsed)) {
                        p.sendMessage(lq.configLang.cantUseTool);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInvClick(final InventoryClickEvent event) {
        final Player p = (Player) event.getWhoClicked();
        final PC pc = lq.players.getPC(p);
        // detect auto armour equip...
        if (event.isShiftClick()) {
            lq.debug.fine("Shift Click used in admin armour by " + p.getDisplayName());
        }
        if (event.getCursor() != null) {
            // player has item on the cursor
            final Material itemID = event.getCursor().getType();
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

    // check for armour validity when inventory is closed
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInvClose(final InventoryCloseEvent event) {
        // check if this is a real players inventory...
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        if (event.getPlayer().getName() == null) {
            return;
        }
        final Player p = (Player) event.getPlayer();
        final PC pc = lq.players.getPC(p);
        // pc.scheduleCheckInv();
        pc.checkInv();
    }

    // Stop Projectile 'Fire'
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectile(final ProjectileLaunchEvent event) {
        final Projectile bullit = event.getEntity();
        if (!(bullit.getShooter() instanceof Player)) {
            return;
        }
        final Player p = (Player) bullit.getShooter();
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
