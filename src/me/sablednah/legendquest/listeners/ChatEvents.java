package me.sablednah.legendquest.listeners;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvents implements Listener {

	public Main	lq;

	public ChatEvents(final Main p) {
		this.lq = p;
	}
/*
	@EventHandler(priority = EventPriority.LOWEST)
	public void chatProcessFirst(AsyncPlayerChatEvent event) {		
		if (lq.configMain.chatUsePrefix) {
			String format = event.getFormat();
			System.out.print(format);
			if (lq.configMain.chatPrefix.contains("{current}")) {
				format = lq.configMain.chatPrefix.replace("{current}",format);
			} else {
				format = lq.configMain.chatPrefix + format; 
			}
			System.out.print(format);
			event.setFormat(format);
		}
		
	}
*/	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void chatProcessLast(AsyncPlayerChatEvent event) {

		String format = event.getFormat();
		if (lq.configMain.chatUsePrefix) {
//			System.out.print(format);
			if (lq.configMain.chatPrefix.contains("{current}")) {
				format = lq.configMain.chatPrefix.replace("{current}",format);
			} else {
				format = lq.configMain.chatPrefix + format; 
			}
//			System.out.print(format);
			event.setFormat(format);
		}
	
		if (lq.configMain.chatProcessPrefix) {
//			System.out.print(format);

			String racename = "";
			String classname = "";
			
			PC pc = lq.players.getPC(event.getPlayer());
			racename = pc.race.name;
			classname = pc.mainClass.name;
			
			format = format.replace("{race}", racename);
			format = format.replace("{class}", classname);

//			System.out.print(format);
			
			event.setFormat(format);
		}
	}	
}
