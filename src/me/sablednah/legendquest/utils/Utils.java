package me.sablednah.legendquest.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.util.Vector;

public class Utils {

	/**
	 * Converts InputStream to String
	 * 
	 * One-line 'hack' to convert InputStreams to strings.
	 * 
	 * @param is
	 *            The InputStream to convert
	 * @return returns a String version of 'is'
	 */
	public static String convertStreamToString(InputStream is) {
		return new Scanner(is).useDelimiter("\\A").next();
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
	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static void setTempnvluln(LivingEntity e, int d) {
		if (e != null) {
			e.setNoDamageTicks(e.getMaximumNoDamageTicks());
			e.setLastDamage(d);
		}
	}

	public static Block getNearestEmptySpace(Block b, int maxradius) {
		BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST };
		BlockFace[][] orth = { { BlockFace.NORTH, BlockFace.EAST }, { BlockFace.UP, BlockFace.EAST }, { BlockFace.NORTH, BlockFace.UP } };
		for (int r = 0; r <= maxradius; r++) {
			for (int s = 0; s < 6; s++) {
				BlockFace f = faces[s % 3];
				BlockFace[] o = orth[s % 3];
				if (s >= 3)
					f = f.getOppositeFace();
				Block c = b.getRelative(f, r);
				for (int x = -r; x <= r; x++) {
					for (int y = -r; y <= r; y++) {
						Block a = c.getRelative(o[0], x).getRelative(o[1], y);
						if (a.getTypeId() == 0 && a.getRelative(BlockFace.UP).getTypeId() == 0)
							return a;
					}
				}
			}
		}
		return null;// no empty space within a cube of (2*(maxradius+1))^3
	}

	public static Location lookAt(Location loc, Location lookat) {
		// Clone the loc to prevent applied changes to the input loc
		loc = loc.clone();

		// Values of change in distance (make it relative)
		double dx = lookat.getX() - loc.getX();
		double dy = lookat.getY() - loc.getY();
		double dz = lookat.getZ() - loc.getZ();

		// Set yaw
		if (dx != 0) {
			// Set yaw start value based on dx
			if (dx < 0) {
				loc.setYaw((float) (1.5 * Math.PI));
			} else {
				loc.setYaw((float) (0.5 * Math.PI));
			}
			loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
		} else if (dz < 0) {
			loc.setYaw((float) Math.PI);
		}

		// Get the distance from dx/dz
		double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

		// Set pitch
		loc.setPitch((float) -Math.atan(dy / dxz));

		// Set values, convert to degrees (invert the yaw since Bukkit uses a
		// different yaw dimension format)
		loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
		loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

		return loc;

	}

	public static String ordinal(Location l) {
		double rot = (l.getYaw() - 90) % 360;
		if (rot < 0) {
			rot += 360.0;
		}
		return getDirection(rot);
	}

	private static String getDirection(double rot) {
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

	public static void stomp(Location from, Entity stomper, int radius, int damage) {
		Bukkit.broadcastMessage("Stomp!");
		for (Entity bounced : stomper.getNearbyEntities(radius, radius, radius)) {
			if (bounced != stomper) {
				if (bounced instanceof Player) {
					Player p = (Player) bounced;
					p.damage(damage, stomper);
					Vector v = p.getVelocity();
					v.setY(v.getY() + 1.5);
					p.setVelocity(v);
				} else if ((bounced instanceof Monster)) {
					Monster c = (Monster) bounced;
					c.damage(damage, stomper); // damage
					Vector vc = c.getVelocity();
					vc.setY(vc.getY() + 1.5);
					c.setVelocity(vc);
				}
			}
		}

		// from.getWorld().strikeLightningEffect(from);
		Block block = from.getBlock();

		ArrayList<BlockState> blocks = new ArrayList<BlockState>();
		for (int x = (radius); x >= (0 - radius); x--) {
			for (int zed = (radius); zed >= (0 - radius); zed--) {
				for (int y = (radius); y >= (0 - radius); y--) {
					if (x == 0 && zed == 0) {
						// skip own blocks...
					} else {
						Block b = block.getRelative(x, y, zed);
						Location distanceBlock;
						if (y >= 0) { // sphere if y<0 cylinder above
							distanceBlock = block.getRelative(x, 0, zed).getLocation();
						} else {
							distanceBlock = b.getLocation();
						}
						if (block.getLocation().distance(distanceBlock) < radius) {
							BlockState thisstate = b.getState();
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

		for (BlockState bs : blocks) {
			Material m = bs.getType();
			byte d = bs.getData().getData();
			int bsX = bs.getX();
			int bsY = bs.getY();
			int bsZ = bs.getZ();
			double depth = (block.getY() + radius) - bsY + 1;
			double speed = .5 + ((1.00D / depth) * 2); // (1.00D/distance)
			Location fbl = new Location(block.getWorld(), bsX, bsY, bsZ);
			FallingBlock fb = fbl.getWorld().spawnFallingBlock(fbl, m, d);
			fb.setVelocity(new Vector(0.00D, speed, 0.00D));
		}
		blocks = null;
	}

	public static void setEquip(LivingEntity mob, ItemStack item, int slot) {
		EntityEquipment eq = mob.getEquipment();
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
}
