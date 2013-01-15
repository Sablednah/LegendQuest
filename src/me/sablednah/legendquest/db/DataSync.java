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
		PC pc = new PC(pName);
		sql = "SELECT * FROM pc WHERE player='"+pName+"';";
		try {
			ResultSet r = dbconn.query(sql);
			if (r==null) { return null; }
			while (r.next()) {
				sql = sql + "player,charname,mainClass,subClass,maxHP,health,skillpoints";

				pc.charname = r.getString("charname");
				pc.maxHP = r.getInt("charname");
				pc.health = r.getInt("charname");
				pc.skillpoints = r.getInt("charname");
				
				pc.mainClass = lq.classes.classTypes.get(r.getString("mainClass"));
				pc.subClass = lq.classes.classTypes.get(r.getString("subClass"));
				
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
		sql = "REPLACE pc (";
		sql = sql + "player,charname,mainClass,subClass,maxHP,health,skillpoints";
		sql = sql + ") values(";
		sql = sql + pc.player + ",";
		sql = sql + pc.charname + ",";
		sql = sql + pc.mainClass.name + ",";
		sql = sql + pc.subClass.name + ",";
		sql = sql + pc.maxHP + ",";
		sql = sql + pc.health + ",";
		sql = sql + pc.skillpoints;
		sql = sql + ");";
		
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
		create += "mainClass varchar(64), ";
		create += "subClass varchar(64), ";
		create += "maxHP INTEGER, ";
		create += "health INTEGER, ";
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
				r= dbconn.query(create);
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
