package me.sablednah.legendquest.playercharacters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.events.SkillTick;
import me.sablednah.legendquest.skills.SkillDataStore;
import me.sablednah.legendquest.skills.SkillPhase;
import me.sablednah.legendquest.skills.SkillType;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class PCs {

	public Main											lq;
	public Map<UUID, PC>								activePlayers	= new HashMap<UUID, PC>();
	public Map<UUID, Scoreboard>						scoreboards		= new HashMap<UUID, Scoreboard>();
	public HashMap<UUID, List<PermissionAttachment>>	lqperms			= new HashMap<UUID, List<PermissionAttachment>>();
	public HashMap<String, PermissionAttachment>		permissions		= new HashMap<String, PermissionAttachment>();
	public int											ticks			= 0;

	public PCs(Main p) {
		this.lq = p;
		for (Player player : lq.getServer().getOnlinePlayers()) {
			UUID uuid = player.getUniqueId();
			PC pc = getPC(player);
			addPlayer(uuid, pc);
			setLqPerms(pc);
			if (lq.scoreboard != null) {
				//
			}
		}
		Bukkit.getServer().getScheduler().runTaskTimer(lq, new SkillTicker(), 10L, lq.configMain.skillTickInterval);
		Bukkit.getServer().getScheduler().runTaskTimer(lq, new ScoresTicker(), 5L, 20L);
	}

	public void addPlayer(UUID uuid, PC pc) {
		activePlayers.put(uuid, pc);
		setLqPerms(pc);
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
		removeLQPerms(uuid);
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
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(lq, new DelayedWrite(uuid), 4L);
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

	public class ScoresTicker implements Runnable {
		public void run() {
			updateScoreBoards();
		}
	}

	public void updateScoreBoards() {
		for (PC activePlayer : activePlayers.values()) {
			Player p = lq.getServer().getPlayer(activePlayer.uuid);
			if (p != null && p.isOnline()) {
				updateScoreBoard(activePlayer);
			}
		}
	}

	public void updateScoreBoard(PC activePlayer) {
		if (lq.configMain.useScoreBoard) {
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard board = scoreboards.get(activePlayer.uuid);
			Objective statistics = null;
			Objective skillo = null;
			Objective level = null;
			Player p = activePlayer.getPlayer();
			String str = "";
			if (board == null) {
				// System.out.print(p.getName() + " making board");
				board = manager.getNewScoreboard();
				statistics = board.registerNewObjective("stats", "dummy");
				statistics.setDisplaySlot(DisplaySlot.BELOW_NAME);
				statistics.setDisplayName("% : Health");

				skillo = board.registerNewObjective("skills", "dummy");
				skillo.setDisplaySlot(DisplaySlot.SIDEBAR);
				skillo.setDisplayName("Skills");

				level = board.registerNewObjective("level", "dummy");
				level.setDisplaySlot(DisplaySlot.PLAYER_LIST);
				level.setDisplayName("Lvl");

				Score scores[] = new Score[activePlayer.skillSet.size()];
				int sknum = 0;
				for (SkillDataStore skill : activePlayer.skillSet.values()) {
					if (skill.type == SkillType.ACTIVE) {
						str = "";
						if (skill.levelRequired < 100) {
							str += " ";
						}
						if (skill.levelRequired < 10) {
							str += " ";
						}
						str += skill.levelRequired + " " + ChatColor.GREEN.toString() + skill.name;

						if (str.length() > 16) {
							str = str.substring(0, 16);
						}
//						System.out.print(str + "<");
						scores[sknum] = skillo.getScore(str);
						scores[sknum].setScore(-1);
						sknum++;
					}
				}

				p.setScoreboard(board);

				scoreboards.put(p.getUniqueId(), board);
			} else {
				statistics = board.getObjective("stats");
				skillo = board.getObjective("skills");
				level = board.getObjective("level");
				// System.out.print(p.getName() + " updating board");
				// p.setScoreboard(board);
				if (p.getScoreboard() != board) {
					p.setScoreboard(board);					
				}
			}

			/*
			 * String str = activePlayer.mainClass.name; if (str.length() > 16) { str = str.substring(0, 16); } Score
			 * score = statistics.getScore(str); score.setScore(p.getLevel()); // Integer only!
			 * 
			 * str = activePlayer.karmaName(); if (str.length() > 7) { str = str.substring(0, 7); } Score karma =
			 * statistics.getScore("Karma: (" + str + ")"); karma.setScore((int) (activePlayer.karma)); // Integer only!
			 */
			for (Player pl : lq.getServer().getOnlinePlayers()) {
				if (pl != null) {
 					Score info = statistics.getScore(pl.getName());
					info.setScore((int)Math.round((pl.getHealth() / pl.getMaxHealth())*100));

					Score listlevel = level.getScore(pl.getName());
					listlevel.setScore(pl.getLevel());
				}

			}

			// Double balance = activePlayer.getBalance();
			// if (balance != null) {
			// Score cash = statistics.getScore("Cash: ");
			// cash.setScore((int) (Math.floor(balance))); // Integer only!
			// }

			Score scores[] = new Score[activePlayer.skillSet.size()];
			int sknum = 0;

			Map<String, Boolean> selected = activePlayer.skillsSelected;

			for (SkillDataStore skill : activePlayer.skillSet.values()) {
				// System.out.print(skill.name + " : " + skill.type);

				String color = "";
				String lvl = "";
				int time = 0;
				if (skill.type == SkillType.ACTIVE) {
					str = "";
					if (skill.levelRequired < 100) {
						lvl += " ";
					}
					if (skill.levelRequired < 10) {
						lvl += " ";
					}
					lvl += skill.levelRequired + " ";
					str = skill.name;
					// System.out.print(str);
					if (str.length() > 10) {
						str = str.substring(0, 10);
					}
					switch (skill.getPhase()) {
						case READY:
							if (selected != null && selected.containsKey(skill.name)) {
								color = ChatColor.DARK_GREEN.toString();
								board.resetScores(lvl + ChatColor.STRIKETHROUGH.toString() + str);								
							} else {
								color = ChatColor.STRIKETHROUGH.toString();
							}
							board.resetScores(lvl + ChatColor.DARK_PURPLE.toString() + str);
							board.resetScores(lvl + ChatColor.LIGHT_PURPLE.toString() + str);
							board.resetScores(lvl + ChatColor.GREEN.toString() + str);
							board.resetScores(lvl + ChatColor.RED.toString() + str);
							break;
						case BUILDING:
							color = ChatColor.DARK_PURPLE.toString();
							board.resetScores(lvl + ChatColor.GREEN.toString() + str);
							board.resetScores(lvl + ChatColor.LIGHT_PURPLE.toString() + str);
							board.resetScores(lvl + ChatColor.DARK_GREEN.toString() + str);
							board.resetScores(lvl + ChatColor.RED.toString() + str);
							break;
						case DELAYED:
							color = ChatColor.LIGHT_PURPLE.toString();
							board.resetScores(lvl + ChatColor.GREEN.toString() + str);
							board.resetScores(lvl + ChatColor.DARK_PURPLE.toString() + str);
							board.resetScores(lvl + ChatColor.DARK_GREEN.toString() + str);
							board.resetScores(lvl + ChatColor.RED.toString() + str);
							break;
						case ACTIVE:
							color = ChatColor.GREEN.toString();
							board.resetScores(lvl + ChatColor.DARK_GREEN.toString() + str);
							board.resetScores(lvl + ChatColor.DARK_PURPLE.toString() + str);
							board.resetScores(lvl + ChatColor.LIGHT_PURPLE.toString() + str);
							board.resetScores(lvl + ChatColor.RED.toString() + str);
							break;
						case COOLDOWN:
							color = ChatColor.RED.toString();
							board.resetScores(lvl + ChatColor.GREEN.toString() + str);
							board.resetScores(lvl + ChatColor.DARK_PURPLE.toString() + str);
							board.resetScores(lvl + ChatColor.LIGHT_PURPLE.toString() + str);
							board.resetScores(lvl + ChatColor.DARK_GREEN.toString() + str);
							break;
					}
					time = (int) skill.getTimeLeft();
					str = lvl + color + str;
					// System.out.print(str + " : " + time);
					if (skill.getPhase() == SkillPhase.READY && time == 0) {
						scores[sknum] = skillo.getScore(str);
						scores[sknum].setScore(-1);
					} else {
						scores[sknum] = skillo.getScore(str);
						scores[sknum].setScore(time);
					}
					sknum++;
				}
			}
		}
	}

	public class SkillTicker implements Runnable {
		public void run() {
			ticks++;
			for (PC activePlayer : activePlayers.values()) {
				Player p = lq.getServer().getPlayer(activePlayer.uuid);
				if (p != null && p.isOnline()) {
					for (SkillDataStore skill : activePlayer.skillSet.values()) {
						if (!activePlayer.validSkill(skill.name)) {
							continue;
						}
						boolean startskill = false;
						boolean stopskill = false;
						SkillPhase phase = null;
						SkillPhase lastPhase = null;
						SkillPhase virtualPhase = null;
						if (skill.type == SkillType.PASSIVE) {
							// passive skills are "always on"
							skill.setActive(true);
							phase = SkillPhase.ACTIVE;
							lastPhase = SkillPhase.ACTIVE;
							virtualPhase = SkillPhase.ACTIVE;
						} else {
							phase = skill.checkPhase();
							lastPhase = skill.getPhase();
							virtualPhase = phase;

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
						 * if (lastPhase!= SkillPhase.READY && (skill.name.equalsIgnoreCase("aura") ||
						 * skill.name.equalsIgnoreCase("might"))) {
						 * System.out.print("Skill "+skill.name+" use: "+p.getName());
						 * System.out.print("Skill virtual: "+virtualPhase.toString());
						 * System.out.print("Skill phase: "+phase.toString());
						 * System.out.print("Skill lastPhase: "+lastPhase.toString());
						 * 
						 * }
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
								permissions.get(p.getUniqueId().toString() + skill.permission).remove();
								permissions.remove(p.getUniqueId().toString() + skill.permission);
//								if (permissions.containsKey(p.getUniqueId().toString() + skill.permission)) {
//									p.removeAttachment(permissions.get(p.getUniqueId().toString() + skill.permission));
//									permissions.remove(p.getUniqueId().toString() + skill.permission);
//								}
							}
							if (skill.permissions != null && (!skill.permissions.isEmpty())) {
								for (String perm : skill.permissions) {
									permissions.get(p.getUniqueId().toString() + perm).remove();
									permissions.remove(p.getUniqueId().toString() + perm);
//									if (permissions.containsKey(p.getUniqueId().toString() + perm)) {
//										p.removeAttachment(permissions.get(p.getUniqueId().toString() + perm));
//										permissions.remove(p.getUniqueId().toString() + perm);
//									}

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

	public void removeLQPerms(UUID uuid) {
		UUID uid = uuid;
		List<PermissionAttachment> permsList = lqperms.get(uid);
		if (permsList != null) {
			for (PermissionAttachment p : permsList) {
				p.remove();
			}
		}
		lqperms.remove(uid);
	}

	public void setLqPerms(PC pc) {
		removeLQPerms(pc.uuid);
		List<PermissionAttachment> permsList = new ArrayList<PermissionAttachment>();
		Player p = pc.getPlayer();
		PermissionAttachment r = p.addAttachment(lq, "legendquest.hasrace." + pc.race.name, true);
		permsList.add(r);
		PermissionAttachment m = p.addAttachment(lq, "legendquest.hasclass." + pc.mainClass.name, true);
		permsList.add(m);
		if (pc.subClass != null) {
			PermissionAttachment s = p.addAttachment(lq, "legendquest.hasclass." + pc.subClass.name, true);
			permsList.add(s);
		}
		lqperms.put(pc.uuid, permsList);
	}

}
