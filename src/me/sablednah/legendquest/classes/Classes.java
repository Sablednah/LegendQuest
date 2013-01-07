package me.sablednah.legendquest.classes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.utils.Pair;
import me.sablednah.legendquest.utils.WeightedProbMap;

public class Classes {
	public Main									lq;
	public Map<String, ClassType>				classTypes			= new HashMap<String, ClassType>();
	private ArrayList<Pair<Integer, String>>	classprobability	= new ArrayList<Pair<Integer, String>>();
	public WeightedProbMap<String>				wpmClasses;

	@SuppressWarnings("unchecked")
	public Classes(Main p) {
		this.lq = p;

		// Find the Classes folder
		File classDir = new File(lq.getDataFolder() + File.separator + "classes");
		// notify sanning begun
		lq.logger.info(lq.configLang.classScan + ": " + classDir);

		// make it if not found
		if (!classDir.exists()) {
			classDir.mkdir();
		}

		File[] classfiles = classDir.listFiles();

		for (File classfile : classfiles) {
			// only want .yml configs here baby
			if (classfile.isFile() && classfile.getName().toLowerCase().endsWith(".yml")) {
				// found a config file - load it in time

				lq.logger.info(lq.configLang.classScanFound + classfile.getName());
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
					c.allowedRaces = allowedRaces;

					List<String> allowedGroups = (List<String>) thisConfig.getList("allowedGroups");
					c.allowedGroups = allowedGroups;

					// check race or group exists.
					boolean hasRace = checkRaceList(allowedRaces);
					boolean hasGroup = checkGroupList(allowedGroups);

					if (!(hasRace || hasGroup)) {
						validConfig = false;
						lq.logger.severe(lq.configLang.classScanNoRaceOrGroup + classfile.getName());
					}

				} catch (Exception e) {
					validConfig = false;
					lq.logger.severe(lq.configLang.classScanInvalid + classfile.getName());
					e.printStackTrace();
				}

				if (validConfig) {
					classprobability.add(new Pair<Integer, String>(c.frequency, c.name));
					classTypes.put(c.name, c);
				}
			}
		}
		wpmClasses = new WeightedProbMap<String>(classprobability);

		// notify sanning ended
		lq.logger.info(lq.configLang.classScanEnd);
	}

	public boolean checkRaceList(List<String> allowedRaces) {
		int racecount = 0;
		for (String r : allowedRaces) {
			if (lq.races.raceExists(r)) {
				racecount++;
			} else {
				lq.logger.warning(lq.configLang.classScanRaceWarning + r);
			}
		}
		if (racecount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkGroupList(List<String> allowedGroups) {
		int groupcount = 0;
		for (String g : allowedGroups) {
			if (lq.races.groupExists(g)) {
				groupcount++;
			} else {
				lq.logger.warning(lq.configLang.classScanGroupWarning + g);
			}
		}
		if (groupcount > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public List<String> getClasses(String raceName) {
		List<String> groups = lq.races.races.get(raceName).groups;
		List<String> result = new ArrayList<String>();
		for (ClassType c : classTypes.values()){
			if (c.allowedRaces.contains(raceName)) {
				result.add(c.name);
			} else {
				//check if groups and allowed groups have any common elements
				if (!Collections.disjoint(groups, c.allowedGroups)) {
					result.add(c.name);
				}
			}
		}
		return result;
	}	
}
