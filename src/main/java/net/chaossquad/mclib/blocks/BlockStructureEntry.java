package net.chaossquad.mclib.blocks;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * Represents a block in a block structure.
 * It saves a copy of its type and of its data.
 * The block data will be copied on setting and getting.
 * @param type block type (material)
 * @param data block data
 */
public record BlockStructureEntry(Material type, BlockData data) {

    public BlockStructureEntry(Material type, BlockData data) {
        this.type = type;
        this.data = data.clone();
    }

    @Override
    public BlockData data() {
        return this.data.clone();
    }

    @Override
    public BlockStructureEntry clone() {
        return new BlockStructureEntry(this.type, this.data.clone());
    }

}
