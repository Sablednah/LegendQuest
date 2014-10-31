package me.sablednah.legendquest.cmds;

import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.party.Party;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdParty extends CommandTemplate implements CommandExecutor {

	public Main	lq;

	public CmdParty(Main p) {
		this.lq = p;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// get the enum for this command
		final Cmds cmd = Cmds.valueOf("PARTY");
		if (!validateCmd(lq, cmd, sender, args)) {
			return true;
		}

		// from here on is command specific code.

		// reject non-player senders e.g. chats bots etc
		if (!(sender instanceof Player)) {
			return false;
		}

		// only players left here
		Player p = (Player) sender;

		if (!lq.validWorld(p.getWorld().getName())) {
			p.sendMessage(lq.configLang.invalidWorld);
			return true;
		}

		// PC pc = lq.players.getPC(p);

		if (args.length < 1) { // why am i worried about negative argument length ? le-sigh
			Party pty = lq.partyManager.getParty(p.getUniqueId());
			if (pty == null) {
				p.sendMessage("You are not in a party. /party create [name] to create one. /party join [name] to request joining an existing party.");
				return true;
			} else {
				if (pty.approved) {
					if (pty.owner) {
						p.sendMessage("You are leader of: " + pty.partyName);
						return true;
					} else {
						p.sendMessage("You are a member of: " + pty.partyName);
						return true;
					}
				} else {
					p.sendMessage("Awaiting approval for request to join: " + pty.partyName);
					return true;
				}
			}
		} else {
			if (args.length < 2) { // why am i worried about negative argument length ? le-sigh
				p.sendMessage(lq.configLang.invalidArgumentsCommand);
				return true;
			} else {
				String action = args[0].toLowerCase();
				String target = args[1];
				if (action.equalsIgnoreCase("create")) {
					Party pty = lq.partyManager.getPartyByName(target);
					if (pty != null) {
						p.sendMessage(target + " already exists.  Select a different name.");
						return true;
					} else {
						pty = new Party(target, p.getUniqueId(), true, true);
						lq.partyManager.addParty(p.getUniqueId(), pty);
						p.sendMessage(target + ": party created.");
						lq.datasync.dirtyPartyData = true;
						return true;
					}
				} else if (action.equalsIgnoreCase("join") || action.equalsIgnoreCase("request")) {
					Party tpat = lq.partyManager.getPartyByName(target);
					if (tpat==null) {
						p.sendMessage(target + ": party does not exist.");
						return true;						
					} else {
						if (tpat.player.equals(p.getUniqueId())) {
							p.sendMessage("you can't join your own party!");
							return true;							
						}
					}
					Party pty = lq.partyManager.getParty(p.getUniqueId());
					if (pty!=null && pty.approved && pty.partyName.equalsIgnoreCase(target)) {
						p.sendMessage("You're already in: "+target);
						return true;
					}
					if (pty != null) {
						lq.partyManager.removeParty(p.getUniqueId());
					}
					pty = new Party(target, p.getUniqueId(), false, false);
					lq.partyManager.addParty(pty.player, pty);
					UUID owner = lq.partyManager.getPartyOwner(target);
					Player tp = lq.getServer().getPlayer(owner);
					if (tp!=null) {
						tp.sendMessage(p.getDisplayName() + " has requested to join your party: " + target);
					}
					p.sendMessage("Party request sent.");
					lq.datasync.dirtyPartyData = true;
					return true;
				} else if (action.equalsIgnoreCase("approve") || action.equalsIgnoreCase("accept") || action.equalsIgnoreCase("allow")) {
					Party pty = lq.partyManager.getParty(p.getUniqueId());
					if (pty.owner) {
						@SuppressWarnings("deprecation")
						Player tp = lq.getServer().getPlayer(target);
						Party pty2 = lq.partyManager.getParty(tp.getUniqueId());
						if (pty.partyName.equalsIgnoreCase(pty2.partyName)) {
							pty2.approved = true;
							lq.partyManager.addParty(pty2.player, pty2);
							tp.sendMessage(p.getDisplayName() + " has approved your request to join your party: " + target);
							p.sendMessage(p.getDisplayName() + " party request approved");
							lq.datasync.dirtyPartyData = true;
						} else {
							p.sendMessage(target + " has not requested to join your party.");
						}
						return true;
					} else {
						p.sendMessage("You do not lead your party, you can't approve.");
						return true;
					}
				} else {
					p.sendMessage(lq.configLang.invalidArgumentsCommand);
					return true;
				}
			}
		}
	}
}
