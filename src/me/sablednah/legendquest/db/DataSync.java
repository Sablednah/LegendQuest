package me.sablednah.legendquest.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;
import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.scheduler.BukkitTask;

public class DataSync {

    public class dbKeepAlive implements Runnable {

        @Override
        public void run() {
            try {
                dbconn.query("SELECT 1");
            } catch (final SQLException e) {
                lq.logSevere("Error during database keep-alive.");
                e.printStackTrace();
            }
        }
    }

    public class dbProcessCache implements Runnable {

        @Override
        public void run() {
            final PC pc = pendingWrites.poll();
            if (pc != null) {
                writeData(pc);
            }
        }
    }

    public Main lq;
    public ConcurrentLinkedQueue<PC> pendingWrites = new ConcurrentLinkedQueue<PC>();
    public Database dbconn;

    private final BukkitTask aSyncTaskKeeper;

    private final BukkitTask aSyncTaskQueue;

    public DataSync(final Main p) {
        this.lq = p;

        if (lq.configMain.useMySQL) {
            dbconn = new MySQL(lq.logger, "LQ_", lq.configMain.sqlHostname, lq.configMain.sqlPort, lq.configMain.sqlDatabase, lq.configMain.sqlUsername, lq.configMain.sqlPassword);
        } else {
            dbconn = new SQLite(lq.logger, "LQ_", p.getDataFolder().getPath(), "LegendQuest");
        }

        lq.log("opening db...");
        dbconn.open();

        tableCheck();

        this.aSyncTaskKeeper = lq.getServer().getScheduler().runTaskTimerAsynchronously(lq, new dbKeepAlive(), 1200L, 1200L);
        this.aSyncTaskQueue = lq.getServer().getScheduler().runTaskTimerAsynchronously(lq, new dbProcessCache(), 10L, 10L);

    }

    public synchronized void addWrite(final PC pc) {
        pendingWrites.add(pc);
    }

    public void flushdb() {
        PC pc;
        while (!pendingWrites.isEmpty()) {
            pc = pendingWrites.poll();
            if (pc != null) {
                writeData(pc);
            }
        }
    }

    public synchronized PC getData(final String pName) {
        @SuppressWarnings("deprecation")
        UUID uuid = lq.getServer().getPlayer(pName).getUniqueId();
        return (getData(uuid));
    }
    
    public synchronized PC getData(UUID uuid) {    
        lq.debug.fine("loading " + uuid.toString() + " from db");
        String sql;
        final PC pc = new PC(lq, uuid);
        sql = "SELECT * FROM pcs WHERE uuid='" + uuid.toString() + "';";
        lq.debug.fine(sql);
        try {
            ResultSet r = dbconn.query(sql);
            if (r == null) {
                return null;
            }
            while (r.next()) {

                pc.charname = r.getString("charname");
                lq.debug.fine("loading character " + pc.charname);

                pc.maxHP = r.getInt("maxHP");
                pc.health = r.getInt("health");
                pc.karma = r.getInt("karma");
                pc.mana = r.getInt("mana");
                pc.race = lq.races.getRace(r.getString("race"));
                pc.raceChanged = r.getBoolean("raceChanged");

                pc.mainClass = lq.classes.getClass(r.getString("mainClass"));
                pc.subClass = lq.classes.getClass(r.getString("subClass"));

                lq.debug.fine("class is " + pc.mainClass.name);

                pc.statStr = r.getInt("statStr");
                pc.statDex = r.getInt("statDex");
                pc.statInt = r.getInt("statInt");
                pc.statWis = r.getInt("statWis");
                pc.statCon = r.getInt("statCon");
                pc.statChr = r.getInt("statChr");


            }
            r.close();

            sql = "SELECT xp, class FROM xpEarnt WHERE uuid='" + uuid.toString() + "';";
            lq.debug.fine(sql);
            int thisXP;
            r = dbconn.query(sql);
            if (r != null) {
                while (r.next()) {
                    thisXP = r.getInt("xp");
                    lq.debug.fine("found " + thisXP + " xp for " + r.getString("class"));
                    if (pc.mainClass.name.toLowerCase().equals(r.getString("class"))) {
                        pc.currentXP = thisXP;
                        lq.debug.fine(r.getString("class") + " is current class - setting main XP");
                    }
                    pc.xpEarnt.put(r.getString("class").toLowerCase(), thisXP);
                }
            }

            sql = "SELECT skillName FROM skillsBought WHERE uuid='" + uuid.toString() + "';";
            lq.debug.fine(sql);
            int skillCost;
            r = dbconn.query(sql);
            if (r != null) {
                while (r.next()) {
                    skillCost = r.getInt("cost");
                    lq.debug.fine("found " + skillCost + " cost for " + r.getString("skillName"));
                    pc.xpEarnt.put(r.getString("skillName").toLowerCase(), skillCost);
                }
            }

            return pc;
        } catch (final SQLException e) {
            lq.logSevere("Error reading XP from to database.");
            e.printStackTrace();
        }
        return null;
    }

    public synchronized int getXP(final UUID uuid, final String className) {
        String sql;
        int xp = 0;
        try {
            sql = "SELECT xp FROM xpEarnt WHERE uuid='" + uuid.toString() + "' and class='" + className.toLowerCase() + "';";
            final ResultSet r = dbconn.query(sql);
            if (r != null) {
                while (r.next()) {
                    xp = r.getInt("xp");
                }
            }
            return xp;
        } catch (final SQLException e) {
            lq.logSevere("Error reading XP from database.");
            e.printStackTrace();
        }
        return xp;
    }

    public synchronized HashMap<String, Integer> getXPs(final UUID uuid) {
        String sql;
        final HashMap<String, Integer> result = new HashMap<String, Integer>();
        try {
            sql = "SELECT xp,class FROM xpEarnt WHERE uuid='" + uuid.toString() + "';";
            final ResultSet r = dbconn.query(sql);
            if (r != null) {
                while (r.next()) {
                    result.put(r.getString("class"), r.getInt("xp"));
                }
            }
        } catch (final SQLException e) {
            lq.logSevere("Error reading XP from database.");
            e.printStackTrace();
        }
        return result;
    }

    public void shutdown() {
        aSyncTaskQueue.cancel();
        aSyncTaskKeeper.cancel();
        flushdb();
        dbconn.close();
    }

    private void tableCheck() {
        String create;
        create = "CREATE TABLE if not exists pcs (";
        create += "uuid varchar(32) NOT NULL";
        if (!lq.configMain.useMySQL) {
            create += " UNIQUE ON CONFLICT FAIL";
        }
        create += ", ";
        create += "player varchar(16) NOT NULL, ";
        create += "charname varchar(64) NOT NULL, ";
        create += "race varchar(64), ";
        create += "raceChanged INTEGER, ";
        create += "mainClass varchar(64), ";
        create += "subClass varchar(64), ";
        create += "maxHP DOUBLE, ";
        create += "health DOUBLE, ";
        create += "mana INTEGER, ";
        create += "karma INTEGER, ";
        create += "statStr INTEGER, ";
        create += "statDex INTEGER, ";
        create += "statInt INTEGER, ";
        create += "statWis INTEGER, ";
        create += "statCon INTEGER, ";
        create += "statChr INTEGER, ";
        create += "skillpoints INTEGER";

        if (lq.configMain.useMySQL) {
            create += ", PRIMARY KEY (uuid)";
        }
        create += " );";
        lq.debug.fine(create);

        ResultSet r;
        try {
            r = dbconn.query(create);
            lq.debug.fine(r.toString());
            r.close();
            if (!lq.configMain.useMySQL) {
                create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_index ON pcs(uuid)";
                r = dbconn.query(create);
                r.close();
            }
        } catch (final SQLException e) {
            lq.logSevere("Error creating table 'pcs'.");
            e.printStackTrace();
        }

        create = "CREATE TABLE if not exists xpEarnt (";
        create += "uuid varchar(32) NOT NULL, ";
        create += "player varchar(16) NOT NULL, ";
        create += "class varchar(64) NOT NULL, ";
        create += "xp INTEGER";
        if (lq.configMain.useMySQL) {
            create += ", CONSTRAINT uid PRIMARY KEY (uuid,class)";
        } else {
            create += ", UNIQUE(uuid, class) ON CONFLICT REPLACE";
        }
        create += " );";
        lq.debug.fine(create);
        try {
            r = dbconn.query(create);
            lq.debug.fine(r.toString());
            r.close();
            if (!lq.configMain.useMySQL) {
                create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_class_index ON xpEarnt(uuid,class)";
                dbconn.query(create);
                r.close();
            }
        } catch (final SQLException e) {
            lq.logSevere("Error creating table 'xpEarnt'.");
            e.printStackTrace();
        }

        create = "CREATE TABLE if not exists skillsBought (";
        create += "uuid varchar(32) NOT NULL, ";
        create += "player varchar(16) NOT NULL, ";
        create += "skillName varchar(64) NOT NULL, ";
        create += "cost INTEGER";
        if (lq.configMain.useMySQL) {
            create += ", CONSTRAINT pid PRIMARY KEY (uuid,skillName)";
        } else {
            create += ", UNIQUE(uuid, skillName) ON CONFLICT REPLACE";
        }
        create += " );";
        lq.debug.fine(create);
        try {
            r = dbconn.query(create);
            lq.debug.fine(r.toString());
            r.close();
            if (!lq.configMain.useMySQL) {
                create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_skill_index ON skillsBought(uuid,skillName)";
                dbconn.query(create);
                r.close();
            }
        } catch (final SQLException e) {
            lq.logSevere("Error creating table 'skillsBought'.");
            e.printStackTrace();
        }
    }

    private synchronized void writeData(final PC pc) {
        String sql;
        sql = "REPLACE INTO pcs (";
        sql = sql + "uuid,player,charname,race,raceChanged,mainClass,subClass,maxHP,health,mana,karma,statStr,statDex,statInt,statWis,statCon,statChr";
        sql = sql + ") values(\"";
        sql = sql + pc.uuid.toString() + "\",\"";
        sql = sql + pc.player + "\",\"";
        sql = sql + pc.charname + "\",\"";
        sql = sql + pc.race.name + "\",";
        if (pc.raceChanged) {
            sql = sql + "1,\"";
        } else {
            sql = sql + "0,\"";
        }
        sql = sql + pc.mainClass.name + "\",\"";
        if (pc.subClass != null) {
            sql = sql + pc.subClass.name + "\",";
        } else {
            sql = sql + "\",";
        }
        sql = sql + pc.maxHP + ",";
        sql = sql + pc.health + ",";
        sql = sql + pc.mana + ",";
        sql = sql + pc.karma + ",";
        sql = sql + pc.statStr + ",";
        sql = sql + pc.statDex + ",";
        sql = sql + pc.statInt + ",";
        sql = sql + pc.statWis + ",";
        sql = sql + pc.statCon + ",";
        sql = sql + pc.statChr;
        sql = sql + ");";
        lq.debug.fine(sql);

        try {
            ResultSet r = dbconn.query(sql);
            r.close();
            String sql2;
            for (final Map.Entry<String, Integer> entry : pc.xpEarnt.entrySet()) {
                sql2 = "REPLACE INTO xpEarnt (";
                sql2 = sql2 + "uuid,player,class,xp";
                sql2 = sql2 + ") values(\"";
                sql2 = sql2 + pc.uuid.toString() + "\",\"";
                sql2 = sql2 + pc.player + "\",\"";
                sql2 = sql2 + entry.getKey().toLowerCase() + "\",";
                sql2 = sql2 + entry.getValue();
                sql2 = sql2 + ");";
                lq.debug.fine(sql2);
                r = dbconn.query(sql2);
                r.close();
            }

            for (final Map.Entry<String, Integer> entry : pc.skillsPurchased.entrySet()) {
                sql2 = "REPLACE INTO skillsBought (";
                sql2 = sql2 + "uuid, player,skillName,cost";
                sql2 = sql2 + ") values(\"";
                sql2 = sql2 + pc.uuid + "\",\"";
                sql2 = sql2 + pc.player + "\",\"";
                sql2 = sql2 + entry.getKey() + "\",";
                sql2 = sql2 + entry.getValue();
                lq.debug.fine(sql2);
                r = dbconn.query(sql2);
                r.close();
            }

        } catch (final SQLException e) {
            lq.logSevere("Error writing pc to database.");
            e.printStackTrace();
        }
    }
}
