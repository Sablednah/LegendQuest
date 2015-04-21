package me.sablednah.legendquest.utils.plugins;

//import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.sablednah.legendquest.Main;

public class PlaceholderAPIClass implements Listener {
	public Main	lq;

	public PlaceholderAPIClass(final Main lq) {
		this.lq = lq;

		/**
		 * When a placeholder hook is registered, DeluxeChat will request a value based on the placeholder identifier
		 * for the specific plugin
		 * 
		 * placeholders are automatically registered in a specific format.
		 * 
		 * format: %<pluginname>_<Itentifier>% the plugin name will always be forced to lower case example:
		 * %deluxetags_tag%
		 * 
		 * When the onPlaceholderRequest is called for your plugin, you can simply return the value specific to the
		 * placeholder identifier requiring a value for the player.
		 */
		boolean registered = PlaceholderAPI.registerPlaceholderHook((Plugin) lq, new PlaceholderHook() {
			@Override
			public String onPlaceholderRequest(Player player, String id) {
				return PlaceholderString.getSting(player,id, lq);
			}
		});

		if (registered) {
			lq.log("PlaceholderAPI handler registered!");
		}

	}

}
