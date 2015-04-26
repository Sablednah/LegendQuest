package me.sablednah.legendquest.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SafeLoc {

	// safe materials.
	public static Set<Material>		SAFE_MATERIALS	= new HashSet<Material>();

	static {
		SAFE_MATERIALS.add(Material.AIR);
		SAFE_MATERIALS.add(Material.SAPLING);
		SAFE_MATERIALS.add(Material.POWERED_RAIL);
		SAFE_MATERIALS.add(Material.DETECTOR_RAIL);
		SAFE_MATERIALS.add(Material.LONG_GRASS);
		SAFE_MATERIALS.add(Material.DEAD_BUSH);
		SAFE_MATERIALS.add(Material.YELLOW_FLOWER);
		SAFE_MATERIALS.add(Material.RED_ROSE);
		SAFE_MATERIALS.add(Material.BROWN_MUSHROOM);
		SAFE_MATERIALS.add(Material.RED_MUSHROOM);
		SAFE_MATERIALS.add(Material.TORCH);
		SAFE_MATERIALS.add(Material.REDSTONE_WIRE);
		SAFE_MATERIALS.add(Material.SEEDS);
		SAFE_MATERIALS.add(Material.SIGN_POST);
		SAFE_MATERIALS.add(Material.WOODEN_DOOR);
		SAFE_MATERIALS.add(Material.LADDER);
		SAFE_MATERIALS.add(Material.RAILS);
		SAFE_MATERIALS.add(Material.WALL_SIGN);
		SAFE_MATERIALS.add(Material.LEVER);
		SAFE_MATERIALS.add(Material.STONE_PLATE);
		SAFE_MATERIALS.add(Material.IRON_DOOR_BLOCK);
		SAFE_MATERIALS.add(Material.WOOD_PLATE);
		SAFE_MATERIALS.add(Material.REDSTONE_TORCH_OFF);
		SAFE_MATERIALS.add(Material.REDSTONE_TORCH_ON);
		SAFE_MATERIALS.add(Material.STONE_BUTTON);
		SAFE_MATERIALS.add(Material.SNOW);
		SAFE_MATERIALS.add(Material.SUGAR_CANE_BLOCK);
		SAFE_MATERIALS.add(Material.DIODE_BLOCK_OFF);
		SAFE_MATERIALS.add(Material.DIODE_BLOCK_ON);
		SAFE_MATERIALS.add(Material.PUMPKIN_STEM);
		SAFE_MATERIALS.add(Material.MELON_STEM);
		SAFE_MATERIALS.add(Material.VINE);
		SAFE_MATERIALS.add(Material.FENCE_GATE);
		SAFE_MATERIALS.add(Material.WATER_LILY);
		SAFE_MATERIALS.add(Material.NETHER_WARTS);
		SAFE_MATERIALS.add(Material.CARPET);

		SAFE_MATERIALS.add(Material.WATER);
		SAFE_MATERIALS.add(Material.STATIONARY_WATER);
	}
	public static int				RADIUS			= 3;
	public static final Vector3D[]	VOLUME;

	public static class Vector3D {
		public int	x;
		public int	y;
		public int	z;

		public Vector3D(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	static {
		List<Vector3D> pos = new ArrayList<Vector3D>();
		for (int x = -RADIUS; x <= RADIUS; x++) {
			for (int y = -RADIUS; y <= RADIUS; y++) {
				for (int z = -RADIUS; z <= RADIUS; z++) {
					pos.add(new Vector3D(x, y, z));
				}
			}
		}
		Collections.sort(pos, new Comparator<Vector3D>() {
			@Override
			public int compare(Vector3D a, Vector3D b) {
				return (a.x * a.x + a.y * a.y + a.z * a.z) - (b.x * b.x + b.y * b.y + b.z * b.z);
			}
		});
		VOLUME = pos.toArray(new Vector3D[0]);
	}

	public static Location getSafeLocation(Location loc) {
		if (loc == null || loc.getWorld() == null) {
			return null;
		}
		final World world = loc.getWorld();
		int x = loc.getBlockX();
		int y = (int) Math.round(loc.getY());
		int z = loc.getBlockZ();
		final int origX = x;
		final int origY = y;
		final int origZ = z;
		while (isAboveAir(world, x, y, z)) {
			y -= 1;
			if (y < 0) {
				y = origY;
				break;
			}
		}
		if (isUnsafe(world, x, y, z)) {
			x = Math.round(loc.getX()) == origX ? x - 1 : x + 1;
			z = Math.round(loc.getZ()) == origZ ? z - 1 : z + 1;
		}
		int i = 0;
		while (isUnsafe(world, x, y, z)) {
			i++;
			if (i >= VOLUME.length) {
				x = origX;
				y = origY + RADIUS;
				z = origZ;
				break;
			}
			x = origX + VOLUME[i].x;
			y = origY + VOLUME[i].y;
			z = origZ + VOLUME[i].z;
		}
		while (isUnsafe(world, x, y, z)) {
			y += 1;
			if (y >= world.getMaxHeight()) {
				x += 1;
				break;
			}
		}
		while (isUnsafe(world, x, y, z)) {
			y -= 1;
			if (y <= 1) {
				x += 1;
				y = world.getHighestBlockYAt(x, z);
				if (x - 48 > loc.getBlockX()) {
					System.out.print("Warning - void hole @ " + x + "," + z);
				}
			}
		}
		return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
	}
	
	static boolean isAboveAir(World world, int x, int y, int z) {
		if (y > world.getMaxHeight()) {
			return true;
		}
		return SAFE_MATERIALS.contains(world.getBlockAt(x, y - 1, z).getType());
	}

	public static boolean isUnsafe(World world, int x, int y, int z) {
		if (isDamaging(world, x, y, z)) {
			return true;
		}
		return isAboveAir(world, x, y, z);
	}

	public static boolean isDamaging(World world, int x, int y, int z) {
		final Block below = world.getBlockAt(x, y - 1, z);
		if (below.getType() == Material.LAVA || below.getType() == Material.STATIONARY_LAVA || below.getType() == Material.FIRE || below.getType() == Material.BED_BLOCK || below.getType() == Material.CACTUS) {
			return true;
		}
		if ((!SAFE_MATERIALS.contains(world.getBlockAt(x, y, z).getType())) || (!SAFE_MATERIALS.contains(world.getBlockAt(x, y + 1, z).getType()))) {
			return true;
		}
		return false;
	}

}