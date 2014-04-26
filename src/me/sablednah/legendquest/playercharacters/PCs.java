package me.sablednah.legendquest.playercharacters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.skills.Skill;
import me.sablednah.legendquest.skills.SkillDataStore;
import me.sablednah.legendquest.skills.SkillType;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class PCs {

	public Main										lq;
	public Map<UUID, PC>							activePlayers	= new HashMap<UUID, PC>();
	public HashMap<String, PermissionAttachment>	permissions		= new HashMap<String, PermissionAttachment>();

	public PCs(Main p) {
		this.lq = p;
		for (Player player : lq.getServer().getOnlinePlayers()) {
			UUID uuid = player.getUniqueId();
			PC pc = getPC(player);
			addPlayer(uuid, pc);
		}
		Bukkit.getServer().getScheduler().runTaskTimer(lq, new SkillTicker(), 10L, lq.configMain.skillTickInterval);
	}

	public void addPlayer(UUID uuid, PC pc) {
		activePlayers.put(uuid, pc);
	}

	public PC getPC(OfflinePlayer p) {
		if (p != null) {
			return getPC(p.getUniqueId());
		}
		return null;
	}

	public PC getPC(Player p) {
		if (p != null) {
			return getPC(p.getUniqueId());
		}
		return null;
	}

	@Deprecated
	public PC getPC(String pName) {
		if (pName != null) {
			UUID uuid = Utils.getPlayerUUID(pName);
			return getPC(uuid);
		}
		return null;
	}

	public PC getPC(UUID uuid) {
		PC pc = null;
		if (uuid != null) {
			pc = activePlayers.get(uuid);
			if (pc == null) {
				pc = loadPlayer(uuid);
			}
		}
		return pc;
	}

	private PC loadPlayer(UUID uuid) {
		// Load player from disk. if not found make new blank
		PC pc = null;
		pc = lq.datasync.getData(uuid);
		if (pc == null) {
			pc = new PC(lq, uuid);
		}
		return pc;
	}

	public void removePlayer(UUID uuid) {
		savePlayer(uuid);
		Player p = lq.getServer().getPlayer(uuid);
		if (p != null) {
			Iterator<String> it = lq.players.permissions.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				PermissionAttachment val = lq.players.permissions.get(key);
				if (key.startsWith(uuid.toString())) {
					p.removeAttachment(val);
					it.remove();
				}
			}
		}
		activePlayers.remove(uuid);
	}

	public void cleanup(Player p) {
		UUID uuid = p.getUniqueId();

		lq.players.removePlayer(uuid);
	}

	public void savePlayer(PC pc) {
		lq.datasync.addWrite(pc);
	}

	public void savePlayer(UUID uuid) {
		savePlayer(getPC(uuid));
	}

	public void scheduleUpdate(UUID uuid) {
		Bukkit.getServer().getScheduler().runTaskLater(lq, new DelayedWrite(uuid), 4L);
	}

	public class DelayedWrite implements Runnable {

		public UUID	uuid;

		public DelayedWrite(UUID u) {
			this.uuid = u;
		}

		public void run() {
			savePlayer(uuid);
		}
	}

	public class SkillTicker implements Runnable {

		public void run() {
			for (PC activePlayer : activePlayers.values()) {
				Player p = lq.getServer().getPlayer(activePlayer.uuid);
				if (p != null && p.isOnline()) {
					for (SkillDataStore skill : activePlayer.skillSet.values()) {
						if (skill.type == SkillType.PASSIVE) {
							// passive skills are "always on"
							skill.isActive = true;
							activePlayer.skillSet.put(skill.name, skill);
							continue;
						}
						boolean startskill = false;
						boolean stopskill = false;
						switch (skill.checkPhase()) {
							case READY:
								if (skill.isActive == true) {
									stopskill = true;
								}
								skill.isActive = false;
								activePlayer.skillSet.put(skill.name, skill);
								break;
							case BUILDING:
								if (skill.isActive == true) {
									stopskill = true;
								}
								Location pLoc = p.getLocation();
								double distance = pLoc.distanceSquared(skill.lastUseLoc);
								double allowed = lq.configMain.skillBuildupMoveAllowed;
								allowed = allowed * allowed;
								if (distance > allowed) {
									skill.isCanceled = true;
									skill.lastUse = 0;
									activePlayer.skillSet.put(skill.name, skill);
									p.sendMessage(skill.name + " : " + lq.configLang.skillBuildupDisturbed);
								}
								break;
							case DELAYED:
								if (skill.isActive == true) {
									stopskill = true;
								}

								skill.isActive = false;
								activePlayer.skillSet.put(skill.name, skill);
								break;
							case ACTIVE:
								if (skill.isActive == false) {
									startskill = true;
								}

								skill.isActive = true;
								activePlayer.skillSet.put(skill.name, skill);
								break;
							case COOLDOWN:
								if (skill.isActive == true) {
									stopskill = true;
								}
								skill.isActive = false;
								activePlayer.skillSet.put(skill.name, skill);
								break;

						}
						if (skill.isActive) {
							if (skill.permission != null && (!skill.permission.isEmpty())) {
								if (permissions.containsKey(p.getUniqueId().toString() + skill.permission)) {
									p.removeAttachment(permissions.get(p.getUniqueId().toString() + skill.permission));
									permissions.remove(p.getUniqueId().toString() + skill.permission);
								}
								PermissionAttachment attachment = p.addAttachment(lq, skill.permission, true, (int) lq.configMain.skillTickInterval + 1);
								permissions.put(p.getUniqueId().toString() + skill.permission, attachment);
							}
							if (startskill) {
								//pay the price...
								if (skill.manaCost>0) {
									if (activePlayer.mana<skill.manaCost) {
										p.sendMessage(lq.configLang.skillLackOfMana);
										skill.isCanceled = true;
										skill.lastUse = 0;
										activePlayer.skillSet.put(skill.name, skill);
										continue;
									}
								}
								//TODO put ingrediant use check here.
								
								// run the start command if any.
								if (skill.startCommand != null && (!skill.startCommand.isEmpty())) {
									lq.getServer().dispatchCommand(p, skill.startCommand);
									Skill skillClass = lq.skills.skillList.get(skill.name);
									if (skillClass != null) {
										// CommandResult result =
										skillClass.onCommand();
									}

								}
							}
						} else {
							if (stopskill) {
								// run the stop command if any.
								if (skill.endCommand != null && (!skill.endCommand.isEmpty())) {
									lq.getServer().dispatchCommand(p, skill.endCommand);
								}
							}
							if (skill.permission != null && (!skill.permission.isEmpty())) {
								if (permissions.containsKey(p.getUniqueId().toString() + skill.permission)) {
									p.removeAttachment(permissions.get(p.getUniqueId().toString() + skill.permission));
									permissions.remove(p.getUniqueId().toString() + skill.permission);
								}
							}
						}
					}
				}
			}
		}
	}
}
