package me.sablednah.legendquest.utils;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * A serializable and immutable wrapper for org.bukkit.Location objects.
 */
public class SerializableLocation implements Serializable {

    private static final long serialVersionUID = 8130077079595390518L;

    /**
     * Creates a new instance based on the given string. The general contract of this method is that if the output of the {@link #toString()} is passed it will result in a clone.
     * 
     * @param string the String.
     * @return a new instance or null if string could not be parsed.
     */
    public static SerializableLocation fromString(final String string) {
        if (string != null && !string.isEmpty()) {
            final String[] s = string.trim().split(" ");
            try {
                String worldName;
                double x, y, z;
                float pitch, yaw;
                if (s.length == 4) {
                    worldName = s[0];
                    x = Double.parseDouble(s[1]);
                    y = Double.parseDouble(s[2]);
                    z = Double.parseDouble(s[3]);
                    pitch = 0f;
                    yaw = 0f;
                } else if (s.length == 6) {
                    worldName = s[0];
                    x = Double.parseDouble(s[1]);
                    y = Double.parseDouble(s[2]);
                    z = Double.parseDouble(s[3]);
                    pitch = Float.parseFloat(s[4]);
                    yaw = Float.parseFloat(s[5]);
                } else {
                    return null;
                }
                return new SerializableLocation(worldName, x, y, z, pitch, yaw);
            } catch (final NumberFormatException e) {}
        }
        return null;
    }

    private final float pitch, yaw;
    private final String worldName;
    private final double x, y, z;

    /**
     * Creates a new instance based on the given Location.
     * 
     * @param location the Locatuion.
     */
    public SerializableLocation(final Location location) {
        this(location.getWorld().getName(), location.getX(), location.getBlockY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    /**
     * Creates a new instance. Pitch and yaw will be assumed as 0.
     * 
     * @param worldName the name of the world.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param z the z-coordinate.
     */
    public SerializableLocation(final String worldName, final double x, final double y, final double z) {
        this(worldName, x, y, z, 0f, 0f);
    }

    /**
     * Creates a new instance.
     * 
     * @param worldName the name of the world.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param z the z-coordinate.
     * @param pitch the pitch.
     * @param yaw the yaw.
     */
    public SerializableLocation(final String worldName, final double x, final double y, final double z, final float pitch, final float yaw) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    /**
     * <p>
     * The following types can be used to compare the instance with the passed value:
     * </p>
     * <ul>
     * <li><b>SerializableLocation:</b> All values will be compared.</li>
     * <li><b>Location:</b> All values will be compared by internally creating a new instance of SerializableLocation based on the given Location.</li>
     * <li><b>World:</b> The name of the World will be compared.</li>
     * </ul>
     * 
     * @return true if the passed value is equal to this instance.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (obj instanceof SerializableLocation) {
                final SerializableLocation l = (SerializableLocation) obj;
                return l.worldName.equals(worldName) && l.x == x && l.y == y && l.z == z && l.pitch == pitch && l.yaw == yaw;
            } else if (obj instanceof Location) {
                final Location l = (Location) obj;
                return new SerializableLocation(l).equals(this);
            } else if (obj instanceof World) {
                return ((World) obj).getName().equals(worldName);
            }
        }
        return false;
    }

    /**
     * @return the pitch.
     */
    public final float getPitch() {
        return pitch;
    }

    /**
     * @return the World object of this instance or null if there is no world loaded with this name.
     */
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    /**
     * @return the name of the world.
     */
    public final String getWorldName() {
        return worldName;
    }

    /**
     * @return the x-coordinate.
     */
    public final double getX() {
        return x;
    }

    /**
     * @return the y-coordinate.
     */
    public final double getY() {
        return y;
    }

    /**
     * @return the yaw.
     */
    public final float getYaw() {
        return yaw;
    }

    /**
     * @return the z-coordinate.
     */
    public final double getZ() {
        return z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(pitch);
        result = prime * result + ((worldName == null) ? 0 : worldName.hashCode());
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + Float.floatToIntBits(yaw);
        temp = Double.doubleToLongBits(z);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Creates a new Location based on this instance.
     * 
     * @return the Location.
     */
    public Location toLocation() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    /**
     * Creates a String representing this instance in the following format: [worldname][space][x][space][y][space][z][space][pitch][space][yaw]
     * 
     * @return a String representing this instance.
     */
    @Override
    public String toString() {
        return (new StringBuilder()).append(worldName).append(" ").append(String.valueOf(x)).append(" ").append(String.valueOf(y)).append(" ")
                .append(String.valueOf(z)).append(" ").append(String.valueOf(pitch)).append(" ").append(String.valueOf(yaw)).toString();
    }

}