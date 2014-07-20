package me.sablednah.legendquest.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Utils {

	public static String barGraph(final double x, final double y, final int scale, final String prefix, final String suffix) {
		return barGraph((int) x, (int) y, scale, prefix, suffix);
	}

	public static String barGraph(final int x, final int y, final int scale, final String prefix, final String suffix) {
		final int percent = (int) ((x / (float) y) * scale);
		final StringBuilder mesage = new StringBuilder(12 + scale + prefix.length() + suffix.length());
		mesage.append(ChatColor.WHITE);
		mesage.append(prefix).append(": [");
		mesage.append(ChatColor.GREEN);
		if (percent > 0) {
			mesage.append(Utils.stringRepeat("|", percent));
		}
		mesage.append(ChatColor.RED);
		if (percent < scale) {
			mesage.append(Utils.stringRepeat("|", (scale - percent)));
		}
		mesage.append(ChatColor.WHITE);
		mesage.append("]").append(suffix);
		return mesage.toString();
	}

	/**
	 * Joins two arrays
	 * 
	 * @param first
	 *            array
	 * @param second
	 *            array
	 * @return Arrays joined
	 */
	public static <T> T[] concat(final T[] first, final T[] second) {
		final T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/**
	 * Converts InputStream to String One-line 'hack' to convert InputStreams to strings.
	 * 
	 * @param is
	 *            The InputStream to convert
	 * @return returns a String version of 'is'
	 */
	public static String convertStreamToString(final InputStream is) {
		return new Scanner(is).useDelimiter("\\A").next();
	}

	private static String getDirection(final double rot) {
		if (0 <= rot && rot < 22.5) {
			return "North";
		} else if (22.5 <= rot && rot < 67.5) {
			return "NorthEast";
		} else if (67.5 <= rot && rot < 112.5) {
			return "East";
		} else if (112.5 <= rot && rot < 157.5) {
			return "SouthEast";
		} else if (157.5 <= rot && rot < 202.5) {
			return "South";
		} else if (202.5 <= rot && rot < 247.5) {
			return "SouthWest";
		} else if (247.5 <= rot && rot < 292.5) {
			return "West";
		} else if (292.5 <= rot && rot < 337.5) {
			return "NorthWest";
		} else if (337.5 <= rot && rot < 360) {
			return "North";
		} else {
			return null;
		}
	}

	public static Block getNearestEmptySpace(final Block b, final int maxradius) {
		final BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST };
		final BlockFace[][] orth = { { BlockFace.NORTH, BlockFace.EAST }, { BlockFace.UP, BlockFace.EAST }, { BlockFace.NORTH, BlockFace.UP } };
		for (int r = 0; r <= maxradius; r++) {
			for (int s = 0; s < 6; s++) {
				BlockFace f = faces[s % 3];
				final BlockFace[] o = orth[s % 3];
				if (s >= 3) {
					f = f.getOppositeFace();
				}
				final Block c = b.getRelative(f, r);
				for (int x = -r; x <= r; x++) {
					for (int y = -r; y <= r; y++) {
						final Block a = c.getRelative(o[0], x).getRelative(o[1], y);
						Block ayup = a.getRelative(BlockFace.UP);
						if (!(a.getType().isSolid()) && !(ayup.getType().isSolid())) {
							return a;
						}
					}
				}
			}
		}
		return null;// no empty space within a cube of (2*(maxradius+1))^3
	}

	public static String join(final String r[], final String d) {
		final StringBuilder sb = new StringBuilder();
		int i;
		for (i = 0; i < r.length - 1; i++) {
			sb.append(r[i] + d);
		}
		return sb.toString() + r[i];
	}

	public static Location lookAt(Location loc, final Location lookat) {
		// Clone the loc to prevent applied changes to the input loc
		loc = loc.clone();

		// Values of change in distance (make it relative)
		final double dx = lookat.getX() - loc.getX();
		final double dy = lookat.getY() - loc.getY();
		final double dz = lookat.getZ() - loc.getZ();

		// Set yaw
		if (dx != 0) {
			// Set yaw start value based on dx
			if (dx < 0) {
				loc.setYaw((float) (1.5 * Math.PI));
			} else {
				loc.setYaw((float) (0.5 * Math.PI));
			}
			loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
		} else if (dz < 0) {
			loc.setYaw((float) Math.PI);
		}

		// Get the distance from dx/dz
		final double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

		// Set pitch
		loc.setPitch((float) -Math.atan(dy / dxz));

		// Set values, convert to degrees (invert the yaw since Bukkit uses a
		// different yaw dimension format)
		loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
		loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

		return loc;

	}

	public static String ordinal(final Location l) {
		double rot = (l.getYaw() - 90) % 360;
		if (rot < 0) {
			rot += 360.0;
		}
		return getDirection(rot);
	}

	public static void setEquip(final LivingEntity mob, final ItemStack item, final int slot) {
		final EntityEquipment eq = mob.getEquipment();
		if (slot == 0) {
			eq.setItemInHand(item);
		}
		if (slot == 1) {
			eq.setBoots(item);
		}
		if (slot == 2) {
			eq.setLeggings(item);
		}
		if (slot == 3) {
			eq.setChestplate(item);
		}
		if (slot == 4) {
			eq.setHelmet(item);
		}
	}

	public static void setTempnvluln(final LivingEntity e, final int d) {
		if (e != null) {
			e.setNoDamageTicks(e.getMaximumNoDamageTicks());
			e.setLastDamage(d);
		}
	}

	public static void stomp(final Location from, final Entity stomper, final int radius, final int damage) {
		Bukkit.broadcastMessage("Stomp!");
		for (final Entity bounced : stomper.getNearbyEntities(radius, radius, radius)) {
			if (bounced != stomper) {
				if (bounced instanceof Player) {
					final Player p = (Player) bounced;
					p.damage(damage, stomper);
					final Vector v = p.getVelocity();
					v.setY(v.getY() + 1.5);
					p.setVelocity(v);
				} else if ((bounced instanceof Monster)) {
					final Monster c = (Monster) bounced;
					c.damage(damage, stomper); // damage
					final Vector vc = c.getVelocity();
					vc.setY(vc.getY() + 1.5);
					c.setVelocity(vc);
				}
			}
		}

		// from.getWorld().strikeLightningEffect(from);
		final Block block = from.getBlock();

		ArrayList<BlockState> blocks = new ArrayList<BlockState>();
		for (int x = (radius); x >= (0 - radius); x--) {
			for (int zed = (radius); zed >= (0 - radius); zed--) {
				for (int y = (radius); y >= (0 - radius); y--) {
					if (x == 0 && zed == 0) {
						// skip own blocks...
					} else {
						final Block b = block.getRelative(x, y, zed);
						Location distanceBlock;
						if (y >= 0) { // sphere if y<0 cylinder above
							distanceBlock = block.getRelative(x, 0, zed).getLocation();
						} else {
							distanceBlock = b.getLocation();
						}
						if (block.getLocation().distance(distanceBlock) < radius) {
							final BlockState thisstate = b.getState();
							if (b.getType() != Material.AIR) {
								if (thisstate instanceof Chest || thisstate instanceof BrewingStand || thisstate instanceof CreatureSpawner || thisstate instanceof Dispenser || thisstate instanceof DoubleChest || thisstate instanceof Furnace
										|| thisstate instanceof Jukebox || thisstate instanceof NoteBlock || thisstate instanceof Sign) {
									// skip me
								} else {
									blocks.add(thisstate);
									b.setType(Material.AIR);
								}
							}
						}
					}
				}
			}
		}

		for (final BlockState bs : blocks) {
			final Material m = bs.getType();
			@SuppressWarnings("deprecation")
			final byte d = bs.getData().getData();
			final int bsX = bs.getX();
			final int bsY = bs.getY();
			final int bsZ = bs.getZ();
			final double depth = (block.getY() + radius) - bsY + 1;
			final double speed = .5 + ((1.00D / depth) * 2); // (1.00D/distance)
			final Location fbl = new Location(block.getWorld(), bsX, bsY, bsZ);
			@SuppressWarnings("deprecation")
			final FallingBlock fb = fbl.getWorld().spawnFallingBlock(fbl, m, d);

			fb.setVelocity(new Vector(0.00D, speed, 0.00D));
		}
		blocks = null;
	}

	public static String stringRepeat(final String newString, final int n) {
		final StringBuilder builder = new StringBuilder(n * newString.length());
		for (int x = 0; x < n; x++) {
			builder.append(newString);
		}
		return builder.toString();
	}

	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false;
			}
		}
		return true;
	}

	public static boolean isParsableToInt(final String i) {
		/*
		 * try { Integer.parseInt(i); return true; } catch (final NumberFormatException nfe) { return false; }
		 */
		return isInteger(i);
	}

	public static void extractFile(InputStream inStream, OutputStream outStream) throws IOException {
		byte[] buf = new byte[1024];
		int l;
		while ((l = inStream.read(buf)) >= 0) {
			outStream.write(buf, 0, l);
		}
		inStream.close();
		outStream.close();
	}

	/*
	 * look for player by name - should only be used for commands for targeting offline players returns null if player
	 * has never been on server
	 */
	@Deprecated
	public static UUID getPlayerUUID(String name) {
		UUID uuid = null;
		Player p = Bukkit.getPlayer(name);
		if (p != null) {
			uuid = p.getUniqueId();
		} else {
			OfflinePlayer[] offline = Bukkit.getOfflinePlayers();
			for (OfflinePlayer oneoff : offline) {
				if (oneoff.getName() != null && oneoff.getName().equals(name)) {
					uuid = oneoff.getUniqueId();
				}
			}
		}
		return uuid;
	}

	public static void playEffect(Effect e, Location l) {
		playEffect(e, l, 0, 32);
	}	
	public static void playEffect(Effect e, Location l, int data) {
		playEffect(e, l, data, 32);
	}
	public static void playEffect(Effect e, Location l, int data, int radius) {
		switch (e) {
			case CLICK2:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case CLICK1:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case BOW_FIRE:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case DOOR_TOGGLE:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case EXTINGUISH:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case RECORD_PLAY:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case GHAST_SHRIEK:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case GHAST_SHOOT:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case BLAZE_SHOOT:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case ZOMBIE_CHEW_WOODEN_DOOR:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case ZOMBIE_CHEW_IRON_DOOR:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case ZOMBIE_DESTROY_DOOR:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case SMOKE:
				//l.getWorld().playEffect(l, e, data,radius);
				l.getWorld().playEffect(l, e, BlockFace.UP,32);
				break;
			case STEP_SOUND:
				l.getWorld().playEffect(l, e, data,radius);
				break;
			case POTION_BREAK:  // 5 bl00d
				l.getWorld().playEffect(l, e, 0,radius);
				break;
			case ENDER_SIGNAL:
				l.getWorld().playEffect(l, e, 0,radius);
				break;
			case MOBSPAWNER_FLAMES:
				l.getWorld().playEffect(l, e, 15,radius);
				break;
		}
	}
	
	public static Player getTargetPlayer(Player player, int distance) {
		LivingEntity e = getTarget(player, distance);
		if (e instanceof Player) {
			return (Player) e;
		} else {
			return null;
		}
	}

	public static LivingEntity getTarget(Player player, int distance) {
		List<Entity> nearbyE = player.getNearbyEntities(distance,distance,distance);
		ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();

		for (Entity e : nearbyE) {
			if (e instanceof LivingEntity) {
				livingE.add((LivingEntity) e);
			}
		}

		BlockIterator bItr = new BlockIterator(player, distance);
		Block block;
		Location loc;
		int bx, by, bz;
		double ex, ey, ez;
		// loop through player's line of sight
		while (bItr.hasNext()) {
			block = bItr.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			// check for entities near this block in the line of sight
			for (LivingEntity e : livingE) {
				loc = e.getLocation();
				ex = loc.getX();
				ey = loc.getY();
				ez = loc.getZ();
				if ((bx - .75 <= ex && ex <= bx + 1.75) && (bz - .75 <= ez && ez <= bz + 1.75) && (by - 1 <= ey && ey <= by + 2.5)) {
					// entity is close enough, set target and stop
					return e;
				}
			}
		}
		return null;
	}
}