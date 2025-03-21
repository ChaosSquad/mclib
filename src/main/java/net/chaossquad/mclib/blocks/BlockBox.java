package net.chaossquad.mclib.blocks;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

/**
 * Does the same as a {@link BoundingBox}, but for blocks.
 * This was created because the {@link BoundingBox} does not include the end block in the contains method.
 * Use this when you are working with blocks and {@link BoundingBox} when you are not working with blocks.
 * Both objects can be converted into each other.
 */
public class BlockBox {

    /**
     * An interface which provides the coordinates for {@link #forEach(BlockLocationProvider)}.
     */
    public interface BlockLocationProvider {

        /**
         * Provides the coordinates.
         * @param x x coordinate
         * @param y y coordinate
         * @param z z coordinate
         */
        void provide(int x, int y, int z);

    }

    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    // CONSTRUCTORS

    /**
     * Creates a block box with the specified coordinates as min and max locations.
     * @param minX min x
     * @param minY min y
     * @param minZ min z
     * @param maxX max x
     * @param maxY max y
     * @param maxZ max z
     */
    public BlockBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    /**
     * Creates a block box containing all blocks between the specified location vectors.
     * @param min min location vector
     * @param max max location vector
     */
    public BlockBox(Vector min, Vector max) {
        this(min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());
    }

    /**
     * Creates a block box containing a single block at the specified location vector.
     * @param location block location vector
     */
    public BlockBox(Vector location) {
        this(location, location);
    }

    /**
     * Creates a block box containing all blocks between the specified locations.
     * @param min first location
     * @param max second location
     */
    public BlockBox(Location min, Location max) {
        this(min.toVector(), max.toVector());
    }

    /**
     * Creates a block box containing a single block at the specified location.
     * @param l block location
     */
    public BlockBox(Location l) {
        this(l, l);
    }

    /**
     * Creates a block box from a {@link BoundingBox} using its block coordinates.
     * @param boundingBox BoundingBox
     */
    public BlockBox(BoundingBox boundingBox) {
        this(boundingBox.getMin().getBlockX(), boundingBox.getMin().getBlockY(), boundingBox.getMin().getBlockZ(), boundingBox.getMax().getBlockX(), boundingBox.getMax().getBlockY(), boundingBox.getMax().getBlockZ());
    }

    // CONTAINS

    /**
     * Checks if this block box contains the specified location (specified by x, y and z coordinates).
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return true if block box contains specified location
     */
    public boolean contains(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ ;
    }

    /**
     * Checks if this block box contains the specified location (specified by location vector).
     * @param vector location vector
     * @return true if block box contains specified location
     */
    public boolean contains(Vector vector) {
        return this.contains(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    /**
     * Checks if this block box contains the specified location (specified by location).
     * @param location location
     * @return true if block box contains specified location
     */
    public boolean contains(Location location) {
        return this.contains(location.toVector());
    }

    // GETTER AND SETTER

    /**
     * Returns the min x coordinate.
     * @return min x
     */
    public int getMinX() {
        return minX;
    }

    /**
     * Sets the min x coordinate.
     * @param minX min x
     */
    public void setMinX(int minX) {
        this.minX = minX;
    }

    /**
     * Returns the min y coordinate.
     * @return min y
     */
    public int getMinY() {
        return minY;
    }

    /**
     * Sets the min y coordinate.
     * @param minY min y
     */
    public void setMinY(int minY) {
        this.minY = minY;
    }

    /**
     * Returns the min z coordinate.
     * @return min z
     */
    public int getMinZ() {
        return minZ;
    }

    /**
     * Sets the min z coordinate.
     * @param minZ min z
     */
    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    /**
     * Returns the max x coordinate.
     * @return max x
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * Sets the max x coordinate.
     * @param maxX max x
     */
    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    /**
     * Returns the max y coordinate.
     * @return max y
     */
    public int getMaxY() {
        return maxY;
    }

    /**
     * Sets the max y coordinate.
     * @param maxY max y
     */
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    /**
     * Returns the max z coordinate.
     * @return max z
     */
    public int getMaxZ() {
        return maxZ;
    }

    /**
     * Sets the max z coordinate.
     * @param maxZ max z
     */
    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    // MINIMUMS AND MAXIMUMS

    /**
     * Returns a location vector of the min location of this block box.
     * @return min location vector
     */
    public Vector getMin() {
        return new Vector(this.minX, this.minY, this.minZ);
    }

    /**
     * Returns a location vector of the max location of this block box.
     * @return max location vector
     */
    public Vector getMax() {
        return new Vector(this.maxX, this.maxY, this.maxZ);
    }

    // SORT

    /**
     * Sorts the min and max coordinates.
     * This will make the min location the location with the lowest coordinates and the max location the location with the highest coordinates.
     * It makes sense to use this if you are planning to iterate through this block box.
     */
    public void sort() {
        int minX = this.minX;
        int minY = this.minY;
        int minZ = this.minZ;
        int maxX = this.maxX;
        int maxY = this.maxY;
        int maxZ = this.maxZ;

        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    // FOR EACH

    /**
     * Iterates through every single block in the BlockBox.
     * @param locationProvider location provider
     */
    public void forEach(BlockLocationProvider locationProvider) {
        BlockBox box = this.clone();
        box.sort();

        for (int x = box.getMinX(); x <= box.getMaxX(); x++) {
            for (int y = box.getMinY(); y <= box.getMaxY(); y++) {
                for (int z = box.getMinZ(); z <= box.getMaxZ(); z++) {
                    locationProvider.provide(x, y, z);
                }
            }
        }

    }

    // CLONE AND CONVERT

    /**
     * Creates a copy of this block box.
     * @return copy of this block box
     */
    @Override
    public BlockBox clone() {
        return new BlockBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    /**
     * Converts the BlockBox into a {@link BoundingBox} containing all blocks
     * @return bounding box
     */
    public BoundingBox toBoundingBox() {
        BlockBox copy = this.clone();
        copy.sort();

        double minX = copy.getMinX();
        double minY = copy.getMinY();
        double minZ = copy.getMinZ();
        double maxX = copy.getMaxX();
        double maxY = copy.getMaxY();
        double maxZ = copy.getMaxZ();

        maxX += 0.99999;
        maxY += 0.99999;
        maxZ += 0.99999;

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

}
