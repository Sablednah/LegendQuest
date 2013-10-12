package me.sablednah.legendquest.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.sablednah.legendquest.Main;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class DataConfig extends Config {

    public boolean whitelistItemPriority = true;
    private final Map<String, Object> dataSetHolder;
    public HashMap<String, List<Material>> dataSets = new HashMap<String, List<Material>>();

    @SuppressWarnings("unchecked")
    public DataConfig(final Main p) {
        super(p, "data.yml");

        this.whitelistItemPriority = this.getConfigItem("whitelistItemPriority", this.whitelistItemPriority);

        final ConfigurationSection dataSetList = this.getConfigItem("dataSets");

        dataSetHolder = dataSetList.getValues(false);

        final Iterator<Entry<String, Object>> entries = dataSetHolder.entrySet().iterator();
        while (entries.hasNext()) {
            final Entry<String, Object> entry = entries.next();
            List<String> data = (List<String>)entry.getValue();
            List<Material> newdata = new ArrayList<Material>();
            
            lq.debug.fine("Data: " + entry.getKey() + " - " + data);
            for (String dat : data) {
                newdata.add(Material.matchMaterial(dat));
            }
            dataSets.put(entry.getKey(), newdata);
        }
    }
}
