package me.sablednah.legendquest.playercharacters;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PCs {

    public class DelayedWrite implements Runnable {

        public UUID uuid;

        public DelayedWrite(UUID u) {
            this.uuid = u;
        }

        @Override
        public void run() {
            savePlayer(uuid);
        }
    }

    public Main lq;

    public Map<UUID, PC> activePlayers = new HashMap<UUID, PC>();

    public PCs(Main p) {
        this.lq = p;
        for (final Player player : lq.getServer().getOnlinePlayers()) {
            final UUID uuid = player.getUniqueId();
            final PC pc = getPC(player);
            addPlayer(uuid, pc);
        }
    }

    public void addPlayer( UUID uuid,  PC pc) {
        activePlayers.put(uuid, pc);
    }

    public PC getPC(OfflinePlayer p) {
        if (p != null) {
            return getPC(p.getUniqueId());
        }
        return null;
    }

    public PC getPC(Player p) {
        if (p != null) {
            return getPC(p.getUniqueId());
        }
        return null;
    }
    
    @Deprecated
    public PC getPC(String pName) {
        if (pName != null) {
            UUID uuid = Utils.getPlayerUUID(pName);
            return getPC(uuid);
        }
        return null;
    }
    
    public PC getPC(UUID uuid) {
        PC pc = null;
        if (uuid != null) {
            pc = activePlayers.get(uuid);
            if (pc == null) {
                pc = loadPlayer(uuid);
            }
        }
        return pc;
    }

    private PC loadPlayer(UUID uuid) {
        // Load player from disk. if not found make new blank
        PC pc = null;
        pc = lq.datasync.getData(uuid);
        if (pc == null) {
            pc = new PC(lq, uuid);
        }
        return pc;
    }

    public void removePlayer(UUID uuid) {
        savePlayer(uuid);
        activePlayers.remove(uuid);
    }

    public void savePlayer(PC pc) {
        lq.datasync.addWrite(pc);
    }

    public void savePlayer(UUID uuid) {
        savePlayer(getPC(uuid));
    }

    public void scheduleUpdate(UUID uuid) {
        Bukkit.getServer().getScheduler().runTaskLater(lq, new DelayedWrite(uuid), 4L);
    }
}
