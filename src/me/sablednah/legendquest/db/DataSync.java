package me.sablednah.legendquest.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;
import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.party.Party;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.skills.SkillDataStore;
import me.sablednah.legendquest.skills.SkillPhase;
import me.sablednah.legendquest.utils.SerializableLocation;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class DataSync {

	public class dbKeepAlive implements Runnable {

		public void run() {
			try {
				makeOpen();
				dbconn.query("SELECT 1");
			} catch (final SQLException e) {
				lq.logSevere("Error during database keep-alive.");
				e.printStackTrace();
			}
		}
	}

	public class dbProcessCache implements Runnable {

		public void run() {
			final PC pc = pendingWrites.poll();
			if (pc != null) {
				writeData(pc);
			}

			final HealthStore hs = pendingHPWrites.poll();
			if (hs != null) {
				writeHealthData(hs);
			}

			if (dirtyPartyData) {
				saveParties(lq.partyManager.partyList);
				dirtyPartyData = false;
			}
		}
	}

	public Main									lq;
	public ConcurrentLinkedQueue<PC>			pendingWrites	= new ConcurrentLinkedQueue<PC>();
	public ConcurrentLinkedQueue<HealthStore>	pendingHPWrites	= new ConcurrentLinkedQueue<HealthStore>();
	public Database								dbconn;
	public boolean								dirtyPartyData	= false;

	private final BukkitTask					aSyncTaskKeeper;
	private final BukkitTask					aSyncTaskQueue;

	public DataSync(final Main p) {
		this.lq = p;

		if (lq.configMain.useMySQL) {
			dbconn = new MySQL(lq.logger, lq.configMain.sqlPrefix, lq.configMain.sqlHostname, lq.configMain.sqlPort, lq.configMain.sqlDatabase, lq.configMain.sqlUsername, lq.configMain.sqlPassword);
		} else {
			dbconn = new SQLite(lq.logger, lq.configMain.sqlPrefix, p.getDataFolder().getPath(), "LegendQuest");
		}

		lq.log("opening db...");
		dbconn.open();

		tableCheck();
		updateCheck();

		this.aSyncTaskKeeper = lq.getServer().getScheduler().runTaskTimerAsynchronously(lq, new dbKeepAlive(), lq.configMain.sqlKeepAliveInterval * 20L, lq.configMain.sqlKeepAliveInterval * 20L);
		this.aSyncTaskQueue = lq.getServer().getScheduler().runTaskTimerAsynchronously(lq, new dbProcessCache(), 11L, 2L);

	}

	public boolean makeOpen() {
		if (lq.configMain.useMySQL) {
			if (!dbconn.isOpen()) {
				return dbconn.open();
			}
		}
		return true;
	}

	public synchronized void addWrite(final PC pc) {
		if (pendingWrites.contains(pc)) {
			pendingWrites.remove(pc);
		} else {
			pendingWrites.add(pc);
		}
	}

	public synchronized void addHPWrite(final HealthStore hp) {
		if (pendingHPWrites.contains(hp)) {
			pendingHPWrites.remove(hp);
		} else {
			pendingHPWrites.add(hp);
		}
	}

	public synchronized void addHPWrite(final Player p) {
		HealthStore hp = new HealthStore(p.getUniqueId(), p.getHealth(), p.getMaxHealth());
		addHPWrite(hp);
	}

	public void flushdb() {
		lq.log("Flushing database...");
		PC pc;
		while (!pendingWrites.isEmpty()) {
			pc = pendingWrites.poll();
			if (pc != null) {
				lq.log("Saving Character: " + pc.player);				
				writeData(pc);
			}
		}
		
		while (!pendingHPWrites.isEmpty()) {
			HealthStore hp;
			hp = pendingHPWrites.poll();
			if (hp != null) {
				lq.log("Saving Health: " + hp.getUuid());				
				writeHealthData(hp);
			}
		}
		lq.log("Saving Party info......");
		saveParties(lq.partyManager.partyList);
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
		sql = "SELECT * FROM " + lq.configMain.sqlPrefix + "pcs WHERE uuid='" + uuid.toString() + "';";
		if (lq.configMain.logSQL) {
			lq.debug.fine(sql);
		}
		try {
			makeOpen();
			ResultSet r = dbconn.query(sql);
			if (r == null) {
				return null;
			}
			while (r.next()) {

				pc.charname = r.getString("charname");
				lq.debug.fine("loading character " + pc.charname);

				pc.maxHP = r.getDouble("maxHP");
				pc.setHealth(r.getDouble("health"));
				pc.karma = r.getLong("karma");
				pc.mana = r.getInt("mana");
				pc.race = lq.races.getRace(r.getString("race"));
				
				if (pc.race == null) {
					lq.logSevere("Race '" + r.getString("race") + "' not found, setting to default race");					
					pc.race=lq.races.defaultRace;
				}
				
				pc.raceChanged = r.getBoolean("raceChanged");

				pc.mainClass = lq.classes.getClass(r.getString("mainClass"));
				if (pc.mainClass == null) {
					lq.logSevere("Main class '" + r.getString("mainClass") + "' not found, setting to default class");					
					pc.mainClass=lq.classes.defaultClass;
				}
				pc.subClass = lq.classes.getClass(r.getString("subClass"));
				if (pc.subClass == null && (r.getString("subClass")!=null && !(r.getString("subClass").isEmpty()) )) {
					lq.logSevere("Sub class '" + r.getString("subClass") + "' not found, setting to null");					
					pc.subClass = null;
				}

				
				lq.debug.fine("class is " + pc.mainClass.name);

				pc.setStatStr(r.getInt("statStr"));
				pc.setStatDex(r.getInt("statDex"));
				pc.setStatInt(r.getInt("statInt"));
				pc.setStatWis(r.getInt("statWis"));
				pc.setStatCon(r.getInt("statCon"));
				pc.setStatChr(r.getInt("statChr"));

			}
			r.close();

			sql = "SELECT xp, class FROM " + lq.configMain.sqlPrefix + "xpEarnt WHERE uuid='" + uuid.toString() + "';";
			if (lq.configMain.logSQL) {
				lq.debug.fine(sql);
			}
			int thisXP;
			makeOpen();
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

			sql = "SELECT skillName, cost FROM " + lq.configMain.sqlPrefix + "skillsBought WHERE uuid='" + uuid.toString() + "';";
			if (lq.configMain.logSQL) {
				lq.debug.fine(sql);
			}
			int skillCost;
			makeOpen();
			r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					skillCost = r.getInt("cost");
					lq.debug.fine("found " + skillCost + " cost for " + r.getString("skillName"));
					pc.skillsPurchased.put(r.getString("skillName"), skillCost);
				}
			}

			sql = "SELECT skillName, material FROM " + lq.configMain.sqlPrefix + "skillsLinked WHERE uuid='" + uuid.toString() + "';";
			if (lq.configMain.logSQL) {
				lq.debug.fine(sql);
			}
			makeOpen();
			r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					lq.debug.fine("found " + r.getString("material") + " linked to " + r.getString("skillName"));
					pc.addLink(Material.getMaterial(r.getString("material")), r.getString("skillName"));
				}
			}

			pc.skillSet = pc.getUniqueSkills(true);

			// load skill timings from db
			sql = "SELECT skillname,lastuse,lastuseloc,phase,lastargs FROM " + lq.configMain.sqlPrefix + "skilldata WHERE uuid='" + uuid.toString() + "';";
			if (lq.configMain.logSQL) {
				lq.debug.fine(sql);
			}
			makeOpen();
			r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					lq.debug.fine("found " + r.getString("skillname") + " lastuse " + r.getLong("lastuse"));
					SkillDataStore skd = pc.skillSet.get(r.getString("skillname"));
					if (skd != null) {
						skd.setLastUse(r.getLong("lastuse"));
						if (r.getString("lastuseloc") != null && !(r.getString("lastuseloc").isEmpty())) {
							skd.setLastUseLoc(SerializableLocation.fromString(r.getString("lastuseloc")).toLocation());
						}
						skd.setPhase(SkillPhase.valueOf(r.getString("phase")));
						String[] args = StringUtils.split(r.getString("lastargs"), "¦|¦");
						skd.setlastArgs(args);
						pc.skillSet.put(r.getString("skillname"), skd);
					}
				}
			}

			pc.dataStore = this.getDataStore(uuid);

			return pc;
		} catch (final SQLException e) {
			lq.logSevere("Error reading " + lq.configMain.sqlPrefix + "skilldata from database.");
			e.printStackTrace();
		}
		return null;
	}

	public synchronized int getXP(final UUID uuid, final String className) {
		String sql;
		int xp = 0;
		try {
			makeOpen();
			sql = "SELECT xp FROM " + lq.configMain.sqlPrefix + "xpEarnt WHERE uuid='" + uuid.toString() + "' and class='" + className.toLowerCase() + "';";
			final ResultSet r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					xp = r.getInt("xp");
				}
			}
			return xp;
		} catch (final SQLException e) {
			lq.logSevere("Error reading " + lq.configMain.sqlPrefix + "xpEarnt from database.");
			e.printStackTrace();
		}
		return xp;
	}

	public synchronized HealthStore getAltHealthStore(UUID uuid) {
		String sql;
		double hp = 0;
		double maxhp = 0;

		try {
			makeOpen();
			sql = "SELECT maxhealth,health FROM " + lq.configMain.sqlPrefix + "otherhealth WHERE uuid='" + uuid.toString() + "';";
			final ResultSet r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					hp = r.getDouble("health");
					maxhp = r.getDouble("maxhealth");
				}
			}
			HealthStore hs = new HealthStore(uuid, hp, maxhp);
			return hs;
		} catch (final SQLException e) {
			lq.logSevere("Error reading " + lq.configMain.sqlPrefix + "otherhealth from database.");
			e.printStackTrace();
		}
		return null;
	}

	public synchronized double getAltHealth(UUID uuid) {
		String sql;
		double hp = 0;
		try {
			makeOpen();
			sql = "SELECT health FROM " + lq.configMain.sqlPrefix + "otherhealth WHERE uuid='" + uuid.toString() + "';";
			final ResultSet r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					hp = r.getDouble("maxhealth");
				}
			}
			return hp;
		} catch (final SQLException e) {
			lq.logSevere("Error reading " + lq.configMain.sqlPrefix + "otherhealth from database.");
			e.printStackTrace();
		}
		return hp;
	}

	public synchronized double getAltMaxHealth(UUID uuid) {
		String sql;
		double hp = 0.0D;
		try {
			makeOpen();
			sql = "SELECT maxhealth FROM " + lq.configMain.sqlPrefix + "otherhealth WHERE uuid='" + uuid.toString() + "';";
			final ResultSet r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					hp = r.getDouble("maxhealth");
				}
			}
			return hp;
		} catch (final SQLException e) {
			lq.logSevere("Error reading max " + lq.configMain.sqlPrefix + "otherhealth from database.");
			e.printStackTrace();
		}
		return hp;
	}

	public synchronized HashMap<String, Integer> getXPs(final UUID uuid) {
		String sql;
		final HashMap<String, Integer> result = new HashMap<String, Integer>();
		try {
			makeOpen();
			sql = "SELECT xp,class FROM " + lq.configMain.sqlPrefix + "xpEarnt WHERE uuid='" + uuid.toString() + "';";
			final ResultSet r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					result.put(r.getString("class"), r.getInt("xp"));
				}
			}
		} catch (final SQLException e) {
			lq.logSevere("Error reading " + lq.configMain.sqlPrefix + "xpEarnt from database.");
			e.printStackTrace();
		}
		return result;
	}

	public synchronized ConcurrentHashMap<UUID, Party> loadParties() {
		String sql;
		ConcurrentHashMap<UUID, Party> result = new ConcurrentHashMap<UUID, Party>();
		try {
			makeOpen();
			sql = "SELECT uuid,party,owner,accepted FROM " + lq.configMain.sqlPrefix + "partydata;";
			final ResultSet r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					Party p = new Party(r.getString("party"), UUID.fromString(r.getString("uuid")), r.getBoolean("accepted"), r.getBoolean("owner"));
					result.put(UUID.fromString(r.getString("uuid")), p);
				}
			}
		} catch (final SQLException e) {
			lq.logSevere("Error reading " + lq.configMain.sqlPrefix + "partydata from database.");
			e.printStackTrace();
		}
		return result;
	}

	public void shutdown() {
		aSyncTaskQueue.cancel();
		aSyncTaskKeeper.cancel();
		flushdb();
		dbconn.close();
		lq.log("Database Closed");
	}

	private void tableCheck() {
		String create;

		// characters
		create = "CREATE TABLE if not exists " + lq.configMain.sqlPrefix + "pcs (";
		create += "uuid varchar(36) NOT NULL";
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
		create += "karma LONG, ";
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
		if (lq.configMain.logSQL) {
			lq.debug.fine(create);
		}

		ResultSet r;
		try {
			makeOpen();
			r = dbconn.query(create);
			// lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				makeOpen();
				create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_index ON " + lq.configMain.sqlPrefix + "pcs(uuid)";
				r = dbconn.query(create);
				r.close();
			}
		} catch (final SQLException e) {
			lq.logSevere("Error creating table '" + lq.configMain.sqlPrefix + "pcs'.");
			e.printStackTrace();
		}

		// experience
		create = "CREATE TABLE if not exists " + lq.configMain.sqlPrefix + "xpEarnt (";
		create += "uuid varchar(36) NOT NULL, ";
		create += "player varchar(16) NOT NULL, ";
		create += "class varchar(64) NOT NULL, ";
		create += "xp INTEGER";
		if (lq.configMain.useMySQL) {
			create += ", CONSTRAINT uid PRIMARY KEY (uuid,class)";
		} else {
			create += ", UNIQUE(uuid, class) ON CONFLICT REPLACE";
		}
		create += " );";
		if (lq.configMain.logSQL) {
			lq.debug.fine(create);
		}
		try {
			makeOpen();
			r = dbconn.query(create);
			// lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				makeOpen();
				create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_class_index ON " + lq.configMain.sqlPrefix + "xpEarnt(uuid,class)";
				dbconn.query(create);
				r.close();
			}
		} catch (final SQLException e) {
			lq.logSevere("Error creating table '" + lq.configMain.sqlPrefix + "xpEarnt'.");
			e.printStackTrace();
		}

		// purchased skills
		create = "CREATE TABLE if not exists " + lq.configMain.sqlPrefix + "skillsBought (";
		create += "uuid varchar(36) NOT NULL, ";
		create += "player varchar(16) NOT NULL, ";
		create += "skillName varchar(64) NOT NULL, ";
		create += "cost INTEGER";
		if (lq.configMain.useMySQL) {
			create += ", CONSTRAINT pid PRIMARY KEY (uuid,skillName)";
		} else {
			create += ", UNIQUE(uuid, skillName) ON CONFLICT REPLACE";
		}
		create += " );";
		if (lq.configMain.logSQL) {
			lq.debug.fine(create);
		}
		try {
			makeOpen();
			r = dbconn.query(create);
			// lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				makeOpen();
				create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_skill_index ON " + lq.configMain.sqlPrefix + "skillsBought(uuid,skillName)";
				dbconn.query(create);
				r.close();
			}
		} catch (final SQLException e) {
			lq.logSevere("Error creating table '" + lq.configMain.sqlPrefix + "skillsBought'.");
			e.printStackTrace();
		}

		// linked skills
		create = "CREATE TABLE if not exists " + lq.configMain.sqlPrefix + "skillsLinked (";
		create += "uuid varchar(36) NOT NULL, ";
		create += "player varchar(16) NOT NULL, ";
		create += "material varchar(64) NOT NULL, ";
		create += "skillName varchar(64) NOT NULL, ";
		if (lq.configMain.useMySQL) {
			create += "CONSTRAINT pid PRIMARY KEY (uuid,material)";
		} else {
			create += "UNIQUE(uuid, material) ON CONFLICT REPLACE";
		}
		create += " );";
		if (lq.configMain.logSQL) {
			lq.debug.fine(create);
		}
		try {
			makeOpen();
			r = dbconn.query(create);
			// lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				makeOpen();
				create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_material_index ON " + lq.configMain.sqlPrefix + "skillsLinked(uuid,material)";
				dbconn.query(create);
				r.close();
			}
		} catch (final SQLException e) {
			lq.logSevere("Error creating table '" + lq.configMain.sqlPrefix + "skillsLinked'.");
			e.printStackTrace();
		}

		// otherHealth
		create = "CREATE TABLE if not exists " + lq.configMain.sqlPrefix + "otherHealth(";
		create += "uuid varchar(36) NOT NULL, ";
		create += "player varchar(16) NOT NULL, ";
		create += "health DOUBLE, ";
		create += "maxhealth DOUBLE";
		if (lq.configMain.useMySQL) {
			create += ", CONSTRAINT uid PRIMARY KEY (uuid)";
		} else {
			create += ", UNIQUE(uuid) ON CONFLICT REPLACE";
		}
		create += " );";
		if (lq.configMain.logSQL) {
			lq.debug.fine(create);
		}
		try {
			makeOpen();
			r = dbconn.query(create);
			// lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				makeOpen();
				create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_index ON " + lq.configMain.sqlPrefix + "otherhealth(uuid)";
				dbconn.query(create);
				r.close();
			}
		} catch (final SQLException e) {
			lq.logSevere("Error creating table '" + lq.configMain.sqlPrefix + "otherHealth'.");
			e.printStackTrace();
		}

		// save skill timings.
		// uuid, skillname, lastuse, lastuseloc, phase, ,lastargs

		create = "CREATE TABLE if not exists " + lq.configMain.sqlPrefix + "skilldata(";
		create += "uuid varchar(36) NOT NULL, ";
		create += "skillname varchar(64) NOT NULL, ";
		create += "lastuse LONG, ";
		create += "lastuseloc varchar(256) NOT NULL, ";
		create += "lastargs varchar(256) NOT NULL, ";
		create += "phase varchar(16) NOT NULL";
		if (lq.configMain.useMySQL) {
			create += ", CONSTRAINT uid PRIMARY KEY (uuid,skillname)";
		} else {
			create += ", UNIQUE(uuid,skillname) ON CONFLICT REPLACE";
		}
		create += " );";
		if (lq.configMain.logSQL) {
			lq.debug.fine(create);
		}
		try {
			makeOpen();
			r = dbconn.query(create);
			// lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				makeOpen();
				create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_skilldata_index ON " + lq.configMain.sqlPrefix + "skilldata(uuid,skillname)";
				dbconn.query(create);
				r.close();
			}
		} catch (final SQLException e) {
			lq.logSevere("Error creating table '" + lq.configMain.sqlPrefix + "skilldata'.");
			e.printStackTrace();
		}

		// party data
		create = "CREATE TABLE if not exists " + lq.configMain.sqlPrefix + "partydata(";
		create += "uuid varchar(36) NOT NULL, ";
		create += "party varchar(64) NOT NULL, ";
		create += "owner INTEGER, ";
		create += "accepted INTEGER";
		if (lq.configMain.useMySQL) {
			create += ", CONSTRAINT uid PRIMARY KEY (uuid,party)";
		} else {
			create += ", UNIQUE(uuid,party) ON CONFLICT REPLACE";
		}
		create += " );";
		if (lq.configMain.logSQL) {
			lq.debug.fine(create);
		}
		try {
			makeOpen();
			r = dbconn.query(create);
			// lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				makeOpen();
				create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_partydata_index ON " + lq.configMain.sqlPrefix + "partydata(uuid,party)";
				dbconn.query(create);
				r.close();
			}
		} catch (final SQLException e) {
			lq.logSevere("Error creating table '" + lq.configMain.sqlPrefix + "partydata'.");
			e.printStackTrace();
		}

		// generic options table
		create = "CREATE TABLE if not exists " + lq.configMain.sqlPrefix + "datastore(";
		create += "uuid varchar(36) NOT NULL, ";
		create += "akey varchar(64) NOT NULL, ";
		create += "value varchar(256) NOT NULL ";
		if (lq.configMain.useMySQL) {
			create += ", CONSTRAINT uuid_akey PRIMARY KEY (uuid,akey)";
		} else {
			create += ", UNIQUE(uuid,akey) ON CONFLICT REPLACE";
		}
		create += " );";
		if (lq.configMain.logSQL) {
			lq.debug.fine(create);
		}
		try {
			makeOpen();
			r = dbconn.query(create);
			// lq.debug.fine(r.toString());
			r.close();
			if (!lq.configMain.useMySQL) {
				makeOpen();
				create = "CREATE UNIQUE INDEX IF NOT EXISTS uuid_key_index ON " + lq.configMain.sqlPrefix + "datastore(uuid,akey)";
				dbconn.query(create);
				r.close();
			}
		} catch (final SQLException e) {
			lq.logSevere("Error creating table '" + lq.configMain.sqlPrefix + "datastore'.");
			e.printStackTrace();
		}

	}

	public void updateCheck() {

	}

	private synchronized void writeData(final PC pc) {
		String sql;
		sql = "REPLACE INTO " + lq.configMain.sqlPrefix + "pcs (";
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
		sql = sql + pc.getHealth() + ",";
		sql = sql + pc.mana + ",";
		sql = sql + pc.karma + ",";
		sql = sql + pc.statStr + ",";
		sql = sql + pc.statDex + ",";
		sql = sql + pc.statInt + ",";
		sql = sql + pc.statWis + ",";
		sql = sql + pc.statCon + ",";
		sql = sql + pc.statChr;
		sql = sql + ");";
		if (lq.configMain.logSQL) {
			lq.debug.fine(sql);
		}

		try {
			makeOpen();
			ResultSet r = dbconn.query(sql);
			r.close();
			String sql2;

			HashMap<String, Integer> copy = pc.xpEarnt;
			for (final Map.Entry<String, Integer> entry : copy.entrySet()) {
				sql2 = "REPLACE INTO " + lq.configMain.sqlPrefix + "xpEarnt (";
				sql2 = sql2 + "uuid,player,class,xp";
				sql2 = sql2 + ") values(\"";
				sql2 = sql2 + pc.uuid.toString() + "\",\"";
				sql2 = sql2 + pc.player + "\",\"";
				sql2 = sql2 + entry.getKey().toLowerCase() + "\",";
				sql2 = sql2 + entry.getValue();
				sql2 = sql2 + ");";
				if (lq.configMain.logSQL) {
					lq.debug.fine(sql2);
				}
				makeOpen();
				r = dbconn.query(sql2);
				r.close();
			}

			HashMap<String, Integer> copy2 = pc.skillsPurchased;
			for (final Map.Entry<String, Integer> entry : copy2.entrySet()) {
				sql2 = "REPLACE INTO " + lq.configMain.sqlPrefix + "skillsBought (";
				sql2 = sql2 + "uuid, player,skillName,cost";
				sql2 = sql2 + ") values(\"";
				sql2 = sql2 + pc.uuid + "\",\"";
				sql2 = sql2 + pc.player + "\",\"";
				sql2 = sql2 + entry.getKey() + "\",\"";
				sql2 = sql2 + entry.getValue();
				sql2 = sql2 + "\");";
				if (lq.configMain.logSQL) {
					lq.debug.fine(sql2);
				}
				makeOpen();
				r = dbconn.query(sql2);
				r.close();
			}

			sql2 = "DELETE FROM " + lq.configMain.sqlPrefix + "skillsLinked ";
			sql2 = sql2 + "where uuid=\"";
			sql2 = sql2 + pc.uuid + "\"";
			sql2 = sql2 + ";";
			if (lq.configMain.logSQL) {
				lq.debug.fine(sql2);
			}
			makeOpen();
			r = dbconn.query(sql2);
			r.close();

			HashMap<Material, String> copy3 = pc.skillLinkings;
			for (final Entry<Material, String> entry : copy3.entrySet()) {
				sql2 = "REPLACE INTO " + lq.configMain.sqlPrefix + "skillsLinked (";
				sql2 = sql2 + "uuid, player,material,skillName";
				sql2 = sql2 + ") values(\"";
				sql2 = sql2 + pc.uuid + "\",\"";
				sql2 = sql2 + pc.player + "\",\"";
				sql2 = sql2 + entry.getKey().toString() + "\",\"";
				sql2 = sql2 + entry.getValue();
				sql2 = sql2 + "\");";
				if (lq.configMain.logSQL) {
					lq.debug.fine(sql2);
				}
				makeOpen();
				r = dbconn.query(sql2);
				r.close();
			}

			writeAllSkillData(pc);

		} catch (final SQLException e) {
			lq.logSevere("Error writing " + lq.configMain.sqlPrefix + "pc to database.");
			e.printStackTrace();
		}

		this.saveDataStore(pc.uuid, pc.dataStore);
	}

	/*
	 * create = "CREATE TABLE if not exists partydata("; create += "uuid varchar(36) NOT NULL, "; create +=
	 * "party varchar(64) NOT NULL, "; create += "owner INTEGER, "; create += "accepted INTEGER";
	 */
	public void saveParties(ConcurrentHashMap<UUID, Party> partyList) {
		String sql = "";
		ResultSet r;
		try {

			sql = "DELETE FROM " + lq.configMain.sqlPrefix + "partydata;";
			if (lq.configMain.logSQL) {
				lq.debug.fine(sql);
			}
			makeOpen();
			r = dbconn.query(sql);
			r.close();

			for (Entry<UUID, Party> partyinfo : partyList.entrySet()) {
				Party p = partyinfo.getValue();
				sql = "REPLACE INTO " + lq.configMain.sqlPrefix + "partydata(";
				sql = sql + "uuid,party,owner,accepted";
				sql = sql + ") values(\"";
				sql = sql + p.player.toString() + "\",";
				sql = sql + "\"" + p.partyName + "\",";
				if (p.owner) {
					sql = sql + "\"1\",";
				} else {
					sql = sql + "\"0\",";
				}
				if (p.approved) {
					sql = sql + "\"1\"";
				} else {
					sql = sql + "\"0\"";
				}
				sql = sql + ");";
				if (lq.configMain.logSQL) {
					lq.debug.fine(sql);
				}
				// System.out.print(sql);
				try {
					makeOpen();
					r = dbconn.query(sql);
					r.close();
				} catch (final SQLException e) {
					lq.logSevere("Error writing " + lq.configMain.sqlPrefix + "partydata to database.");
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			lq.logSevere("Error writing " + lq.configMain.sqlPrefix + "partydata sets to database.");
			e.printStackTrace();
		}
	}

	private void writeAllSkillData(PC pc) {
		HashMap<String, SkillDataStore> skillset = pc.skillSet;
		String sql = "";
		ResultSet r;
		try {

			sql = "DELETE FROM " + lq.configMain.sqlPrefix + "skilldata ";
			sql = sql + "where uuid=\"";
			sql = sql + pc.uuid + "\"";
			sql = sql + ";";
			if (lq.configMain.logSQL) {
				lq.debug.fine(sql);
			}
			makeOpen();
			r = dbconn.query(sql);
			r.close();

			for (Entry<String, SkillDataStore> skd : skillset.entrySet()) {
				writeSkillData(skd.getValue(), pc.uuid);
			}

		} catch (SQLException e) {
			lq.logSevere("Error writing " + lq.configMain.sqlPrefix + "skilldata sets to database.");
			e.printStackTrace();
		}
	}

	// //uuid, skillname, lastuse, lastuseloc, phase, ,lastargs
	private void writeSkillData(SkillDataStore sds, UUID uuid) {
		String sql;
		SerializableLocation sl = null;
		String args = StringUtils.join(sds.getlastArgs(), "¦|¦");
		sql = "REPLACE INTO " + lq.configMain.sqlPrefix + "skilldata(";
		sql = sql + "uuid,skillname,lastuse,lastuseloc,phase,lastargs";
		sql = sql + ") values(\"";
		sql = sql + uuid.toString() + "\"";
		sql = sql + ",\"" + sds.name + "\"";
		sql = sql + ",\"" + sds.getLastUse() + "\"";
		if (sds.getLastUseLoc() != null) {
			sl = new SerializableLocation(sds.getLastUseLoc());
			sql = sql + ",\"" + sl.toString() + "\"";
		} else {
			sql = sql + ",\"\"";
		}
		sql = sql + ",\"" + sds.getPhase().toString() + "\"";
		sql = sql + ",\"" + args + "\"";
		sql = sql + ");";
		if (lq.configMain.logSQL) {
			lq.debug.fine(sql);
		}
		// System.out.print(sql);
		try {
			makeOpen();
			ResultSet r = dbconn.query(sql);
			r.close();
		} catch (final SQLException e) {
			lq.logSevere("Error writing " + lq.configMain.sqlPrefix + "skilldata to database.");
			e.printStackTrace();
		}
	}

	private void writeHealthData(HealthStore hs) {
		String sql;
		sql = "REPLACE INTO " + lq.configMain.sqlPrefix + "otherHealth(";
		sql = sql + "uuid,player,health,maxhealth";
		sql = sql + ") values(\"";
		sql = sql + hs.getUuid().toString() + "\",\"";
		sql = sql + "\",\"";
		sql = sql + hs.getHealth() + "\",\"";
		sql = sql + hs.getMaxhealth() + "\"";
		sql = sql + ");";
		if (lq.configMain.logSQL) {
			lq.debug.fine(sql);
		}

		try {
			makeOpen();
			ResultSet r = dbconn.query(sql);
			r.close();
		} catch (final SQLException e) {
			lq.logSevere("Error writing " + lq.configMain.sqlPrefix + "otherhealth to database.");
			e.printStackTrace();
		}
	}

	public synchronized HashMap<String, String> getDataStore(UUID uuid) {
		HashMap<String, String> dataStore = new HashMap<String, String>();
		String sql = "";
		try {
			makeOpen();
			sql = "SELECT akey,value FROM " + lq.configMain.sqlPrefix + "datastore WHERE uuid='" + uuid.toString() + "';";
			final ResultSet r = dbconn.query(sql);
			if (r != null) {
				while (r.next()) {
					dataStore.put(r.getString("akey"), r.getString("value"));
				}
			}
		} catch (final SQLException e) {
			lq.logSevere("Error reading " + lq.configMain.sqlPrefix + "datastore from database.");
			e.printStackTrace();
		}
		return dataStore;
	}

	public synchronized void saveDataStore(UUID uuid, HashMap<String, String> dataStore) {
		String sql = "";
		ResultSet r;
		try {

			sql = "DELETE FROM " + lq.configMain.sqlPrefix + "datastore WHERE uuid='" + uuid.toString() + "';";
			if (lq.configMain.logSQL) {
				lq.debug.fine(sql);
			}
			makeOpen();
			r = dbconn.query(sql);
			r.close();
			for (Entry<String, String> dataInfo : dataStore.entrySet()) {
				String key = dataInfo.getKey();
				String value = dataInfo.getValue();
				key = StringEscapeUtils.escapeSql(key);
				value = StringEscapeUtils.escapeSql(value);
				sql = "REPLACE INTO " + lq.configMain.sqlPrefix + "datastore(";
				sql = sql + "uuid,akey,value";
				sql = sql + ") values(\"";
				sql = sql + uuid.toString() + "\",";
				sql = sql + "\"" + key + "\",";
				sql = sql + "\"" + value + "\"";
				sql = sql + ");";
				if (lq.configMain.logSQL) {
					lq.debug.fine(sql);
				}
				// System.out.print(sql);
				try {
					makeOpen();
					r = dbconn.query(sql);
					r.close();
				} catch (final SQLException e) {
					lq.logSevere("Error writing " + lq.configMain.sqlPrefix + "datastore to database.");
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			lq.logSevere("Error writing " + lq.configMain.sqlPrefix + "datastore sets to database.");
			e.printStackTrace();
		}
	}

}