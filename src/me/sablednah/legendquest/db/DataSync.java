package me.sablednah.legendquest.db;

import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.sablednah.legendquest.Main;
//Simport me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.playercharacters.PC;

import lib.PatPeter.SQLibrary.*;

public class DataSync {
	public Main							lq;
	public ConcurrentLinkedQueue<PC>	pendingWrites	= new ConcurrentLinkedQueue<PC>();
	public Database						dbconn;
	private int							aSyncTaskID;

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

		this.aSyncTaskID = lq.getServer().getScheduler().scheduleSyncRepeatingTask(lq, new dbKeepAlive(), 1200L, 1200L);
		this.aSyncTaskID = lq.getServer().getScheduler().scheduleSyncRepeatingTask(lq, new dbProcessCache(), 10L, 10L);

	}

	public synchronized void addWrite(PC pc) {
		pendingWrites.add(pc);
	}

	public synchronized PC getData(String pName) {
		// TODO read PC record from database
		String sql;
		PC pc = new PC(lq, pName);
		sql = "SELECT * FROM pcs WHERE player='" + pName + "';";
		try {
			ResultSet r = dbconn.query(sql);
			if (r == null) {
				return null;
			}
			while (r.next()) {
				pc.charname = r.getString("charname");
				pc.maxHP = r.getInt("maxHP");
				pc.health = r.getInt("health");
				pc.skillpoints = r.getInt("skillpoints");
				pc.race = lq.races.races.get(r.getString("race"));
				pc.raceChanged = r.getBoolean("raceChanged");
				
				pc.mainClass = lq.classes.classTypes.get(r.getString("mainClass"));
				pc.subClass = lq.classes.classTypes.get(r.getString("subClass"));

				pc.statStr = r.getInt("statStr");
				pc.statDex = r.getInt("statDex");
				pc.statInt = r.getInt("statInt");
				pc.statWis = r.getInt("statWis");
				pc.statCon = r.getInt("statCon");
				pc.statChr = r.getInt("statChr");

			}
			r.close();

			return pc;
		} catch (SQLException e) {
			lq.logSevere("Error writing pc to database.");
			e.printStackTrace();
		}

		return null;
	}

	private synchronized PC writeData(PC pc) {
		// TODO write PC record from database
		String sql;
		sql = "REPLACE INTO pcs (";
		sql = sql + "player,charname,race,raceChanged,mainClass,subClass,maxHP,health,statStr,statDex,statInt,statWis,statCon,statChr,skillpoints";
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
		} catch (SQLException e) {
			lq.logSevere("Error reading pc from database.");
			e.printStackTrace();
		}
		return null;
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
		lq.getServer().getScheduler().cancelTask(aSyncTaskID);
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
		// if (!dbconn.isTable("pcs")) {
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
		// } else {

		// }
		// if (!dbconn.isTable("pcs")) {
		create = "CREATE TABLE if not exists xpEarnt (";
		create += "player varchar(16) NOT NULL";
		if (!lq.configMain.useMySQL) {
			create += " UNIQUE ON CONFLICT FAIL";
		}
		create += ", ";
		create += "class varchar(64) NOT NULL, ";
		create += "xp INTEGER, ";
		create += "health INTEGER, ";
		create += "skillpoints INTEGER";
		if (lq.configMain.useMySQL) {
			create += ", PRIMARY KEY (player)";
		}
		create += " );";
		lq.debug.fine(create);
		try {
			r = dbconn.query(create);
			lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				create = "CREATE UNIQUE INDEX IF NOT EXISTS player_class_index ON xpEarnt(player)";
				dbconn.query(create);
				r.close();
			}
		} catch (SQLException e) {
			lq.logSevere("Error creating table 'xpEarnt'.");
			e.printStackTrace();
		}
		// } else {

		// }

	}

}
