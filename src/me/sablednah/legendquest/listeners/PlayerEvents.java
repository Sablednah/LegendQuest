package me.sablednah.legendquest.listeners;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.db.HealthStore;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.events.LevelUpEvent;
import me.sablednah.legendquest.experience.SetExp;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class PlayerEvents implements Listener {

	public class delayedSpawn implements Runnable {

		public int	xp;
		public UUID	player;

		public delayedSpawn(int xp, UUID player) {
			this.xp = xp;
			this.player = player;
		}

		public void run() {
			PC pc = lq.players.getPC(player);
			pc.setXP(xp);
			lq.players.savePlayer(pc);
			pc.scheduleHealthCheck();
			pc.scheduleXPSave();
		}
	}

	public Main	lq;

	public PlayerEvents(final Main p) {
		this.lq = p;
	}

	// preserve XP on death...
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(final PlayerDeathEvent event) {
		int xp = (int) (event.getDroppedExp() * (lq.configMain.percentXpLossRespawn / 100.0D));
		event.setDroppedExp(xp);
		event.setKeepLevel(true);
		if (Main.debugMode) {
			System.out.print("DIED: " + event.getEntity().getName() + " [" + event.hashCode() + "] " + event.getEventName() + " - " + event.getDeathMessage());
		}
	}

	// set to monitor - we're not gonna change the login, only load our data
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		PC pc = lq.players.getPC(p);
		lq.players.addPlayer(uuid, pc);

		if (!lq.validWorld(p.getWorld().getName())) {
			if (lq.configMain.manageHealthNonLqWorlds) {
				HealthStore hs = lq.datasync.getAltHealthStore(p.getUniqueId());
				if (hs == null || hs.getMaxhealth() < 1) {
					double hp = p.getHealth();
					if (hp > 20.0D) {
						hp = 20.0D;
						p.setHealth(hp);
						p.setMaxHealth(20.0D);
						p.setHealthScale(20.0D);
					}
				} else {
					p.setHealth(hs.getHealth());
					p.setMaxHealth(hs.getMaxhealth());
					p.setHealthScale(20.0D);
				}
			}
		} else {
			p.setTotalExperience(pc.currentXP);
			p.setMaxHealth(pc.maxHP);
			if (pc.getHealth() > pc.maxHP) {
				pc.setHealth(pc.maxHP);
			}
			if (pc.getHealth() > p.getMaxHealth()) {
				pc.setHealth(p.getMaxHealth());
			}
			p.setHealth(pc.getHealth());
			pc.healthCheck();
			p.setWalkSpeed(pc.race.baseSpeed);
		}

		if (Main.debugMode) {
			Location l = p.getLocation();
			Chunk c = l.getChunk();
			int x = c.getX();
			int z = c.getZ();

			for (int i = -10; i < 11; i++) {
				for (int j = -10; j < 11; j++) {
					Chunk chunk = p.getWorld().getChunkAt(x + i, z + j);
					Entity[] ents = chunk.getEntities();
					for (Entity e : ents) {
						if (e.getType() == EntityType.IRON_GOLEM) {
							System.out.print("Removing Golem from:" + i + " , " + j);
							e.remove();
						}
					}
				}
			}
		}
	}

	// set to monitor - we're not gonna change the teleport, only load our data
	// this loads PC data if players swich to/from a non LQ world
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPort(PlayerTeleportEvent event) {
		Player p = event.getPlayer();

		if (!lq.validWorld(event.getTo().getWorld().getName())) {
			if (lq.configMain.manageHealthNonLqWorlds) {
				HealthStore hs = lq.datasync.getAltHealthStore(p.getUniqueId());
				if (hs == null || hs.getMaxhealth() < 1) {
					double hp = p.getHealth();
					if (hp > 20.0D) {
						hp = 20.0D;
						p.setHealth(hp);
						p.setMaxHealth(20.0D);
						p.setHealthScale(20.0D);
					}
				} else {
					p.setHealth(hs.getHealth());
					p.setMaxHealth(hs.getMaxhealth());
					p.setHealthScale(20.0D);
				}
			}
			return;
		} else {
			if (!lq.validWorld(p.getWorld().getName())) {
				// from invalid word to valid world
				// save current health
				HealthStore hs = new HealthStore(p.getUniqueId(), p.getHealth(), p.getMaxHealth());
				lq.datasync.addHPWrite(hs);

				// load character
				UUID uuid = p.getUniqueId();
				PC pc = lq.players.getPC(p);
				lq.players.addPlayer(uuid, pc);
				p.setTotalExperience(pc.currentXP);
				p.setMaxHealth(pc.maxHP);
				p.setHealth(pc.getHealth());
				pc.healthCheck();
				pc.scheduleHealthCheck();
				pc.scheduleCheckInv();

			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPort2(PlayerTeleportEvent event) {
		PC pc = lq.players.getPC(event.getPlayer());
		if (pc != null) {
			pc.scheduleHealthCheck();
		}
	}

	// set to monitor - we can't change the quit - just want to clean our data up.
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (!lq.validWorld(event.getPlayer().getWorld().getName())) {
			if (lq.configMain.manageHealthNonLqWorlds) {
				HealthStore hs = new HealthStore(uuid, event.getPlayer().getHealth(), event.getPlayer().getMaxHealth());
				lq.datasync.addHPWrite(hs);
			}
		}
		lq.players.removePlayer(uuid);
	}

	// set to monitor - we can't change the quit - just want to clean our data up.
	@EventHandler(priority = EventPriority.MONITOR)
	public void okKick(PlayerKickEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (!lq.validWorld(event.getPlayer().getWorld().getName())) {
			if (lq.configMain.manageHealthNonLqWorlds) {
				HealthStore hs = new HealthStore(uuid, event.getPlayer().getHealth(), event.getPlayer().getMaxHealth());
				lq.datasync.addHPWrite(hs);
			}
		}
		lq.players.removePlayer(uuid);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();

		if (!lq.validWorld(p.getWorld().getName())) {
			if (lq.configMain.manageHealthNonLqWorlds) {
				HealthStore hs = lq.datasync.getAltHealthStore(p.getUniqueId());
				if (hs == null || hs.getMaxhealth() < 1) {
					double hp = p.getHealth();
					if (hp > 20.0D) {
						hp = 20.0D;
						p.setHealth(hp);
						p.setMaxHealth(20.0D);
					}
				} else {
					p.setHealth(hs.getHealth());
					p.setMaxHealth(hs.getMaxhealth());
				}
			}
			return;
		}

		PC pc = lq.players.getPC(p);
		lq.effectManager.removeAllProcess(OwnerType.PLAYER, p.getUniqueId());
		lq.logWarn("currentXP: " + pc.currentXP);
		int currentXP = SetExp.getTotalExperience(p);
		lq.logWarn("totxp: " + currentXP);
		int xpLoss = (int) (currentXP * (lq.configMain.percentXpLossRespawn / 100));
		lq.logWarn("xpLoss: " + xpLoss);
		int newXp = currentXP - xpLoss;
		pc.setXP(newXp);
		// lq.players.savePlayer(pc);
		lq.getServer().getScheduler().runTaskLaterAsynchronously(lq, new delayedSpawn(newXp, pc.uuid), 5);
		p.setWalkSpeed(pc.race.baseSpeed);
	}

	// track EXP changes - and halve then if dual class
	@EventHandler(priority = EventPriority.LOWEST)
	public void onXPChange(PlayerExpChangeEvent event) {
		Player p = event.getPlayer();
		if (!lq.validWorld(p.getWorld().getName())) {
			return;
		}

		double xpAmount = event.getAmount();
		if (Main.debugMode) {
			System.out.print("xpAmount: " + xpAmount);
		}

		UUID uuid = p.getUniqueId();
		PC pc = lq.players.getPC(uuid);

		if (Main.debugMode) {
			System.out.print("xpAmount (" + xpAmount + ") p.getExpToLevel(): " + p.getExpToLevel());
		}

		if (xpAmount >= SetExp.getExpUntilNextLevel(p)) {
			// will level up
			if (Main.debugMode) {
				System.out.print("Leveling up: lq.configMain.hardLevelCap = " + lq.configMain.hardLevelCap + " - lq.configMain.max_level > " + lq.configMain.max_level);
			}
			if (lq.configMain.hardLevelCap && lq.configMain.max_level > 0) {
				if (Main.debugMode) {
					System.out.print("Leveling up: p.getLevel() = " + p.getLevel() + " - lq.configMain.max_level > " + lq.configMain.max_level);
				}
				if (p.getLevel() >= lq.configMain.max_level) {
					if (Main.debugMode) {
						System.out.print("zeroing XP");
					}
					event.setAmount(0);
					xpAmount = 0;
				}
			}
		}

		if (xpAmount > 0) {

			// ScaleXP
			xpAmount = (int) (xpAmount * (lq.configMain.scaleXP / 100.0D));

			if (Main.debugMode) {
				System.out.print("ScaleXP (" + lq.configMain.scaleXP + ") xpAmount: " + xpAmount);
			}

			// half xp gain for dual class
			if (pc.subClass != null) {
				xpAmount = xpAmount / 2.0D;
			}
			if (Main.debugMode) {
				System.out.print("subclassed xpAmount: " + xpAmount);
			}

			// useParties
			if (lq.configMain.useParties) {
				List<Player> party = lq.partyManager.getPartyMembers(p);
				if (party != null) { // has party
					if (party.size() > 1) { // party has at least 2 people!
						xpAmount = (xpAmount * (1 + (lq.configMain.partyBonus / 100.0D))); // bonus party XP
						if (Main.debugMode) {
							System.out.print("partyBonus (" + lq.configMain.partyBonus + ") xpAmount: " + xpAmount);
						}
						xpAmount = xpAmount / party.size();
						if (Main.debugMode) {
							System.out.print("partyBonus perperson xpAmount: " + xpAmount);
						}
						if (Main.debugMode) {
							System.out.print("party xpAmount: " + xpAmount);
						}
						for (Player pp : party) {
							if (!(pp.getUniqueId().equals(p.getUniqueId()))) {
								/*
								 * pp.giveExp((int) Math.round(xpAmount)); if (lq.configMain.XPnotify) {
								 * pp.sendMessage(lq.configLang.partyXpChange + xpAmount); }
								 */
								Random rnd = new Random();
								long rndNum = rnd.nextInt(8) + 1;
								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(lq, new DelayedXP(p.getUniqueId(), xpAmount), rndNum);
							}
						}
					}
				}
			}

			pc.scheduleXPSave();

			// lq.players.addPlayer(uuid, pc);
			lq.players.scheduleUpdate(uuid);

			event.setAmount((int) Math.round(xpAmount));

			if (xpAmount >= SetExp.getExpUntilNextLevel(p)) {
				pc.scheduleHealthCheck();
				pc.skillSet = pc.getUniqueSkills(true);
				final LevelUpEvent e = new LevelUpEvent(p, p.getLevel() + 1, pc);
				Bukkit.getServer().getPluginManager().callEvent(e);
				if (Main.debugMode) {
					System.out.print("Level UP++");
				}
				lq.players.setLqPerms(pc);
			}
		}
	}

	// track EXP changes - and notify if needed
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onXPNotify(PlayerExpChangeEvent event) {
		if (lq.configMain.XPnotify) {
			event.getPlayer().sendMessage(lq.configLang.xpChange + event.getAmount());
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		if (p != null) {
			ItemStack item = p.getInventory().getItem(event.getNewSlot());
			if (item != null) {
				if (item.getType() == Material.WRITTEN_BOOK) {
					BookMeta bm = (BookMeta) item.getItemMeta();
					System.out.print("found book: " + bm.getTitle());
					if (bm.getTitle().equalsIgnoreCase(ChatColor.RESET + "journal")) {
						bm = lq.players.writeJournal(bm, p);
						item.setItemMeta(bm);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		// check whether the event has been cancelled by another plugin
		if (!e.isCancelled()) {
			HumanEntity ent = e.getWhoClicked();
			if (ent instanceof Player) {
				Player player = (Player) ent;
				Inventory inv = e.getInventory();
				if (inv instanceof AnvilInventory) {
					AnvilInventory anvil = (AnvilInventory) inv;
					InventoryView view = e.getView();
					int rawSlot = e.getRawSlot();
					// compare raw slot to the inventory view to make sure we are in the upper inventory
					if (rawSlot == view.convertSlot(rawSlot)) {
						// 2 = result slot
						if (rawSlot == 2) {
							// all three items in the anvil inventory
							ItemStack[] items = anvil.getContents();
							// item in the left slot
							ItemStack item1 = items[0];
							// item in the right slot
							ItemStack item2 = items[1];

							// I do not know if this is necessary
							if (item1 != null && item2 != null) {
								Material id1 = item1.getType();
//								Material id2 = item2.getType();

								// if the player is repairing something the ids will be the same
								if (id1 != Material.AIR) { // && id1 == id2) {
									// item in the result slot
									ItemStack item3 = e.getCurrentItem();

									// check if there is an item in the result slot
									if (item3 != null) {
										ItemMeta meta = item3.getItemMeta();

										// meta data could be null
										if (meta != null) {
											// get the repairable interface to obtain the repair cost
											if (meta instanceof Repairable) {
												Repairable repairable = (Repairable) meta;
												int cost = repairable.getRepairCost();

												int newcost;
												if (Main.debugMode) {
													System.out.print("Cost: " + cost);
												}
												if (lq.configMain.blockRepairXPloss) {
													if (Main.debugMode) {
														System.out.print("blocking repair xp loss - setting cost to 0");
													}
													newcost = 0;
													repairable.setRepairCost(0);
												} else {
													newcost = (int) (cost * (lq.configMain.adjustRepairXP / 100));
													if (Main.debugMode) {
														System.out.print("setting repair cost to : " + newcost);
													}
													repairable.setRepairCost(newcost);
												}
												PC pc = lq.getPlayers().getPC(player);
												if (lq.configMain.useAlternateRepairExpCost) {
													if (Main.debugMode) {
														System.out.print("using repair alt cost...");
													}
													int manaCost = 0;
													int ecoCost = 0;
													boolean canPay = true;
													if (pc != null) {
														if (lq.configMain.manaCostPerRepairLevel != 0) {
															manaCost = cost * lq.configMain.manaCostPerRepairLevel;
															if (manaCost > pc.mana) {
																canPay = false;
																player.sendMessage(lq.configLang.repairNoMana + manaCost);
															}
														}
														if (Main.debugMode) {
															System.out.print("manaCost = " + manaCost);
														}
														if (Main.debugMode) {
															System.out.print("canPay = " + canPay);
														}
														if (lq.configMain.ecoCostPerRepairLevel != 0) {
															ecoCost = cost * lq.configMain.ecoCostPerRepairLevel;
															if (!pc.canPay(ecoCost)) {
																canPay = false;
																player.sendMessage(lq.configLang.repairNoEco + ecoCost);
															}
														}
														if (Main.debugMode) {
															System.out.print("ecoCost = " + ecoCost);
														}
														if (Main.debugMode) {
															System.out.print("canPay = " + canPay);
														}
														if (canPay) {
															if (lq.configMain.materialRepairQtyPerLevel > 0 && !lq.configMain.materialRepairCost.isEmpty()) {
																// can aford other costs - try to take the materials noe
																ItemStack itemCost = new ItemStack(Material.matchMaterial(lq.configMain.materialRepairCost), (lq.configMain.materialRepairQtyPerLevel * cost));
																if (Main.debugMode) {
																	System.out.print("itemCost = " + itemCost.toString());
																}
																if (pc.payItem(itemCost)) {
																	if (manaCost != 0) {
																		pc.payMana(manaCost);
																	}
																	if (ecoCost != 0) {
																		pc.payCash(ecoCost);
																	}

																} else {
																	canPay = false;
																	player.sendMessage(lq.configLang.repairNoItem + lq.configMain.materialRepairCost + " x " + (lq.configMain.materialRepairQtyPerLevel * cost));
																}
															}
														}
														if (Main.debugMode) {
															System.out.print("canPay = " + canPay);
														}
														if (!canPay) {
															e.setCancelled(true);
															return;
														}
													}
												}
												if (Main.debugMode) {
													System.out.print("level Cost is : " + repairable.getRepairCost());
												}

												int exp = SetExp.getTotalExperience(player);
												if (Main.debugMode) {
													System.out.print("exp is : " + exp);
												}

												if (newcost < cost) {
													if (Main.debugMode) {
														System.out.print("player.getLevel() is : " + player.getLevel());
													}
													int bigExp = SetExp.getExpToLevel(player.getLevel());
													int littleExp = SetExp.getExpToLevel(player.getLevel() - newcost);

													if (Main.debugMode) {
														System.out.print("bigExp is : " + bigExp);
													}
													if (Main.debugMode) {
														System.out.print("littleExp is : " + littleExp);
													}

													int difference = bigExp - littleExp;
													if (Main.debugMode) {
														System.out.print("difference is : " + difference);
													}
													exp = exp - difference;
													if (Main.debugMode) {
														System.out.print("exp is : " + exp);
													}
												}
												Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(lq, new DelayedFixXp(exp, pc), 2L);

											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public class DelayedXP implements Runnable {
		UUID	id		= null;
		double	amount	= 0.0D;

		public DelayedXP(UUID id, double amount) {
			this.id = id;
			this.amount = amount;
		}

		public void run() {
			Bukkit.getServer().getScheduler().runTaskLater(lq, new GiveXP(id, amount), 1L);
		}
	}

	public class GiveXP implements Runnable {
		UUID	id		= null;
		double	amount	= 0.0D;

		public GiveXP(UUID id, double amount) {
			this.id = id;
			this.amount = amount;
		}

		public void run() {
			Player pp = lq.getServer().getPlayer(id);
			if (pp != null) {
				pp.giveExp((int) Math.round(amount));
				if (lq.configMain.XPnotify) {
					pp.sendMessage(lq.configLang.partyXpChange + amount);
				}
			}
		}
	}

	public class DelayedFixXp implements Runnable {
		int	xp	= 0;
		PC	pc;

		public DelayedFixXp(int exp, PC pc) {
			this.xp = exp;
			this.pc = pc;
		}

		public void run() {
			if (pc.getPlayer() != null && !pc.getPlayer().isDead()) {
				// still alive!
				if (Main.debugMode) {
					System.out.print("postrepair: setting xp to : " + xp);
				}
				pc.setXP(xp);
				pc.scheduleXPSave();
			}
		}
	}

}
