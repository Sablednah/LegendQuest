package me.sablednah.legendquest.races;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.utils.Pair;
import me.sablednah.legendquest.utils.Utils;
import me.sablednah.legendquest.utils.WeightedProbMap;

public class Races {
	public Main									lq;
	private Map<String, Race>					races			= new HashMap<String, Race>();
	private ArrayList<Pair<Integer, String>>	raceprobability	= new ArrayList<Pair<Integer, String>>();
	public WeightedProbMap<String>				wpmRaces;

	public Race									defaultRace;

	@SuppressWarnings("unchecked")
	public Races(Main p) {
		this.lq = p;

		// Find the Races folder
		File raceDir = new File(lq.getDataFolder() + File.separator + "races");
		// notify sanning begun
		lq.log(lq.configLang.raceScan + ": " + raceDir);

		// make it if not found
		if (!raceDir.exists()) {
			raceDir.mkdir();
		}

		File[] racefiles = raceDir.listFiles();

		for (File race : racefiles) {
			// only want .yml configs here baby
			if (race.isFile() && race.getName().toLowerCase().endsWith(".yml")) {
				// found a config file - load it in time

				lq.log(lq.configLang.raceScanFound + race.getName());
				Race r = new Race();
				Boolean validConfig = true;
				YamlConfiguration thisConfig = null;

				// begin parsing config file
				try {
					thisConfig = YamlConfiguration.loadConfiguration(race);

					r.filename = race.getName();
					r.name = thisConfig.getString("name");
					r.frequency = thisConfig.getInt("frequency");

					r.plural = thisConfig.getString("plural");
					r.size = thisConfig.getDouble("size");
					r.defaultRace = thisConfig.getBoolean("default");
					r.statStr = thisConfig.getInt("statmods.str");
					r.statDex = thisConfig.getInt("statmods.dex");
					r.statInt = thisConfig.getInt("statmods.int");
					r.statWis = thisConfig.getInt("statmods.wis");
					r.statCon = thisConfig.getInt("statmods.con");
					r.statChr = thisConfig.getInt("statmods.chr");
					r.baseHealth = thisConfig.getInt("basehealth");
					r.baseMana = thisConfig.getInt("baseMana");
					r.manaPerSecond = thisConfig.getInt("manaPerSecond");
					
					r.perm = thisConfig.getString("perm");

					List<String> groups = (List<String>) thisConfig.getList("groups");
					for (int i = 0; i < groups.size(); i++) {
						groups.set(i, groups.get(i).toLowerCase());
					}
					r.groups = groups;
					
					
					// allowed lists

					List<String> stringList;
					List<Integer> intList;
					String keyName;
					stringList = (List<String>) thisConfig.getList("allowedTools");
					intList = new ArrayList<Integer>();
					if (stringList != null) {
						for (int i = 0; i < stringList.size(); i++) {
							keyName = stringList.get(i).toLowerCase();
							if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
								keyName = "tools";
							}
							if (!keyName.equalsIgnoreCase("none")) {
								if (lq.configData.dataSets.containsKey(keyName)) {
									intList.addAll(lq.configData.dataSets.get(keyName));
								} else if (Material.matchMaterial(keyName) != null) {
									intList.add(Material.matchMaterial(keyName).getId());
								} else if (Utils.isParsableToInt(keyName)) {
									intList.add(Integer.parseInt(keyName));
								} else {
									lq.debug.error("Allowed tool '" + keyName + "' in " + r.filename + " not understood");
								}
							}
						}
					}
					r.allowedTools = intList;

					stringList = (List<String>) thisConfig.getList("allowedArmour");
					intList = new ArrayList<Integer>();
					if (stringList != null) {
						for (int i = 0; i < stringList.size(); i++) {
							keyName = stringList.get(i).toLowerCase();
							if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
								keyName = "armour";
							}
							if (!keyName.equalsIgnoreCase("none")) {
								if (lq.configData.dataSets.containsKey(keyName)) {
									intList.addAll(lq.configData.dataSets.get(keyName));
								} else if (Material.matchMaterial(keyName) != null) {
									intList.add(Material.matchMaterial(keyName).getId());
								} else if (Utils.isParsableToInt(keyName)) {
									intList.add(Integer.parseInt(keyName));
								} else {
									lq.debug.error("Allowed Armour '" + keyName + "' in " + r.filename + " not understood");
								}
							}
						}
					}
					r.allowedArmour = intList;

					stringList = (List<String>) thisConfig.getList("allowedWeapons");
					intList = new ArrayList<Integer>();
					if (stringList != null) {
						for (int i = 0; i < stringList.size(); i++) {
							keyName = stringList.get(i).toLowerCase();
							if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
								keyName = "weapons";
							}
							if (!keyName.equalsIgnoreCase("none")) {
								if (lq.configData.dataSets.containsKey(keyName)) {
									intList.addAll(lq.configData.dataSets.get(keyName));
								} else if (Material.matchMaterial(keyName) != null) {
									intList.add(Material.matchMaterial(keyName).getId());
								} else if (Utils.isParsableToInt(keyName)) {
									intList.add(Integer.parseInt(keyName));
								} else {
									lq.debug.error("Allowed Weapons '" + keyName + "' in " + r.filename + " not understood");
								}
							}
						}
					}
					r.allowedWeapons = intList;

					// build disallowed lists

					stringList = (List<String>) thisConfig.getList("dissallowedTools");
					intList = new ArrayList<Integer>();
					if (stringList != null) {
						for (int i = 0; i < stringList.size(); i++) {
							keyName = stringList.get(i).toLowerCase();
							if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
								keyName = "tools";
							}
							if (!keyName.equalsIgnoreCase("none")) {
								if (lq.configData.dataSets.containsKey(keyName)) {
									intList.addAll(lq.configData.dataSets.get(keyName));
								} else if (Material.matchMaterial(keyName) != null) {
									intList.add(Material.matchMaterial(keyName).getId());
								} else if (Utils.isParsableToInt(keyName)) {
									intList.add(Integer.parseInt(keyName));
								} else {
									lq.debug.error("Dissallowed tool '" + keyName + "' in " + r.filename + " not understood");
								}
							}
						}
					}
					r.dissallowedTools = intList;

					stringList = (List<String>) thisConfig.getList("dissallowedArmour");
					intList = new ArrayList<Integer>();
					if (stringList != null) {
						for (int i = 0; i < stringList.size(); i++) {
							keyName = stringList.get(i).toLowerCase();
							if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
								keyName = "armour";
							}
							if (!keyName.equalsIgnoreCase("none")) {
								if (lq.configData.dataSets.containsKey(keyName)) {
									intList.addAll(lq.configData.dataSets.get(keyName));
								} else if (Material.matchMaterial(keyName) != null) {
									intList.add(Material.matchMaterial(keyName).getId());
								} else if (Utils.isParsableToInt(keyName)) {
									intList.add(Integer.parseInt(keyName));
								} else {
									lq.debug.error("Dissallowed Armour '" + keyName + "' in " + r.filename + " not understood");
								}
							}
						}
					}
					r.dissallowedArmour = intList;

					stringList = (List<String>) thisConfig.getList("dissallowedWeapons");
					intList = new ArrayList<Integer>();
					if (stringList != null) {
						for (int i = 0; i < stringList.size(); i++) {
							keyName = stringList.get(i).toLowerCase();
							if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
								keyName = "weapons";
							}
							if (!keyName.equalsIgnoreCase("none")) {
								if (lq.configData.dataSets.containsKey(keyName)) {
									intList.addAll(lq.configData.dataSets.get(keyName));
								} else if (Material.matchMaterial(keyName) != null) {
									intList.add(Material.matchMaterial(keyName).getId());
								} else if (Utils.isParsableToInt(keyName)) {
									intList.add(Integer.parseInt(keyName));
								} else {
									lq.debug.error("Dissallowed Weapons '" + keyName + "' in " + r.filename + " not understood");
								}
							}
						}
					}
					r.dissallowedWeapons = intList;

					
					
				} catch (Exception e) {
					validConfig = false;
					lq.log(lq.configLang.raceScanInvalid + race.getName());
					e.printStackTrace();
				}

				if (validConfig) {
					raceprobability.add(new Pair<Integer, String>(r.frequency, r.name));
					races.put(r.name.toLowerCase(), r);
					if (r.defaultRace) {
						defaultRace = r;
					}
				}
			}
		}

		wpmRaces = new WeightedProbMap<String>(raceprobability);

		// notify scanning ended
		lq.log(lq.configLang.raceScanEnd);

		if (defaultRace == null) {
			lq.log(lq.configLang.raceNoDefault);
			// set "default" to first found to stop breaking...
			defaultRace = races.entrySet().iterator().next().getValue();
		}
	}

	public boolean raceExists(String racename) {
		return races.containsKey(racename.toLowerCase());
	}

	public boolean groupExists(String groupname) {
		Collection<Race> racelist = races.values();
		for (Race race : racelist) {
			if (race.groups.contains(groupname.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public Map<String, Race> getRaces() {
		return races;
	}
	
	public Race getRace(String raceName) {
		return races.get(raceName.toLowerCase());
	}
	
}
