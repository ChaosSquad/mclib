package net.chaossquad.mclib.immutables;

import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

/**
 * An immutable version of {@link BoundingBox}.
 * @deprecated Causes issues. Use a normal BoundingBox and isolate it if you want to have immutability.
 */
@Deprecated
public class ImmutableBoundingBox extends BoundingBox {

    /**
     * Creates a new ImmutableBoundingBox.
     * @param x1 min x
     * @param y1 min y
     * @param z1 min z
     * @param x2 max x
     * @param y2 max y
     * @param z2 max z
     */
    public ImmutableBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        super.resize(x1, y1, z1, x2, y2, z2);
    }

    /**
     * Creates a new ImmutableBoundingBox.
     * @param boundingBox object to copy
     */
    public ImmutableBoundingBox(@NotNull BoundingBox boundingBox) {
        super.resize(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());
    }

    @Override
    @NotNull
    public BoundingBox resize(double x1, double y1, double z1, double x2, double y2, double z2) {
        throw new UnsupportedOperationException("BoundingBox is immutable");
    }

    @Override
    public String toString() {
        return "Immutable" + super.toString();
    }

    @NotNull
    public ImmutableBoundingBox clone() {
        return (ImmutableBoundingBox) super.clone();
    }

    /**
     * Returns a mutable copy if this BoundingBox.
     * @return mutable copy
     */
    @NotNull
    public BoundingBox mutableCopy() {
        return new BoundingBox(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }

}
