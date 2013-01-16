package me.sablednah.legendquest.races;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.utils.Pair;
import me.sablednah.legendquest.utils.WeightedProbMap;

public class Races {
	public Main									lq;
	public Map<String, Race>					races			= new HashMap<String, Race>();
	private ArrayList<Pair<Integer, String>>	raceprobability	= new ArrayList<Pair<Integer, String>>();
	public WeightedProbMap<String>				wpmRaces;

	public Race									defaultRace;

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

					@SuppressWarnings("unchecked")
					List<String> groups = (List<String>) thisConfig.getList("groups");
					for (int i = 0; i < groups.size(); i++) {
						groups.set(i, groups.get(i).toLowerCase());
					}
					r.groups = groups;

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
}
