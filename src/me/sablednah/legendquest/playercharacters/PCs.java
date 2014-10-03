package me.sablednah.legendquest.playercharacters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.SkillTick;
import me.sablednah.legendquest.skills.SkillDataStore;
import me.sablednah.legendquest.skills.SkillPhase;
import me.sablednah.legendquest.skills.SkillType;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.permissions.PermissionAttachment;

public class PCs {

	public Main										lq;
	public Map<UUID, PC>							activePlayers	= new HashMap<UUID, PC>();
	public HashMap<String, PermissionAttachment>	permissions		= new HashMap<String, PermissionAttachment>();
	public int										ticks			= 0;

	public PCs(Main p) {
		this.lq = p;
		for (Player player : lq.getServer().getOnlinePlayers()) {
			UUID uuid = player.getUniqueId();
			PC pc = getPC(player);
			addPlayer(uuid, pc);
			if (lq.scoreboard != null) {
				//
			}
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
					// p.removeAttachment(val);
					val.remove();
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
			ticks++;
			for (PC activePlayer : activePlayers.values()) {
				Player p = lq.getServer().getPlayer(activePlayer.uuid);
				if (p != null && p.isOnline()) {
					for (SkillDataStore skill : activePlayer.skillSet.values()) {
						if ( !activePlayer.validSkill(skill.name) ) { continue; }						
						boolean startskill = false;
						boolean stopskill = false;
						SkillPhase phase = skill.checkPhase();
						SkillPhase lastPhase = skill.getPhase();
						SkillPhase virtualPhase = phase;
						if (skill.type == SkillType.PASSIVE) {
							// passive skills are "always on"
							skill.setActive(true);
						} else {
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
/*						
						if (lastPhase!= SkillPhase.READY && (skill.name.equalsIgnoreCase("aura") || skill.name.equalsIgnoreCase("might"))) { 
							System.out.print("Skill "+skill.name+" use: "+p.getName());
							System.out.print("Skill virtual: "+virtualPhase.toString());
							System.out.print("Skill phase: "+phase.toString());
							System.out.print("Skill lastPhase: "+lastPhase.toString());
							
						}
*/
						
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
							if (skill.permissions != null && (!skill.permissions.isEmpty())) {
								for (String perm : skill.permissions) {
									if (permissions.containsKey(p.getUniqueId().toString() + perm)) {
										p.removeAttachment(permissions.get(p.getUniqueId().toString() + perm));
										permissions.remove(p.getUniqueId().toString() + perm);
									}
									
								}
							}
						}
						skill.setPhase(virtualPhase);
					}
					SkillTick e = new SkillTick(p);
					lq.getServer().getPluginManager().callEvent(e);
				}
			}
		}
	}

	public double getSize(Entity entity) {
		double size = 1.6D;
		if (entity == null) {
			return size;
		}
		EntityType type = entity.getType();
		if (type == EntityType.COMPLEX_PART) {
			type = ((ComplexEntityPart) entity).getParent().getType();
		}
		switch (type) {
			case PLAYER:
				PC pc = getPC((Player) entity);
				if (pc != null) {
					size = pc.race.size;
				} else {
					size = 1.875D;
				}
				break;
			case GIANT:
				size = 12.0D;
				break;
			case GHAST:
				size = 7.5D;
				break;
			case ENDER_DRAGON:
				// height = 3.7 - using largest to allow for size in other dimensions
				size = 7.0D;
				break;
			case ENDERMAN:
				size = 2.9D;
				break;
			case WITHER:
				size = 2.8D;
				break;
			case IRON_GOLEM:
				size = 2.7D;
				break;
			case SKELETON:
				if (((Skeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
					size = 2.4D;
				} else {
					size = 2.0D;
				}
				break;
			case WITCH:
			case PIG_ZOMBIE:
			case ZOMBIE:
			case VILLAGER:
			case SQUID:
				size = 2.0D;
				break;
			case SNOWMAN:
				size = 1.9D;
				break;
			case MUSHROOM_COW:
				size = 1.8D;
				break;
			case CREEPER:
				size = 1.6D;
				break;
			case HORSE:
				size = 1.6D;
				break;
			case COW:
				size = 1.5D;
				break;
			case BLAZE:
				size = 1.4D;
				break;
			case SHEEP:
				size = 1.4D;
				break;
			case SPIDER:
				size = 1.2D;
			case PIG:
			case WOLF:
				size = 1.0D;
				break;
			case OCELOT:
				size = 0.9D;
				break;
			case CHICKEN:
			case CAVE_SPIDER:
				size = 0.6D;
				break;
			case SILVERFISH:
			case BAT:
				size = 0.4D;
				break;
			case MAGMA_CUBE:
			case SLIME:
				Slime s = (Slime) entity;
				size = s.getSize() * 0.5D;
				break;
			/* non typical entities just in case */
			case FALLING_BLOCK:
			case PRIMED_TNT:
			case MINECART:
			case MINECART_COMMAND:
			case MINECART_HOPPER:
			case MINECART_MOB_SPAWNER:
			case MINECART_CHEST:
			case MINECART_TNT:
			case MINECART_FURNACE:
			case ITEM_FRAME:
			case WITHER_SKULL:
			case ENDER_CRYSTAL:
			case BOAT:
				size = 1.0D;
				break;
			/* tiny stuff */
			case DROPPED_ITEM:
			case EXPERIENCE_ORB:
			case LEASH_HITCH:
			case SNOWBALL:
			case ARROW:
			case FIREBALL:
			case ENDER_PEARL:
			case ENDER_SIGNAL:
			case FIREWORK:
			case THROWN_EXP_BOTTLE:
			case SPLASH_POTION:
			case EGG:
			case FISHING_HOOK:
				size = 0.3D;
				break;
			case PAINTING:
				size = 2.0D;
			case UNKNOWN:
			default:
				size = 1.875D;
		}
		return size;
	}

}
/*
 *         
 */