package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.loadout.Loadout;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class SkillLinkEvents implements Listener {

	public Main	lq;

	public SkillLinkEvents(final Main p) {
		this.lq = p;
	}

	// catch "use" actions
	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();

		if (event.getItem()== null || event.getItem().getType().equals(Material.AIR)){
			return;
		}
		
		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		final PC pc = lq.players.getPC(p);
		final Material itemUsed = p.getItemInHand().getType();
		final Action act = event.getAction();
		if (itemUsed != null) {
			if (act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK) {
				String linkedSkill = pc.getLink(itemUsed);
				if (linkedSkill != null) {
					if (lq.effectManager.getPlayerEffects(p.getUniqueId()).contains(Effects.STUNNED)) {
						// stunned - no skills
						p.sendMessage(lq.configLang.skillStunned + linkedSkill);
					} else {
						p.sendMessage(lq.configLang.skillLinkUse + linkedSkill);
/*
						if (Main.debugMode) {
							System.out.print(lq.configLang.skillLinkUse + linkedSkill);
						}
*/
						pc.useSkill(linkedSkill,null);
					}
				} else {
					//check for loadout use
					if (Main.debugMode) { System.out.print("Looking for loadout"); }
					Loadout load = pc.getLoadout(true);
					if (Main.debugMode) { System.out.print("loadout:" + load); }

					if (load != null && load.activeskill != null) {
						if (lq.effectManager.getPlayerEffects(p.getUniqueId()).contains(Effects.STUNNED)) {
							// stunned - no skills
							p.sendMessage(lq.configLang.skillStunned + linkedSkill);
						} else {
							p.sendMessage(lq.configLang.skillLinkUse + load.activeskill + " ["+load.name+"]");
							if (Main.debugMode) {
								System.out.print(lq.configLang.skillLinkUse + linkedSkill);
							}
							pc.useSkill(load.activeskill,null);
							if (load.blockItemUsage) {
								event.setCancelled(true);
							}
						}
					}
				}
			} else if (act == Action.LEFT_CLICK_AIR || act == Action.LEFT_CLICK_BLOCK) {
				// check for loadout to move to next option.
				if (Main.debugMode) { System.out.print("left click: Looking for loadout"); }
				Loadout load = pc.getLoadout(false);
				if (Main.debugMode) { System.out.print("loadout:" + load); }
				if (load != null) {
					load.nextItem();
					p.sendMessage("Skill change: " + load.name + " = " + load.activeskill);
				}
			}
		}
	}
	
	// catch "use" actions
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerItemHeldEvent(final PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		  int itemSlot=event.getNewSlot();
		if (itemSlot >= 0) {
			final PC pc = lq.players.getPC(p);
			if (pc != null) {
			    ItemStack item=p.getInventory().getItem(itemSlot);
	
			    Loadout load = pc.getLoadout(item,false);
			    if (load != null) {
			    	p.sendMessage(load.name + " : '" + load.activeskill + "' Currently selected (left click to select next)");
			    }
		    	load = pc.getLoadout(item,true);
		    	if (load != null) {
			    	p.sendMessage(load.name + " : '" + load.activeskill + "' is ready (right click to use)");
			    }		  
			}
		}
	}
}
