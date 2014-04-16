package me.sablednah.legendquest.listeners;

import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.EnchantSkill;
import me.sablednah.legendquest.events.Haggle;
import me.sablednah.legendquest.mechanics.Attribute;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.SetExp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

public class AttributeCheckEvent implements Listener {
    
    public Main lq;
    
    public AttributeCheckEvent(final Main p) {
        this.lq = p;
    }
    
    // Shopping  award extra something based on charisma
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShop(InventoryCloseEvent event) {
        if (event.getView().getType() == InventoryType.MERCHANT) {
            
            int charismaMod = 0;
            
            final Player p = (Player) event.getPlayer();
            final PC pc = lq.players.getPC(p);
            if (pc != null) {
                charismaMod = pc.getAttributeModifier(Attribute.CHR);
                
                Haggle e = new Haggle(event, charismaMod, pc);
                lq.getServer().getPluginManager().callEvent(e);
                event = e.getEvent();
                charismaMod = e.getCharismaMod();
                
                // TODO use modifier in some way!
            }
        }
    }

    // Shopping award extra something based on wisdom
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {

        final Player p = (Player) event.getEnchanter();
        final PC pc = lq.players.getPC(p);
        
        if (pc != null) {
            int wisdomMod = 0;
            wisdomMod = pc.getAttributeModifier(Attribute.WIS);
            
            EnchantSkill e = new EnchantSkill(event, wisdomMod, pc);
            lq.getServer().getPluginManager().callEvent(e);
            event = e.getEvent();
            wisdomMod = e.getWisdomMod();
            
            int cost = event.getExpLevelCost();
            int currentLevel = p.getLevel();
            int newlevel = currentLevel-cost;
            
            int upper = SetExp.getExpAtLevel(currentLevel);
            int lower = SetExp.getExpAtLevel(newlevel);
            int realXPdiference = upper-lower;
            
            double bonusxp = (realXPdiference/100.0D) * wisdomMod;
            
            lq.getServer().getScheduler().runTask(lq, new BonusExp(p.getUniqueId(),(int)bonusxp));
            
        }
    }
    
    public class BonusExp implements Runnable {
        private UUID u;
        private int xp;
        public BonusExp(UUID u, int xp) {
            this.u=u;
            this.xp=xp;
        }
        @Override
        public void run() {
            Player pl = Bukkit.getPlayer(u);
            if (pl !=null) {
               int exp = SetExp.getTotalExperience(pl);
               exp += xp;
               SetExp.setTotalExperience(pl, exp);
            }
        }
    }
}
