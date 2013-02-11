package me.sablednah.legendquest.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.scheduler.BukkitTask;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import lib.PatPeter.SQLibrary.*;

public class DataSync {
	public Main							lq;
	public ConcurrentLinkedQueue<PC>	pendingWrites	= new ConcurrentLinkedQueue<PC>();
	public Database						dbconn;
	private BukkitTask						aSyncTaskKeeper;
	private BukkitTask						aSyncTaskQueue;
	
	public DataSync(Main p) {
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

	public synchronized void addWrite(PC pc) {
		pendingWrites.add(pc);
	}

	public synchronized int getXP(String playerName, String className) {
		String sql;
		int xp = 0;
		try {
			sql = "SELECT xp FROM xpEarnt WHERE player='" + playerName + "' and class='" + className.toLowerCase() + "';";
			ResultSet r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					xp = r.getInt("xp");
				}
			}
			return xp;
		} catch (SQLException e) {
			lq.logSevere("Error reading XP from database.");
			e.printStackTrace();
		}
		return xp;
	}

	public synchronized HashMap<String,Integer> getXPs(String playerName) {
		String sql;
		HashMap<String,Integer> result = new HashMap<String,Integer>();
		try {
			sql = "SELECT xp,class FROM xpEarnt WHERE player='" + playerName + "';";
			ResultSet r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					result.put(r.getString("class"),r.getInt("xp"));
				}
			}
		} catch (SQLException e) {
			lq.logSevere("Error reading XP from database.");
			e.printStackTrace();
		}
		return result;
	}
	
	public synchronized PC getData(String pName) {
		lq.debug.fine("loading "+pName+" from db");
		String sql;
		PC pc = new PC(lq, pName);
		sql = "SELECT * FROM pcs WHERE player='" + pName + "';";
		lq.debug.fine(sql);
		try {
			ResultSet r = dbconn.query(sql);
			if (r == null) {
				return null;
			}
			while (r.next()) {
				
				pc.charname = r.getString("charname");
				lq.debug.fine("loading character "+pc.charname);
				
				pc.maxHP = r.getInt("maxHP");
				pc.health = r.getInt("health");
				pc.mana = r.getInt("mana");
				pc.skillpoints = r.getInt("skillpoints");
				pc.race = lq.races.getRace(r.getString("race"));
				pc.raceChanged = r.getBoolean("raceChanged");

				pc.mainClass = lq.classes.getClass(r.getString("mainClass"));
				pc.subClass = lq.classes.getClass(r.getString("subClass"));

				lq.debug.fine("class is "+pc.mainClass.name);
				
				pc.statStr = r.getInt("statStr");
				pc.statDex = r.getInt("statDex");
				pc.statInt = r.getInt("statInt");
				pc.statWis = r.getInt("statWis");
				pc.statCon = r.getInt("statCon");
				pc.statChr = r.getInt("statChr");

			}
			r.close();
			sql = "SELECT xp, class FROM xpEarnt WHERE player='" + pName + "';";
			lq.debug.fine(sql);
			int thisXP;
			r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					thisXP = r.getInt("xp");
					lq.debug.fine("found "+thisXP+" xp for "+r.getString("class"));
					if (pc.mainClass.name.toLowerCase() == r.getString("class")) {
						pc.currentXP = thisXP;
						lq.debug.fine(r.getString("class") + " is current class - setting main XP");
					}
					pc.xpEarnt.put(r.getString("class").toLowerCase(), thisXP);
				}
			}
			return pc;
		} catch (SQLException e) {
			lq.logSevere("Error reading XP from to database.");
			e.printStackTrace();
		}
		return null;
	}

	private synchronized void writeData(PC pc) {
		String sql;
		sql = "REPLACE INTO pcs (";
		sql = sql + "player,charname,race,raceChanged,mainClass,subClass,maxHP,health,mana,statStr,statDex,statInt,statWis,statCon,statChr,skillpoints";
		sql = sql + ") values(\"";
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
		sql = sql + pc.statStr + ",";
		sql = sql + pc.statDex + ",";
		sql = sql + pc.statInt + ",";
		sql = sql + pc.statWis + ",";
		sql = sql + pc.statCon + ",";
		sql = sql + pc.statChr + ",";
		sql = sql + pc.skillpoints;
		sql = sql + ");";
		lq.debug.fine(sql);

		try {
			ResultSet r = dbconn.query(sql);
			r.close();
			String sql2;
			for (Map.Entry<String, Integer> entry : pc.xpEarnt.entrySet()) {
				sql2 = "REPLACE INTO xpEarnt (";
				sql2 = sql2 + "player,class,xp";
				sql2 = sql2 + ") values(\"";
				sql2 = sql2 + pc.player + "\",\"";
				sql2 = sql2 + entry.getKey().toLowerCase() + "\",";
				sql2 = sql2 + entry.getValue();
				sql2 = sql2 + ");";
				lq.debug.fine(sql2);
				r = dbconn.query(sql2);
				r.close();
			}
		} catch (SQLException e) {
			lq.logSevere("Error writing pc from database.");
			e.printStackTrace();
		}
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

	public void shutdown() {
		aSyncTaskQueue.cancel();
		aSyncTaskKeeper.cancel();
		flushdb();
		dbconn.close();
	}

	public class dbKeepAlive implements Runnable {
		@Override
		public void run() {
			try {
				dbconn.query("SELECT 1");
			} catch (SQLException e) {
				lq.logSevere("Error during database keep-alive.");
				e.printStackTrace();
			}
		}
	}

	public class dbProcessCache implements Runnable {
		@Override
		public void run() {
			PC pc = pendingWrites.poll();
			if (pc != null) {
				writeData(pc);
			}
		}
	}

	private void tableCheck() {
		String create;
		create = "CREATE TABLE if not exists pcs (";
		create += "player varchar(16) NOT NULL";
		if (!lq.configMain.useMySQL) {
			create += " UNIQUE ON CONFLICT FAIL";
		}
		create += ", ";
		create += "charname varchar(64) NOT NULL, ";
		create += "race varchar(64), ";
		create += "raceChanged INTEGER, ";
		create += "mainClass varchar(64), ";
		create += "subClass varchar(64), ";
		create += "maxHP INTEGER, ";
		create += "health INTEGER, ";
		create += "mana INTEGER, ";
		create += "statStr INTEGER, ";
		create += "statDex INTEGER, ";
		create += "statInt INTEGER, ";
		create += "statWis INTEGER, ";
		create += "statCon INTEGER, ";
		create += "statChr INTEGER, ";
		create += "skillpoints INTEGER";

		if (lq.configMain.useMySQL) {
			create += ", PRIMARY KEY (player)";
		}
		create += " );";
		lq.debug.fine(create);

		ResultSet r;
		try {
			r = dbconn.query(create);
			lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				create = "CREATE UNIQUE INDEX IF NOT EXISTS player_index ON pcs(player)";
				r = dbconn.query(create);
				r.close();
			}
		} catch (SQLException e) {
			lq.logSevere("Error creating table 'pcs'.");
			e.printStackTrace();
		}

		create = "CREATE TABLE if not exists xpEarnt (";
		create += "player varchar(16) NOT NULL, ";
		create += "class varchar(64) NOT NULL, ";
		create += "xp INTEGER";
		if (lq.configMain.useMySQL) {
			create += ", CONSTRAINT pid PRIMARY KEY (player,class)";
		} else {
			create += ", UNIQUE(player, class) ON CONFLICT REPLACE";
		}
		create += " );";
		lq.debug.fine(create);
		try {
			r = dbconn.query(create);
			lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				create = "CREATE UNIQUE INDEX IF NOT EXISTS player_class_index ON xpEarnt(player,class)";
				dbconn.query(create);
				r.close();
			}
		} catch (SQLException e) {
			lq.logSevere("Error creating table 'xpEarnt'.");
			e.printStackTrace();
		}
	}
}
