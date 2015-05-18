package me.sablednah.legendquest.effects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class EffectManager {
	Main				lq;
	List<EffectProcess>	activeProcess	= new ArrayList<EffectProcess>();
	List<EffectProcess>	pendingProcess	= new ArrayList<EffectProcess>();
	long				effecttickcount	= 0;

	public EffectManager(Main lq) {
		this.lq = lq;
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(lq, new EffectRunner(), 5L, 5L);
	}

	public List<EffectProcess> getActiveProcess() {
		return activeProcess;
	}

	public boolean addActiveProcess(EffectProcess ep) {
		return activeProcess.add(ep);
	}

	public boolean removeAllProcess(OwnerType ot, UUID uuid) {
		boolean found = false;
		Iterator<EffectProcess> pp = pendingProcess.iterator();
		while (pp.hasNext()) {
			EffectProcess ep = pp.next();
			if (ep.owner != null && ep.owner.equals(ot)) {
				if (ep.uuid.equals(uuid)) {
					pp.remove();
					found = true;
				}
			}
		}
		Iterator<EffectProcess> ap = activeProcess.iterator();
		while (ap.hasNext()) {
			EffectProcess epa = ap.next();
			if (epa.owner != null && epa.owner.equals(ot)) {
				if (epa.uuid.equals(uuid)) {
					ap.remove();
					found = true;
				}
			}
		}
		return found;
	}

	public boolean removeEffects(OwnerType ot, UUID uuid, Effects effect) {
		boolean found = false;
		Iterator<EffectProcess> pp = pendingProcess.iterator();
		while (pp.hasNext()) {
			EffectProcess ep = pp.next();
			if (ep.effect != null && ep.effect.equals(effect)) {
				if (ep.owner.equals(ot)) {
					if (ep.uuid.equals(uuid)) {
						pp.remove();
						found = true;
					}
				}
			}
		}
		Iterator<EffectProcess> ap = activeProcess.iterator();
		while (ap.hasNext()) {
			EffectProcess epa = ap.next();
			if (epa.effect != null && epa.effect.equals(effect)) {
				if (epa.owner.equals(ot)) {
					if (epa.uuid.equals(uuid)) {
						ap.remove();
						found = true;
					}
				}
			}
		}
		return found;
	}

	public List<EffectProcess> getPendingProcess() {
		return pendingProcess;
	}

	public boolean addPendingProcess(EffectProcess ep) {
		return pendingProcess.add(ep);
	}

	public class EffectRunner implements Runnable {
		@SuppressWarnings("deprecation")
		public void run() {

			effecttickcount++;

			Effect effect;

			// process queued events

			Iterator<EffectProcess> iter = pendingProcess.iterator();
			while (iter.hasNext()) {

				EffectProcess pending = iter.next();

				// start pending
				if (pending.startTime == 0) {
					pending.start();
				}
				effect = pending.effect.getEffect();
				switch (pending.effect.getEffectType()) {
					case POTION:
						// add potion effect.
						if (pending.startTime <= System.currentTimeMillis()) {
							int ticks = (int) pending.duration / 50;
							PotionEffect peffect = new PotionEffect(pending.effect.getPotioneffectType(), ticks, pending.effect.getAmp(), false);
							switch (pending.owner) {
								case PLAYER:
									Player p = lq.getServer().getPlayer(pending.uuid);
									if (p != null) {
										if (p.hasPotionEffect(pending.effect.getPotioneffectType())) {
// System.out.print("Has potion effect " + pending.effect.getPotioneffectType().toString());
											boolean isshorter = false;
											Collection<PotionEffect> pots = p.getActivePotionEffects();
											for (PotionEffect pe : pots) {
// System.out.print("checking potion effect " + pe.toString());
												if (pe.getType().equals(pending.effect.getPotioneffectType())) {
													if (pe.getDuration() < ticks) {
														isshorter = true;
// System.out.print("effect duration " + pe.getDuration() + " < " + ticks);
													} else {
// System.out.print("effect duration " + pe.getDuration() + " > " + ticks);
													}
												}
											}
											if (isshorter) {
// System.out.print("removing effect");
												p.removePotionEffect(pending.effect.getPotioneffectType());
												//boolean add = 
												p.addPotionEffect(peffect);
// System.out.print("re-adding effect: " + add);
											}
										} else {
// System.out.print("no potion effect " + pending.effect.getPotioneffectType().toString());
											p.addPotionEffect(peffect);
										}
									}
									break;
								case MOB:
									LivingEntity ent = getEntity(pending.uuid);
									if (ent != null) {
										ent.addPotionEffect(peffect);
									}
									break;
								case LOCATATION:
									for (LivingEntity e : getNearbyEntities(pending.location, pending.radius)) {
										e.addPotionEffect(peffect);
									}
									List<Location> ball = getSphere(pending.location, pending.radius);
									for (Location l : ball) {
										if (l.getBlock().getType() == null || l.getBlock().getType() == Material.AIR) {
											if (Math.random() > 0.7D) {
												Utils.playEffect(Effect.POTION_BREAK, l, pending.effect.getPotioneffectType().getId());
											}
										}
									}
									break;
							}
						}
						break;
					case BUKKIT:
						switch (pending.owner) {
							case PLAYER:
								Player p = lq.getServer().getPlayer(pending.uuid);
								if (p != null) {
									Utils.playEffect(effect, p.getLocation(), pending.effect.getAmp());
								}
								break;
							case MOB:
								LivingEntity ent = getEntity(pending.uuid);
								if (ent != null) {
									Utils.playEffect(effect, ent.getLocation(), pending.effect.getAmp());
								}
								break;
							case LOCATATION:
								List<Location> ball = getSphere(pending.location, pending.radius);
								for (Location l : ball) {
									if (l.getBlock().getType() == null || l.getBlock().getType() == Material.AIR) {
										if (Math.random() > 0.5D) {
											Utils.playEffect(effect, l, pending.effect.getAmp());
										}
									}
								}
								break;
						}
						break;
					case CUSTOM:
						switch (pending.effect) {
							case BLEED:
							case SLOWBLEED:
								// ;nothing to do
								break;
							case STUNNED:
								if (pending.startTime <= System.currentTimeMillis()) {
									int ticks = (int) pending.duration / 50;
									PotionEffect peffect = new PotionEffect(pending.effect.getPotioneffectType(), ticks, 2, false);
									switch (pending.owner) {
										case PLAYER:
											Player p = lq.getServer().getPlayer(pending.uuid);
											if (p != null) {
												p.addPotionEffect(peffect);
											}
											break;
										case MOB:
											LivingEntity ent = getEntity(pending.uuid);
											if (ent != null) {
												ent.addPotionEffect(peffect);
											}
											break;
										case LOCATATION:
											for (LivingEntity e : getNearbyEntities(pending.location, pending.radius)) {
												e.addPotionEffect(peffect);
											}
											break;
									}
								}
								break;
							case SNEAK:
								int ticks = (int) pending.duration / 50;
								PotionEffect peffect = new PotionEffect(pending.effect.getPotioneffectType(), ticks, 2, false);
								switch (pending.owner) {
									case PLAYER:
										Player p = lq.getServer().getPlayer(pending.uuid);
										if (p != null) {
											p.setSneaking(true);
										}
										break;
									case MOB:
										LivingEntity ent = getEntity(pending.uuid);
										if (ent != null) {
											ent.addPotionEffect(peffect);
										}
										break;
									case LOCATATION:
										for (LivingEntity e : getNearbyEntities(pending.location, pending.radius)) {
											if (e instanceof Player) {
												((Player) e).setSneaking(true);
											} else {
												e.addPotionEffect(peffect);
											}
										}
										break;
								}
						}
						break;
				}

				// add to active
				activeProcess.add(pending);
				iter.remove();
			}

			Iterator<EffectProcess> iter2 = activeProcess.iterator();
			while (iter2.hasNext()) {
				EffectProcess active = iter2.next();
				// remove expired
				long end = active.startTime + active.duration;

				if (end < System.currentTimeMillis()) {
					if (active.effect == Effects.SNEAK && active.owner == OwnerType.PLAYER) {
						Player p = lq.getServer().getPlayer(active.uuid);
						p.setSneaking(false);
					}
					iter2.remove();
				} else {
					effect = active.effect.getEffect();
					switch (active.effect.getEffectType()) {
						case POTION:
							switch (active.owner) {
								case PLAYER:
								case MOB:
									break; // already potion'ed
								case LOCATATION:
									int ticksleft = (int) (active.duration - (System.currentTimeMillis() - active.startTime)) / 50;
									PotionEffect peffect2 = new PotionEffect(active.effect.getPotioneffectType(), ticksleft, active.effect.getAmp(), true);

									for (LivingEntity e : getNearbyEntities(active.location, active.radius)) {
										if (!e.hasPotionEffect(active.effect.getPotioneffectType())) {
											e.addPotionEffect(peffect2);
										}
									}

									List<Location> ball = getSphere(active.location, active.radius);
									for (Location l : ball) {
										if (l.getBlock().getType() == null || l.getBlock().getType() == Material.AIR) {
											if (Math.random() > 0.9D) {
												Utils.playEffect(Effect.POTION_BREAK, l);
											}
										}
									}
									break;
							}
							break;
						case BUKKIT:
							switch (active.owner) {
								case PLAYER:
									Player p = lq.getServer().getPlayer(active.uuid);
									if (p != null) {
										Utils.playEffect(effect, p.getLocation(), active.effect.getAmp());
									}
									break;
								case MOB:
									LivingEntity ent = getEntity(active.uuid);
									if (ent != null) {
										Utils.playEffect(effect, ent.getLocation(), active.effect.getAmp());
									}
									break;
								case LOCATATION:
									List<Location> ball = getSphere(active.location, active.radius);
									for (Location l : ball) {
										if (l.getBlock().getType() == null || l.getBlock().getType() == Material.AIR) {
											if (Math.random() > 0.5D) {
												Utils.playEffect(effect, l, active.effect.getAmp());
											}
										}
									}
									break;
							}
							break;
						case CUSTOM:
							switch (active.owner) {
								case PLAYER:
									Player p = lq.getServer().getPlayer(active.uuid);
									if (p != null) {
										switch (active.effect) {
											case SNEAK:
												p.setSneaking(true);
												break;
											case BLEED:
												if (Math.random() > 0.8D) {
													Utils.playEffect(effect, p.getLocation(), 5);
												}
												p.damage(1.0D);
												break;
											case SLOWBLEED:
												if (effecttickcount % 240 == 0) {
													if (Math.random() > 0.1D) {
														Utils.playEffect(effect, p.getLocation(), 5);
													}
													p.damage(1.0D);
												}
												break;
											case STUNNED:
												if (Math.random() > 0.8D) {
													Utils.playEffect(effect, p.getLocation(), 0);
												}
												break;
										}
									}
									break;
								case MOB:
									LivingEntity ent = getEntity(active.uuid);
									if (ent != null) {
										switch (active.effect) {
											case BLEED:
												if (Math.random() > 0.8D) {
													Utils.playEffect(effect, ent.getLocation(), 5);
												}
												ent.damage(1.0D);
												break;
											case SLOWBLEED:
												if (effecttickcount % 240 == 0) {
													if (Math.random() > 0.1D) {
														Utils.playEffect(effect, ent.getLocation(), 5);
													}
													ent.damage(1.0D);
												}
												break;
											case STUNNED:
												if (Math.random() > 0.8D) {
													Utils.playEffect(effect, ent.getLocation(), 0);
												}
												break;
										}
									}
									break;
								case LOCATATION:
									switch (active.effect) {
										case BLEED:
											if (Math.random() > 0.8D) {
												Utils.playEffect(effect, active.location, 5);
											}
											break;
										case SLOWBLEED:
											if (Math.random() > 0.9D) {
												Utils.playEffect(effect, active.location, 5);
											}
											break;
										case STUNNED:
											if (Math.random() > 0.8D) {
												Utils.playEffect(effect, active.location, 0);
											}
											break;
									}
									break;
							}
							break;
					}
				}
			}
		}
	}

	public List<LivingEntity> getNearbyEntities(Location l, double distance) {
		List<LivingEntity> results = new ArrayList<LivingEntity>();
		/* ignore entities in non-allowed worlds */
		if (!lq.validWorld(l.getWorld().getName())) {
			return results;
		}
		double compdistace = distance * distance;
		List<Entity> potentials = l.getWorld().getEntities();
		for (Entity potential : potentials) {
			if (potential instanceof LivingEntity) {
				if (potential.getLocation().distanceSquared(l) <= compdistace) {
					results.add((LivingEntity) potential);
				}
			}
		}
		return results;
	}

	public LivingEntity getEntity(UUID uuid) {
		for (World w : lq.getServer().getWorlds()) {
			if (!lq.validWorld(w.getName())) {
				continue;
			}
			List<Entity> potentials = w.getEntities();
			for (Entity potential : potentials) {
				if (potential instanceof LivingEntity) {
					if (uuid == potential.getUniqueId()) {
						return (LivingEntity) potential;
					}
				}
			}
		}
		return null;
	}

	public List<Effects> getPlayerEffects(UUID uuid) {
		List<Effects> results = new ArrayList<Effects>();
		for (EffectProcess e : activeProcess) {
			if (e.owner == OwnerType.PLAYER) {
				if (e.uuid.equals(uuid)) {
					results.add(e.effect);
				}
			}
		}
		return results;
	}

	public List<Location> getSphere(Location loc, int r) {
		List<Location> circleblocks = new ArrayList<Location>();
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		for (int x = cx - r; x <= cx + r; x++) {
			for (int z = cz - r; z <= cz + r; z++) {
				for (int y = (cy - r); y < (cy + r); y++) {
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));
					if (dist < (r * r)) {
						Location l = new Location(loc.getWorld(), x, y, z);
						circleblocks.add(l);
					}
				}
			}
		}
		return circleblocks;
	}

}
