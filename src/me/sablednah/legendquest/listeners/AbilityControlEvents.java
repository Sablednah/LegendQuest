package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class AbilityControlEvents implements Listener {

    public Main lq;

    public AbilityControlEvents(final Main p) {
        this.lq = p;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getWhoClicked();
        PC pc = lq.players.getPC(p);
        Result r = event.getResult();
        // next line IF we do PER item blocking..
        // int id = event.getRecipe().getResult().getTypeId();
        if (r != null) {
            if (!pc.canCraft()) {
                event.setResult(Result.DENY);
                p.sendMessage(lq.configLang.cantCraft);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void Control(PlayerInteractEvent event) {
        Player p = event.getPlayer();
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
}