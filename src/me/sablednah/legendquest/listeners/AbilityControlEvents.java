package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.experience.ExperienceSource;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class AbilityControlEvents implements Listener {

	public Main	lq;

	public AbilityControlEvents(final Main p) {
		this.lq = p;
	}

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
		if (!pc.canTame()) {
			p.sendMessage(lq.configLang.cantTame);
			event.setCancelled(true);
			return;
		}

	}

}
