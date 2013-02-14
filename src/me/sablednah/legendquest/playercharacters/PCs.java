package me.sablednah.legendquest.playercharacters;

import java.util.HashMap;
import java.util.Map;

import me.sablednah.legendquest.Main;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PCs {

    public class DelayedWrite implements Runnable {

        public String name;

        public DelayedWrite(final String n) {
            this.name = n;
        }

        @Override
        public void run() {
            savePlayer(name);
        }
    }

    public Main lq;

    public Map<String, PC> activePlayers = new HashMap<String, PC>();

    public PCs(final Main p) {
        this.lq = p;
        for (final Player player : lq.getServer().getOnlinePlayers()) {
            final String pName = player.getName();
            final PC pc = getPC(player);
            addPlayer(pName, pc);
        }
    }

    public void addPlayer(final String pName, final PC pc) {
        activePlayers.put(pName, pc);
    }

    public PC getPC(final OfflinePlayer p) {
        if (p != null) {
            return getPC(p.getName());
        }
        return null;
    }

    public PC getPC(final Player p) {
        if (p != null) {
            return getPC(p.getName());
        }
        return null;
    }

    public PC getPC(final String name) {
        PC pc = null;
        if (name != null) {
            pc = activePlayers.get(name);
            if (pc == null) {
                pc = loadPlayer(name);
            }
        }
        return pc;
    }

    private PC loadPlayer(final String pName) {
        // Load player from disk. if not found make new blank
        PC pc = null;
        pc = lq.datasync.getData(pName);
        if (pc == null) {
            pc = new PC(lq, pName);
        }
        return pc;
    }

    public void removePlayer(final String pName) {
        savePlayer(pName);
        activePlayers.remove(pName);
    }

    public void savePlayer(final PC pc) {
        lq.datasync.addWrite(pc);
    }

    public void savePlayer(final String name) {
        savePlayer(getPC(name));
    }

    public void scheduleUpdate(final String pName) {
        Bukkit.getServer().getScheduler().runTaskLater(lq, new DelayedWrite(pName), 4L);

    }
}
