package net.chaossquad.mclib.immutables;

import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

/**
 * An immutable version of {@link BoundingBox}
 */
public class ImmutableBoundingBox extends BoundingBox {

    public ImmutableBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        super.resize(x1, y1, z1, x2, y2, z2);
    }

    public ImmutableBoundingBox(BoundingBox boundingBox) {
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

    @NotNull
    public BoundingBox mutableCopy() {
        return new BoundingBox(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }

}
