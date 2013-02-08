package me.sablednah.legendquest.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;

import me.sablednah.legendquest.Main;

public class DataConfig extends Config {

	public boolean							whitelistItemPriority	= true;
	private Map<String, Object>				dataSetHolder;
	public HashMap<String, List<Integer>>	dataSets = new HashMap<String, List<Integer>>();

	@SuppressWarnings("unchecked")
	public DataConfig(Main p) {
		super(p, "data.yml");

		this.whitelistItemPriority = this.getConfigItem("whitelistItemPriority", this.whitelistItemPriority);

		ConfigurationSection dataSetList = this.getConfigItem("dataSets");

		dataSetHolder = dataSetList.getValues(false);

		Iterator<Entry<String, Object>> entries = dataSetHolder.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, Object> entry = entries.next();
			lq.debug.fine("Data: "+entry.getKey() + " - " + (List<Integer>) entry.getValue());
			dataSets.put(entry.getKey(), (List<Integer>) entry.getValue());
		}
	}
}
