package net.chaossquad.mclib;

import net.chaossquad.mclib.blocks.BlockBox;
import net.chaossquad.mclib.blocks.BlockStructure;
import net.chaossquad.mclib.blocks.BlockStructureEntry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities related to worlds.
 */
public final class WorldUtils {

    private WorldUtils() {}

    // WEATHER

    /**
     * The available weather types of a world.
     * {@link org.bukkit.WeatherType} cannot be used because bukkit only has {@link org.bukkit.WeatherType#CLEAR} and {@link org.bukkit.WeatherType#DOWNFALL},
     * minecraft has {@link org.bukkit.WeatherType#CLEAR}, {@link WeatherType#RAIN} and {@link WeatherType#THUNDER}.
     * The weather types can be converted by {@link WeatherType#getBukkitWeatherType()}
     */
    public enum WeatherType {
        CLEAR(1, org.bukkit.WeatherType.CLEAR),
        RAIN(2, org.bukkit.WeatherType.DOWNFALL),
        THUNDER(3, org.bukkit.WeatherType.DOWNFALL);

        WeatherType(int value, org.bukkit.WeatherType bukkitWeatherType) {
            this.value = value;
            this.bukkitWeatherType = bukkitWeatherType;
        }

        private final int value;
        private final org.bukkit.WeatherType bukkitWeatherType;

        /**
         * Return an int value for the weather type.
         * 0 = {@link WeatherType#CLEAR}
         * 1 = {@link WeatherType#RAIN}
         * 2 = {@link WeatherType#THUNDER}
         * @return
         */
        public int getValue() {
            return value;
        }

        /**
         * Returns the {@link org.bukkit.WeatherType} of this WeatherType.
         * @return Bukkit WeatherType
         */
        public org.bukkit.WeatherType getBukkitWeatherType() {
            return bukkitWeatherType;
        }

        /**
         * Returns the WeatherType of the {@link org.bukkit.WeatherType}.
         * Since {@link WeatherType#THUNDER} is not available in {@link org.bukkit.WeatherType}, {@link org.bukkit.WeatherType#DOWNFALL} is always translated to {@link WeatherType#RAIN}.
         * @param weatherType Bukkit WeatherType
         * @return WeatherType
         */
        public static WeatherType fromBukkitWeatherType(org.bukkit.WeatherType weatherType) {

            return switch (weatherType) {
                case CLEAR -> CLEAR;
                case DOWNFALL -> RAIN;
            };

        }

    }

    /**
     * Gets the current weather type of the specified world.
     * @param world world the weather should be got from.
     * @return weather type in the specified world
     */
    public static WeatherType getWeather(World world) {

        if (world == null) {
            return null;
        }

        if (world.hasStorm() && world.getWeatherDuration() > 0) {
            if (world.isThundering() && world.getThunderDuration() > 0) {
                return WeatherType.THUNDER;
            } else {
                return WeatherType.RAIN;
            }
        } else {
            return WeatherType.CLEAR;
        }

    }

    /**
     * Updates the weather in the specified world to the specified weather type.
     * @param world world the weather should be changed in.
     * @param weatherType type the weather should be changed to
     */
    public static void setWeather(World world, WeatherType weatherType) {
        if (world == null || weatherType == null) return;

        switch (weatherType) {
            case CLEAR -> {
                world.setStorm(false);
                world.setThundering(false);
                world.setClearWeatherDuration(112801);
                world.setWeatherDuration(0);
                world.setThunderDuration(0);
            }
            case RAIN -> {
                world.setStorm(true);
                world.setThundering(false);
                world.setClearWeatherDuration(0);
                world.setWeatherDuration(15376);
                world.setThunderDuration(15376);
            }
            case THUNDER -> {
                world.setStorm(true);
                world.setThundering(true);
                world.setClearWeatherDuration(0);
                world.setWeatherDuration(13834);
                world.setThunderDuration(13834);
            }
        }

    }

    // LOCATIONS

    /**
     * Compares locations by their block coordinates.
     * @param l1 first location
     * @param l2 second location
     * @return block coordinates match
     */
    public static boolean doBlockCoordinatesMatch(Location l1, Location l2) {
        return l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ();
    }

    /**
     * Returns the distance between two block locations.
     * @param location1 first location
     * @param location2 second location
     * @return distance in blocks
     */
    public static int getBlockDistance(Location location1, Location location2) {
        int dx = Math.abs(location1.getBlockX() - location2.getBlockX());
        int dy = Math.abs(location1.getBlockY() - location2.getBlockY());
        int dz = Math.abs(location1.getBlockZ() - location2.getBlockZ());

        double distanceSquared = dx * dx + dy * dy + dz * dz;
        double distance = Math.sqrt(distanceSquared);

        return (int) Math.round(distance);
    }

    /**
     * Fills the specified area with the specified block.
     * @param world world
     * @param box block box
     * @param material material
     */
    public static void fillAreaWith(World world, BlockBox box, Material material) {
        box = box.clone();
        box.sort();

        box.forEach(((x, y, z) -> {
            world.getBlockAt(x, y, z).setType(material);
        }));
    }

    /**
     * Fills the specified area with the specified block.
     * @param from from location
     * @param to to location
     * @param material block
     * @deprecated Use {@link this#fillAreaWith(World, BlockBox, Material)} instead
     */
    @Deprecated
    public static void fillAreaWith(Location from, Location to, Material material) {
        if (from.getWorld() == null) return;
        fillAreaWith(from.getWorld(), new BlockBox(from, to), material);
    }

    /**
     * Returns a new location with the specified world.
     * @param location location
     * @param world world
     * @return new location with world
     */
    public static Location locationWithWorld(Location location, World world) {
        return new Location(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    // BLOCK DISPLAYS

    public static final NamespacedKey BLOCK_DISPLAY_STRUCTURE_RELATIVE_X = new NamespacedKey("net.chaossquad.mclib", "block_structure_relative_x");
    public static final NamespacedKey BLOCK_DISPLAY_STRUCTURE_RELATIVE_Y = new NamespacedKey("net.chaossquad.mclib", "block_structure_relative_y");
    public static final NamespacedKey BLOCK_DISPLAY_STRUCTURE_RELATIVE_Z = new NamespacedKey("net.chaossquad.mclib", "block_structure_relative_z");

    /**
     * Creates a structure of block displays for the specified {@link BlockStructure} object at the specified location and spawns them in the specified {@link World}.
     * @param world the world the entities should be spawned in
     * @param structure the block structure that should be spawned
     * @param location the location where the structure should be spawned
     * @param scoreboardTags list of scoreboard tags that should be added
     * @return a list of the spawned block displays
     */
    public static List<BlockDisplay> spawnBlockStructure(World world, BlockStructure structure, Location location, List<String> scoreboardTags) {
        location = location.clone();
        structure = structure.clone();
        List<BlockDisplay> blockDisplays = new ArrayList<>();

        int rx = 0;
        for (int x = location.getBlockX(); x < location.getBlockX() + structure.getXLength(); x++) {
            int ry = 0;
            for (int y = location.getBlockY(); y < location.getBlockY() + structure.getYLength(); y++) {
                int rz = 0;
                for (int z = location.getBlockZ(); z < location.getBlockZ() + structure.getZLength(); z++) {
                    BlockStructureEntry entry = structure.getBlock(rx, ry, rz);

                    if (entry.type() != Material.AIR) {

                        BlockDisplay blockDisplay = world.spawn(new Location(world, location.getBlockX() + rx, location.getBlockY() + ry, location.getBlockZ() + rz), BlockDisplay.class);
                        blockDisplay.setGravity(false);
                        blockDisplay.setBlock(entry.data());

                        if (scoreboardTags != null) {
                            for (String tag : List.copyOf(scoreboardTags)) {
                                blockDisplay.addScoreboardTag(tag);
                            }
                        }

                        blockDisplay.getPersistentDataContainer().set(BLOCK_DISPLAY_STRUCTURE_RELATIVE_X, PersistentDataType.INTEGER, rx);
                        blockDisplay.getPersistentDataContainer().set(BLOCK_DISPLAY_STRUCTURE_RELATIVE_Y, PersistentDataType.INTEGER, ry);
                        blockDisplay.getPersistentDataContainer().set(BLOCK_DISPLAY_STRUCTURE_RELATIVE_Z, PersistentDataType.INTEGER, rz);

                        blockDisplays.add(blockDisplay);

                    }

                    rz++;
                }
                ry++;
            }
            rx++;
        }

        return List.copyOf(blockDisplays);
    }

}
