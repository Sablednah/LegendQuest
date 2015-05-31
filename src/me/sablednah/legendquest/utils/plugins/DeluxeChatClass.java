package me.sablednah.legendquest.utils.plugins;

//import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import me.clip.deluxechat.placeholders.DeluxePlaceholderHook;
import me.clip.deluxechat.placeholders.PlaceholderHandler;
import me.clip.deluxechat.events.PlaceholderHookUnloadEvent;

import me.sablednah.legendquest.Main;

public class DeluxeChatClass implements Listener {
	public Main	lq;

	public DeluxeChatClass(final Main lq) {
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
		boolean registered = PlaceholderHandler.registerPlaceholderHook((Plugin) lq, new DeluxePlaceholderHook() {
			@Override
			public String onPlaceholderRequest(Player player, String id) {
				return PlaceholderString.getSting(player,id, lq);
			}
		});

		if (registered) {
			lq.log("DeluxeChat placeholder handler registered!");
		}

	}
	
	@EventHandler
    public void onPlaceholderUnload(PlaceholderHookUnloadEvent e) {
      
        if (e.getPluginName().equalsIgnoreCase("the plugin name you register your placeholder with")) {
            e.setCancelled(true);
        }
    }

}
