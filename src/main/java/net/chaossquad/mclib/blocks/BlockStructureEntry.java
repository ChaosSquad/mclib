package net.chaossquad.mclib.blocks;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * Represents a block in a block structure.
 * It saves a copy of its type and of its data.
 * The block data will be copied on setting and getting.
 */
public class BlockStructureEntry {
    private final Material type;
    private final BlockData data;

    public BlockStructureEntry(Material type, BlockData data) {
        this.type = type;
        this.data = data.clone();
    }

    private BlockStructureEntry(BlockStructureEntry entry) {
        this.type = entry.type;
        this.data = entry.data.clone();
    }

    /**
     * Returns the material (type).
     * @return type
     */
    public final Material type() {
        return this.type;
    }

    /**
     * Returns a copy of the block data.
     * @return copy of block data
     */
    public final BlockData data() {
        return this.data.clone();
    }

    public BlockStructureEntry clone() {
        return new BlockStructureEntry(this);
    }

}
