package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.LevelUpEvent;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.SetExp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerEvents implements Listener {

    public class delayedSpawn implements Runnable {

        public int xp;
        public Player player;

        public delayedSpawn(final int xp, final Player player) {
            this.xp = xp;
            this.player = player;
        }

        @Override
        public void run() {
            final String pName = player.getName();
            final PC pc = lq.players.getPC(pName);
            pc.setXP(xp);
            lq.players.savePlayer(pc);
        }
    }

    public Main lq;

    public PlayerEvents(final Main p) {
        this.lq = p;
    }

    // preserve XP on death...
    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(final PlayerDeathEvent event) {
        event.setDroppedExp(0);
        event.setKeepLevel(true);
    }

    // set to monitor - we're not gonna change the login, only load our data
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        final String pName = p.getName();
        final PC pc = lq.players.getPC(pName);
        lq.players.addPlayer(pName, pc);
        p.setTotalExperience(pc.currentXP);
        p.setMaxHealth(pc.maxHP);
        p.setHealth(pc.health);
        pc.healthCheck();
    }

    // set to monitor - we can't change the quit - just want to clean our data up.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final PlayerQuitEvent event) {
        final String pName = event.getPlayer().getName();
        lq.players.removePlayer(pName);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onRespawn(final PlayerRespawnEvent event) {
        final Player p = event.getPlayer();
        final String pName = p.getName();
        final PC pc = lq.players.getPC(pName);
        lq.logWarn("currentXP: " + pc.currentXP);
        final int currentXP = SetExp.getTotalExperience(p);
        lq.logWarn("totxp: " + currentXP);
        final int xpLoss = (int) (currentXP * (lq.configMain.percentXpLossRespawn / 100));
        lq.logWarn("xpLoss: " + xpLoss);
        final int newXp = currentXP - xpLoss;
        pc.setXP(newXp);
        lq.players.savePlayer(pc);
        lq.getServer().getScheduler().runTaskLater(lq, new delayedSpawn(newXp, p), 5);
    }

    // track EXP changes - and halve then if dual class
    @EventHandler(priority = EventPriority.LOW)
    public void onXPChange(final PlayerExpChangeEvent event) {
        int xpAmount = event.getAmount();
        final Player p = event.getPlayer();
        final String pName = p.getName();
        final PC pc = lq.players.getPC(pName);

        // half xp gain for dual class
        if (pc.subClass != null) {
            xpAmount = xpAmount / 2;
        }
        pc.setXP(SetExp.getTotalExperience(p) + xpAmount);
        lq.players.addPlayer(pName, pc);
        lq.players.savePlayer(pc);

        if (xpAmount >= p.getExpToLevel()) {
            pc.scheduleHealthCheck();
            final LevelUpEvent e = new LevelUpEvent(p, p.getLevel() + 1, pc);
            Bukkit.getServer().getPluginManager().callEvent(e);
        }
    }

}
