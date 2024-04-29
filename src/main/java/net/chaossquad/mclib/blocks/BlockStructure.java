package net.chaossquad.mclib.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves block data in a relative coordinate system for copying and pasting blocks from or to the world.
 */
public class BlockStructure {
    private BlockStructureEntry[][][] structure;

    /**
     * Creates a new empty block structure with the specified size.
     * @param x x size
     * @param y y size
     * @param z z size
     */
    public BlockStructure(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0) throw new IllegalArgumentException("Size cannot be negative");
        this.structure = new BlockStructureEntry[x][y][z];
    }

    /**
     * Creates a new empty block structure with the size of the specified block box.
     * @param box The {@link BlockBox} the size should be used of.
     */
    public BlockStructure(BlockBox box) {
        box = box.clone();
        box.sort();

        int x = box.getMaxX() + 1 - box.getMinX();
        int y = box.getMaxY() + 1 - box.getMinY();
        int z = box.getMaxZ() + 1 - box.getMinZ();

        if (x < 0 || y < 0 || z < 0) throw new IllegalArgumentException("Size cannot be negative");

        this.structure = new BlockStructureEntry[x][y][z];
    }

    /**
     * Copies another block structure.
     * @param blockStructure the other block structure
     */
    public BlockStructure(BlockStructure blockStructure) {
        this.structure = blockStructure.getBlocks();
    }

    /**
     * Clones this block structure
     * @return copy of this block structure
     */
    @Override
    public BlockStructure clone() {
        return new BlockStructure(this);
    }

    /**
     * Returns the x length (width) of the structure.
     * @return x length (width)
     */
    public int getXLength() {
        return this.structure.length;
    }

    /**
     * Returns the y length (height) of the structure.
     * @return y length (height)
     */
    public int getYLength() {
        return this.structure.length >= 1 ? this.structure[0].length : 0;
    }

    /**
     * Returns the z length (length)
     * @return z length (length)
     */
    public int getZLength() {
        return this.structure.length >= 1 && this.structure[0].length >= 1 ? this.structure[0][0].length : 0;
    }

    /**
     * Returns a {@link BlockBox} with the same dimensions as this block structure.
     * The starting point of the block structure can be set with the parameters.
     * @param x x shift
     * @param y y shift
     * @param z z shift
     * @return {@link BlockBox} with the same dimensions as this block structure.
     */
    public BlockBox toBlockBox(int x, int y, int z) {
        int xLen = this.getXLength() - 1;
        int yLen = this.getYLength() - 1;
        int zLen = this.getZLength() - 1;

        if (xLen < 0) xLen = 0;
        if (yLen < 0) yLen = 0;
        if (zLen < 0) zLen = 0;

        return new BlockBox(x, y, z, x + xLen, y + yLen, z + zLen);
    }

    /**
     * Same as {@link this#toBlockBox(int, int, int)} with the difference that the vector is used for the shift.
     * @param vector shift vector / location vector of starting point
     * @return {@link BlockBox} with the same dimensions as this block structure.
     */
    public BlockBox toBlockBox(Vector vector) {
        return this.toBlockBox(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    /**
     * Creates a full copy of the current block structure's elements and returns it as a 3-dimensional array.
     * @return 3-dimensional array filled with block structure entries.
     */
    public BlockStructureEntry[][][] getBlocks() {
        BlockStructureEntry[][][] structureCopy = new BlockStructureEntry[this.structure.length][][];

        for (int x = 0; x < this.structure.length; x++) {
            BlockStructureEntry[][] yCopy = new BlockStructureEntry[this.structure[x].length][];
            structureCopy[x] = yCopy;

            for (int y = 0; y < this.structure[x].length; y++) {
                BlockStructureEntry[] zCopy = new BlockStructureEntry[this.structure[x][y].length];
                structureCopy[x][y] = zCopy;

                for (int z = 0; z < this.structure[x][y].length; z++) {
                    zCopy[z] = this.structure[x][y][z] != null ? this.structure[x][y][z].clone() : null;
                }

            }

        }

        return structureCopy;
    }

    /**
     * Get the block at the specified relative location of the block structure.
     * @param x x position
     * @param y y position
     * @param z z position
     * @return block structure entry (not null)
     */
    public BlockStructureEntry getBlock(int x, int y, int z) {
        if (x >= this.structure.length || y >= this.structure[x].length || z >= this.structure[x][y].length) return new BlockStructureEntry(Material.AIR, Material.AIR.createBlockData());
        return this.structure[x][y][z] != null ? this.structure[x][y][z] : new BlockStructureEntry(Material.AIR, Material.AIR.createBlockData());
    }

    /**
     * Get the block at the specified relative location of the block structure.
     * @param location location vector
     * @return block structure entry (not null)
     */
    public BlockStructureEntry getBlock(Vector location) {
        return this.getBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Set the block at the specified relative location of the block structure.
     * @param x x position
     * @param y y position
     * @param z z position
     * @param entry the entry or null to clear
     */
    public void setBlock(int x, int y, int z, BlockStructureEntry entry) {
        if (x >= this.structure.length || y >= this.structure[x].length || z >= this.structure[x][y].length) return;
        this.structure[x][y][z] = entry != null ? entry.clone() : null;
    }

    /**
     * Set the block at the specified relative location of the block structure.
     * @param vector location vector
     * @param entry the entry or null to clear
     */
    public void setBlock(Vector vector, BlockStructureEntry entry) {
        this.setBlock(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), entry);
    }

    /**
     * Paste this block structure to a specific location.
     * THE WORLD MUST NOT BE NULL FOR THIS TO WORK!
     * @param location location (with world != null)
     * @param xLevel limits this method to one specific x layer (negative value for all x layers)
     * @param yLevel limits this method to one specific y layer (negative value for all y layers)
     * @param zLevel limits this method to one specific z layer (negative value for all z layers)
     * @param ignoreTypes block types that should be ignored when pasting
     * @return List of modified blocks
     */
    public List<Block> paste(Location location, int xLevel, int yLevel, int zLevel, List<Material> ignoreTypes) {

        if (location.getWorld() == null) return List.of();
        List<Block> modifiedBlocks = new ArrayList<>();

        int relativeX = 0;
        for (int x = location.getBlockX(); x < (location.getBlockX() + this.structure.length); x++) {

            if (xLevel < 0 || xLevel == relativeX) {

                int relativeY = 0;
                for (int y = location.getBlockY(); y < (location.getBlockY() + this.structure[relativeX].length); y++) {

                    if (yLevel < 0 || yLevel == relativeY) {

                        int relativeZ = 0;
                        for (int z = location.getBlockZ(); z < (location.getBlockZ() + this.structure[relativeX][relativeY].length); z++) {

                            if (zLevel < 0 || zLevel == relativeZ) {

                                BlockStructureEntry entry = this.getBlock(relativeX, relativeY, relativeZ);
                                if (entry != null && !ignoreTypes.contains(entry.type())) {
                                    Block block = location.getWorld().getBlockAt(x, y, z);
                                    block.setType(entry.type());
                                    block.setBlockData(entry.data().clone());
                                    modifiedBlocks.add(block);
                                }

                            }

                            relativeZ++;
                        }

                    }

                    relativeY++;
                }

            }

            relativeX++;
        }

        return List.copyOf(modifiedBlocks);
    }

    /**
     * Paste this block structure to a specific location.
     * THE WORLD MUST NOT BE NULL FOR THIS TO WORK!
     * @param location location (with world != null)
     * @param xLevel limits this method to one specific x layer (negative value for all x layers)
     * @param yLevel limits this method to one specific y layer (negative value for all y layers)
     * @param zLevel limits this method to one specific z layer (negative value for all z layers)
     * @return List of modified blocks
     */
    public List<Block> paste(Location location, int xLevel, int yLevel, int zLevel) {
        return this.paste(location, xLevel, yLevel, zLevel, List.of());
    }

    /**
     * Paste this block structure to a specific location.
     * THE WORLD MUST NOT BE NULL FOR THIS TO WORK!
     * @param location location (with world != null)
     * @return List of modified blocks
     */
    public List<Block> paste(Location location) {
        return this.paste(location, -1, -1, -1);
    }

    /**
     * Checks if there is enough space to place this structure.
     * THE WORLD MUST NOT BE NULL FOR THIS TO WORK!
     * @param location location (with world != null)
     * @param xLevel limits this method to one specific x layer (negative value for all x layers)
     * @param yLevel limits this method to one specific y layer (negative value for all y layers)
     * @param zLevel limits this method to one specific z layer (negative value for all z layers)
     * @param ignoreStructureTypes blocks in the structure that should be ignored when checking for space (nothing is ignored when list null)
     * @param ignoreWorldTypes blocks in the world that should be ignored when checking for space (air is ignored when list is null)
     * @return true when space is free, false when space is not free
     */
    public boolean hasEnoughSpace(Location location, int xLevel, int yLevel, int zLevel, List<Material> ignoreStructureTypes, List<Material> ignoreWorldTypes) {

        if (location.getWorld() == null) return false;

        int relativeX = 0;
        for (int x = location.getBlockX(); x < (location.getBlockX() + this.structure.length); x++) {

            if (xLevel < 0 || xLevel == relativeX) {

                int relativeY = 0;
                for (int y = location.getBlockY(); y < (location.getBlockY() + this.structure[relativeX].length); y++) {

                    if (yLevel < 0 || yLevel == relativeY) {

                        int relativeZ = 0;
                        for (int z = location.getBlockZ(); z < (location.getBlockZ() + this.structure[relativeX][relativeY].length); z++) {

                            if (zLevel < 0 || zLevel == relativeZ) {

                                BlockStructureEntry entry = this.getBlock(relativeX, relativeY, relativeZ);
                                if (entry != null && (ignoreStructureTypes == null || !ignoreStructureTypes.contains(entry.type()))) {
                                    Block block = location.getWorld().getBlockAt(x, y, z);

                                    if ((ignoreWorldTypes == null && block.getType() != Material.AIR) || (ignoreWorldTypes != null && !ignoreWorldTypes.contains(block.getType()))) {
                                        return false;
                                    }

                                }

                            }

                            relativeZ++;
                        }

                    }

                    relativeY++;
                }

            }

            relativeX++;
        }

        return true;
    }

    /**
     * Checks if there is enough space to place this structure.
     * THE WORLD MUST NOT BE NULL FOR THIS TO WORK!
     * @param location location (with world != null)
     * @param xLevel limits this method to one specific x layer (negative value for all x layers)
     * @param yLevel limits this method to one specific y layer (negative value for all y layers)
     * @param zLevel limits this method to one specific z layer (negative value for all z layers)
     * @return true when space is free, false when space is not free
     */
    public boolean hasEnoughSpace(Location location, int xLevel, int yLevel, int zLevel) {
        return this.hasEnoughSpace(location, xLevel, yLevel, zLevel, null, null);
    }

    /**
     * Checks if there is enough space to place this structure.
     * THE WORLD MUST NOT BE NULL FOR THIS TO WORK!
     * @param location location (with world != null)
     * @param ignoreStructureTypes blocks in the structure that should be ignored when checking for space (nothing is ignored when list null)
     * @param ignoreWorldTypes blocks in the world that should be ignored when checking for space (air is ignored when list is null)
     * @return true when space is free, false when space is not free
     */
    public boolean hasEnoughSpace(Location location, List<Material> ignoreStructureTypes, List<Material> ignoreWorldTypes) {
        return this.hasEnoughSpace(location, -1, -1, -1, ignoreStructureTypes, ignoreWorldTypes);
    }

    /**
     * Checks if there is enough space to place this structure.
     * THE WORLD MUST NOT BE NULL FOR THIS TO WORK!
     * @param location location (with world != null)
     * @return true when space is free, false when space is not free
     */
    public boolean hasEnoughSpace(Location location) {
        return this.hasEnoughSpace(location, -1, -1, -1, null, null);
    }

    /**
     * Copy blocks into this block structure.
     * It copies all blocks from the specified location to the location + the structure size.
     * @param location starting point (end point is start point + size)
     */
    public void copy(Location location) {

        if (location.getWorld() == null) return;

        int relativeX = 0;
        for (int x = location.getBlockX(); x < (location.getBlockX() + this.structure.length); x++) {

            int relativeY = 0;
            for (int y = location.getBlockY(); y < (location.getBlockY() + this.structure[relativeX].length); y++) {

                int relativeZ = 0;
                for (int z = location.getBlockZ(); z < (location.getBlockZ() + this.structure[relativeX][relativeY].length); z++) {

                    Block block = location.getWorld().getBlockAt(x, y, z);
                    this.setBlock(relativeX, relativeY, relativeZ, new BlockStructureEntry(block.getType(), block.getBlockData()));

                    relativeZ++;
                }

                relativeY++;
            }

            relativeX++;
        }

    }

    /**
     * Creates a new block structure filled with the contents of the specified {@link BlockBox} in the specified world.
     * @param box the box the blocks should be copied from
     * @param world the world the blocks in the block box should be copied from
     */
    public BlockStructure(BlockBox box, World world) {
        this(box);
        box = box.clone();
        box.sort();
        this.copy(box.getMin().toLocation(world));
    }

    /**
     * Creates a new block structure filled with the contents from the specified location and the size of the specified vector from this location.
     * @param a Location (world != null)
     * @param b Size vector
     */
    public BlockStructure(Location a, Vector b) {
        this(a.getBlockX() + b.getBlockX(), a.getBlockY() + b.getBlockY(), a.getBlockZ() + b.getBlockZ());
        this.copy(a);
    }

    /**
     * Transposes the X x Z matrix (swaps all x values with all z values and all z values with all x values).
     */
    public void transposeXZ() {
        if (this.structure.length < 1 || this.structure[0].length < 1 || this.structure[0][0].length < 1) return;

        BlockStructureEntry[][][] transposed = new BlockStructureEntry[this.structure[0][0].length][this.structure[0].length][this.structure.length];

        for (int x = 0; x < this.structure.length; x++) {
            for (int y = 0; y < this.structure[x].length; y++) {
                for (int z = 0; z < this.structure[x][y].length; z++) {
                    transposed[z][y][x] = this.structure[x][y][z];
                }
            }
        }

        this.structure = transposed;
    }

    /**
     * Rotates the block structure 90 degrees right.
     */
    public void rotate90DegRight() {
        if (this.structure.length < 1 || this.structure[0].length < 1 || this.structure[0][0].length < 1) return;

        int xLen = this.structure.length;
        int zLen = this.structure[0][0].length;
        BlockStructureEntry[][][] rotatedMatrix = new BlockStructureEntry[zLen][this.structure[0].length][xLen];

        for (int x = 0; x < xLen; x++) {
            for (int y = 0; y < this.structure[0].length; y++) {
                for (int z = 0; z < zLen; z++) {
                    BlockStructureEntry entry = this.getBlock(x, y, z);

                    BlockData data = entry.data();
                    data.rotate(StructureRotation.COUNTERCLOCKWISE_90);

                    entry.setData(data);

                    rotatedMatrix[z][y][xLen - 1 - x] = entry;
                }
            }
        }

        this.structure = rotatedMatrix;
    }

    /**
     * Rotate the block structure 90 degrees left.
     */
    public void rotate90DegLeft() {
        if (this.structure.length < 1 || this.structure[0].length < 1 || this.structure[0][0].length < 1) return;

        int xLen = this.structure.length;
        int zLen = this.structure[0][0].length;
        BlockStructureEntry[][][] rotatedMatrix = new BlockStructureEntry[zLen][this.structure[0].length][xLen];

        for (int x = 0; x < xLen; x++) {
            for (int y = 0; y < this.structure[0].length; y++) {
                for (int z = 0; z < zLen; z++) {
                    BlockStructureEntry entry = this.getBlock(x, y, z);

                    BlockData data = entry.data();
                    data.rotate(StructureRotation.CLOCKWISE_90);

                    entry.setData(data);

                    rotatedMatrix[zLen - 1 - z][y][x] = entry;
                }
            }
        }

        this.structure = rotatedMatrix;
    }

    /**
     * Rotates the block structure 180 degrees.
     */
    public void rotate180Deg() {
        if (this.structure.length < 1 || this.structure[0].length < 1 || this.structure[0][0].length < 1) return;

        int rows = this.structure.length;
        int cols = this.structure[0][0].length;
        BlockStructureEntry[][][] rotatedMatrix = new BlockStructureEntry[rows][this.structure[0].length][cols];

        for (int i = 0; i < rows; i++) {
            for (int y = 0; y < this.structure[0].length; y++) {
                for (int j = 0; j < cols; j++) {
                    BlockStructureEntry entry = this.getBlock(i, y, j);

                    BlockData data = entry.data();
                    data.rotate(StructureRotation.CLOCKWISE_180);

                    entry.setData(data);

                    rotatedMatrix[rows - 1 - i][y][cols - 1 - j] = entry;
                }
            }
        }

        this.structure = rotatedMatrix;
    }

}
