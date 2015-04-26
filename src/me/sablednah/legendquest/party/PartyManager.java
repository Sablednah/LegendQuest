package me.sablednah.legendquest.party;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.utils.SafeLoc;

public class PartyManager {

	public Main								lq;
	public ConcurrentHashMap<UUID, Party>	partyList	= new ConcurrentHashMap<UUID, Party>();

	public PartyManager(Main lq) {
		this.lq = lq;
		partyList = lq.datasync.loadParties();
	}

	public void saveAll() {
		lq.datasync.saveParties(partyList);
	}

	public Party getParty(UUID uniqueId) {
		return partyList.get(uniqueId);
	}

	public Party getParty(Player p) {
		return partyList.get(p.getUniqueId());
	}

	public String getPartyName(Player p) {
		return partyList.get(p.getUniqueId()).partyName;
	}

	public String getPartyName(UUID uniqueId) {
		return partyList.get(uniqueId).partyName;
	}

	public Party addParty(UUID uniqueId, Party party) {
		lq.datasync.dirtyPartyData = true;
		return partyList.put(uniqueId, party);
	}

	public Party getPartyByName(String party) {
		Party p = null;
		for (Map.Entry<UUID, Party> e : partyList.entrySet()) {
			if (e.getValue().partyName.equalsIgnoreCase(party)) {
				if (e.getValue().owner || p == null) {
					p = e.getValue();
				}
			}
		}
		return p;
	}

	public UUID getPartyOwner(String party) {
		UUID id = null;
		for (Map.Entry<UUID, Party> e : partyList.entrySet()) {
			if (e.getValue().partyName.equalsIgnoreCase(party)) {
				if (e.getValue().owner) {
					id = e.getValue().player;
				}
			}
		}
		return id;
	}

	public void removeParty(UUID uniqueId) {
		partyList.remove(uniqueId);
		lq.datasync.dirtyPartyData = true;
	}

	public boolean hasParty(Player p) {
		return partyList.get(p.getUniqueId()) != null;
	}

	public List<Player> getPartyMembers(Player p) {
		if (hasParty(p)) {
			return getPartyMembers(getPartyName(p), p.getLocation());
		}
		return null;
	}

	public List<Player> getPartyMembers(String party, Player p) {
		return getPartyMembers(party, p.getLocation());
	}

	public List<Player> getPartyMembers(String party, Location l) {
		double distance = lq.configMain.partyRange * lq.configMain.partyRange;
		List<Player> partyMembers = new ArrayList<Player>();
		for (Map.Entry<UUID, Party> e : partyList.entrySet()) {
			if (e.getValue().partyName.equalsIgnoreCase(party)) {
				Player p = lq.getServer().getPlayer(e.getKey());
				if (p != null) {
					if (l.getWorld().getName().equals(p.getLocation().getWorld().getName())) {
						if (l.distanceSquared(p.getLocation()) <= distance) {
							partyMembers.add(p);
						}
					}
				}
			}
		}
		return partyMembers;
	}

	public Location getPartyLocation(String party, Player searcher) {
		// get everyone in the party
		List<Player> partyMembers = new ArrayList<Player>();
		for (Map.Entry<UUID, Party> e : partyList.entrySet()) {
			if (e.getValue().partyName.equalsIgnoreCase(party)) {
				Player p = lq.getServer().getPlayer(e.getKey());
				if (p != null && p.getUniqueId() != searcher.getUniqueId()) {
					partyMembers.add(p);
				}
			}
		}
		// check size : if =0 then its solo party - return players own lcation!
		if (partyMembers.size() < 1) {
			return searcher.getLocation();
		}
		// check size : if =1 then its solo party - return the other player
		if (partyMembers.size() == 1) {
			return partyMembers.get(0).getLocation();
		}
		// now see how many people are nearby each player to get the "Bulk" of the party

		int biggest = 0;
		List<Player> bigplayer = null;
		for (Player pl : partyMembers) {
			List<Player> pm2 = getPartyMembers(pl);
			if (pm2 != null) {
				if (pm2.size() > biggest) {
					biggest = pm2.size();
					bigplayer = pm2;
				}
			}
		}
		if (bigplayer != null && bigplayer.size() > 0) {
			double xs = 0.0D;
			double ys = 0.0D;
			double zs = 0.0D;
			for (Player pp : bigplayer) {
				xs += pp.getLocation().getX();
				ys += pp.getLocation().getY();
				zs += pp.getLocation().getZ();
			}
			// get average
			xs = xs / bigplayer.size();
			ys = ys / bigplayer.size();
			zs = zs / bigplayer.size();
			Location l = new Location(searcher.getWorld(), xs, ys, zs);
			Location safe = SafeLoc.getSafeLocation(l);
			if (safe != null) {
				return safe;
			} else {
				searcher.sendMessage(lq.configLang.notSafeTeleport);
			}
		}

		// we failed
		return searcher.getLocation();
	}
}
