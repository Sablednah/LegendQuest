package me.sablednah.legendquest.classes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.races.Race;
import me.sablednah.legendquest.utils.Pair;
import me.sablednah.legendquest.utils.WeightedProbMap;

public class Classes {
	public Main									lq;
	private Map<String, ClassType>				classTypes			= new HashMap<String, ClassType>();
	private ArrayList<Pair<Integer, String>>	classprobability	= new ArrayList<Pair<Integer, String>>();
	public WeightedProbMap<String>				wpmClasses;

	public ClassType							defaultClass;

	@SuppressWarnings("unchecked")
	public Classes(Main p) {
		this.lq = p;

		// Find the Classes folder
		File classDir = new File(lq.getDataFolder() + File.separator + "classes");
		// notify sanning begun
		lq.log(lq.configLang.classScan + ": " + classDir);

		// make it if not found
		if (!classDir.exists()) {
			classDir.mkdir();
		}

		File[] classfiles = classDir.listFiles();

		for (File classfile : classfiles) {
			// only want .yml configs here baby
			if (classfile.isFile() && classfile.getName().toLowerCase().endsWith(".yml")) {
				// found a config file - load it in time

				lq.log(lq.configLang.classScanFound + classfile.getName());
				ClassType c = new ClassType();
				Boolean validConfig = true;
				YamlConfiguration thisConfig = null;

				// begin parsing config file
				try {
					thisConfig = YamlConfiguration.loadConfiguration(classfile);

					c.filename = classfile.getName();
					c.name = thisConfig.getString("name");

					// c.size = (float) thisConfig.getDouble("size", 1.0);
					// c.speed = thisConfig.getDouble("speed", 1.0);
					// c.xp = thisConfig.getInt("xp", 0);
					/*
					 * @SuppressWarnings("unchecked") List<String> effects = (List<String>)
					 * thisConfig.getList("effects"); if (effects != null) { for (String thisEffect : effects) { //
					 * ZombieMod.logger.info("["+ZombieMod.myName+"] Effect: " + thisEffect); Effect x =
					 * Effect.valueOf(thisEffect); // ZombieMod.logger.info("["+ZombieMod.myName+"] x: " + x);
					 * c.effects.add(x); } }
					 */
					/*
					 * @SuppressWarnings("unchecked") List<String> abilities = (List<String>)
					 * thisConfig.getList("abilities"); c.abilities = abilities;
					 */
					c.frequency = thisConfig.getInt("frequency");

					List<String> allowedRaces = (List<String>) thisConfig.getList("allowedRaces");
					for (int i = 0; i < allowedRaces.size(); i++) {
						allowedRaces.set(i, allowedRaces.get(i).toLowerCase());
					}
					c.allowedRaces = allowedRaces;

					List<String> allowedGroups = (List<String>) thisConfig.getList("allowedGroups");
					if (allowedGroups != null) {
						for (int i = 0; i < allowedGroups.size(); i++) {
							allowedGroups.set(i, allowedGroups.get(i).toLowerCase());
						}
					}
					c.allowedGroups = allowedGroups;

					List<String> requiresOne = (List<String>) thisConfig.getList("requiresOne");
					if (requiresOne != null) {
						for (int i = 0; i < requiresOne.size(); i++) {
							requiresOne.set(i, requiresOne.get(i).toLowerCase());
						}
					}
					c.requiresOne = requiresOne;

					List<String> requires = (List<String>) thisConfig.getList("requires");
					if (requires != null) {
						for (int i = 0; i < requires.size(); i++) {
							requires.set(i, requires.get(i).toLowerCase());
						}
					}
					c.requires = requires;

					c.defaultClass = thisConfig.getBoolean("default");
					c.statStr = thisConfig.getInt("statmods.str");
					c.statDex = thisConfig.getInt("statmods.dex");
					c.statInt = thisConfig.getInt("statmods.int");
					c.statWis = thisConfig.getInt("statmods.wis");
					c.statCon = thisConfig.getInt("statmods.con");
					c.statChr = thisConfig.getInt("statmods.chr");
					c.healthPerLevel = thisConfig.getDouble("healthperlevel");

					c.perm = thisConfig.getString("perm");

					// check race or group exists.
					boolean hasRace = checkRaceList(allowedRaces);
					boolean hasGroup = checkGroupList(allowedGroups);

					if (!(hasRace || hasGroup)) {
						validConfig = false;
						lq.log(lq.configLang.classScanNoRaceOrGroup + classfile.getName());
					}

				} catch (Exception e) {
					validConfig = false;
					lq.logSevere(lq.configLang.classScanInvalid + classfile.getName());
					e.printStackTrace();
				}

				if (validConfig) {
					classprobability.add(new Pair<Integer, String>(c.frequency, c.name));
					classTypes.put(c.name.toLowerCase(), c);
					if (c.defaultClass) {
						defaultClass = c;
					}
				}
			}
		}
		wpmClasses = new WeightedProbMap<String>(classprobability);

		// notify sanning ended
		lq.log(lq.configLang.classScanEnd);
		if (defaultClass == null) {
			lq.log(lq.configLang.classNoDefault);
			// set "default" to first found to stop breaking...
			defaultClass = classTypes.entrySet().iterator().next().getValue();
		}
	}

	public boolean checkRaceList(List<String> allowedRaces) {
		if (allowedRaces == null) {
			return false;
		}

		int racecount = 0;
		for (String r : allowedRaces) {
			if (r.equalsIgnoreCase("all") || r.equalsIgnoreCase("any")) {
				return true;
			}
			if (lq.races.raceExists(r)) {
				racecount++;
			} else {
				lq.logWarn(lq.configLang.classScanRaceWarning + r);
			}
		}
		if (racecount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkGroupList(List<String> allowedGroups) {
		if (allowedGroups == null) {
			return false;
		}
		int groupcount = 0;
		for (String g : allowedGroups) {
			if (g.equalsIgnoreCase("all") || g.equalsIgnoreCase("any")) {
				return true;
			}
			if (lq.races.groupExists(g)) {
				groupcount++;
			} else {
				lq.logWarn(lq.configLang.classScanGroupWarning + g);
			}
		}
		if (groupcount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public List<String> getClasses(String raceName, Player player) {

		Race race = lq.races.getRace(raceName);
		if (race != null) {
			List<String> groups = race.groups;
			List<String> result = new ArrayList<String>();
			PC pc = lq.players.getPC(player);
			for (ClassType c : classTypes.values()) {
				if (player != null) {
					if (!(c.perm == null || c.perm.equalsIgnoreCase("") || player.isPermissionSet(c.perm))) {
						continue;
					}
				}
				boolean raceCheck = false;
				if ((c.allowedRaces.contains(raceName.toLowerCase()) || c.allowedRaces.contains("all") || c.allowedRaces.contains("any"))) {
					raceCheck = true;
				} else {
					// check if groups and allowed groups have any common elements
					if (groups != null && c.allowedGroups != null) {
						if (!Collections.disjoint(groups, c.allowedGroups) || c.allowedGroups.contains("all") || c.allowedGroups.contains("any")) {
							raceCheck = true;
						}
					}
				}
				if (!raceCheck) {
					continue;
				}
				if (c.requires != null) {
					boolean allValid = true;
					for (String required : c.requires) {
						if (!pc.hasMastered(required)) {
							allValid = false;
						}
						lq.logger.info("req: "+required  + " - " + pc.hasMastered(required));
					}
					// not mastered all required - skip me!!
					if (!allValid) {
						continue;
					}
				}
				if (c.requiresOne != null) {
					boolean oneValid = false;
					for (String requested : c.requiresOne) {
						if (pc.hasMastered(requested)) {
							oneValid = true;
						}
						lq.logger.info("requested: "+requested  + " - " + pc.hasMastered(requested));
					}

					// if one is mastered allow
					if (!oneValid) {
						continue;
					}
				}
				
				result.add(c.name.toLowerCase());
			}
			return result;
		} else {
			return null;
		}
	}

	public List<String> getClasses(String raceName) {
		return getClasses(raceName, null);
	}

	public Map<String, ClassType> getClassTypes() {
		return classTypes;
	}

	public ClassType getClass(String className) {
		return classTypes.get(className.toLowerCase());
	}
}
