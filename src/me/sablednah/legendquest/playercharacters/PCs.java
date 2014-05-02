package me.sablednah.legendquest.playercharacters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.skills.SkillDataStore;
import me.sablednah.legendquest.skills.SkillPhase;
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
						boolean startskill = false;
						boolean stopskill = false;

						SkillPhase phase = skill.checkPhase();
						SkillPhase lastPhase = skill.getPhase();
						SkillPhase virtualPhase = phase;

						if (skill.type == SkillType.PASSIVE) {
							// passive skills are "always on"
							skill.setActive(true);
						} else {
							/*
							 * if (skill.name.toLowerCase().startsWith("summon")) {
							 * System.out.print("{skill tick} Checking  "+ skill.name + " Perm:" + skill.permission +
							 * " command:" + skill.startCommand); } if (skill.name.toLowerCase().startsWith("summon")) {
							 * System.out.print("{skill tick} "+ skill.name + "| lastPhase  "+ lastPhase + " phase:" +
							 * phase); }
							 */

							// ensure skills spend 1 tick at each state they have a value for >0
							switch (lastPhase) {
								case READY:
									switch (phase) {
										case DELAYED:
											// skipped building
											if (skill.buildup > 0) {
												// should have had a build up -
												virtualPhase = SkillPhase.BUILDING;
												break;
											}
										case ACTIVE:
											// skipped building and delay
											if (skill.buildup > 0) {
												// should have had a build up -
												virtualPhase = SkillPhase.BUILDING;
												break;
											}
											if (skill.delay > 0) {
												// should have had a delay up -
												virtualPhase = SkillPhase.DELAYED;
												break;
											}
										case COOLDOWN:
											// skipped building, delay and active!
											if (skill.buildup > 0) {
												// should have had a build up -
												virtualPhase = SkillPhase.BUILDING;
												break;
											}
											if (skill.delay > 0) {
												// should have had a delay -
												virtualPhase = SkillPhase.DELAYED;
												break;
											}
											// should have had a active -
											virtualPhase = SkillPhase.ACTIVE;
											break;
									}
									break;
								case BUILDING:
									switch (phase) {
										case ACTIVE:
											// skipped delay
											if (skill.delay > 0) {
												// should have had a delay up -
												virtualPhase = SkillPhase.DELAYED;
												break;
											}
										case COOLDOWN:
											// skipped delay and active!
											if (skill.delay > 0) {
												// should have had a delay -
												virtualPhase = SkillPhase.DELAYED;
												break;
											}
											// should have had a active -
											virtualPhase = SkillPhase.ACTIVE;
											break;
										case READY:
											// skipped delay, active!
											if (skill.delay > 0) {
												// should have had a delay -
												virtualPhase = SkillPhase.DELAYED;
												break;
											}
											// should have had a active -
											virtualPhase = SkillPhase.ACTIVE;
											break;

									}
									break;
								case DELAYED:
									switch (phase) {
										case COOLDOWN:
											// skipped active!
											virtualPhase = SkillPhase.ACTIVE;
											break;
										case READY:
											virtualPhase = SkillPhase.ACTIVE;
											break;

									}
									break;
							}
							switch (virtualPhase) {
								case READY:
									if (skill.isActive()) {
										stopskill = true;
									}
									skill.setActive(false);
									break;
								case BUILDING:
									skill.setActive(false);
									Location pLoc = p.getLocation();
									double distance = pLoc.distanceSquared(skill.getLastUseLoc());
									double allowed = lq.configMain.skillBuildupMoveAllowed;
									allowed = allowed * allowed;
									if (distance > allowed) {
										skill.setActive(false);
										skill.setCanceled(true);
										skill.setLastUse(0);
										skill.setPhase(SkillPhase.READY);
										virtualPhase = SkillPhase.READY;
										p.sendMessage(skill.name + " : " + lq.configLang.skillBuildupDisturbed);
									}
									break;
								case DELAYED:
									skill.setActive(false);
									break;
								case ACTIVE:
									if (!skill.isActive()) {
										startskill = true;
									}
									skill.setActive(true);
									break;
								case COOLDOWN:
									if (skill.isActive()) {
										stopskill = true;
									}
									skill.setActive(false);
									break;

							}
						}
						if (skill.isActive()) {
							skill.startperms(lq, p);
							if (startskill) {
								if (skill.delay > 0 || skill.buildup > 0) {
									skill.start(lq, activePlayer);
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
						skill.setPhase(virtualPhase);
					}
				}
			}
		}
	}
}
