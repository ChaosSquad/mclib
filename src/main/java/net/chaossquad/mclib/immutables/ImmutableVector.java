package net.chaossquad.mclib.immutables;

import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * An immutable version of {@link Vector}
 */
public class ImmutableVector extends Vector {

    public ImmutableVector() {
        super();
    }

    public ImmutableVector(int x, int y, int z) {
        super(x, y, z);
    }

    public ImmutableVector(double x, double y, double z) {
        super(x, y, z);
    }

    public ImmutableVector(float x, float y, float z) {
        super(x, y, z);
    }

    /**
     * Creates a new immutable vector based on a mutable vector.
     * @param vector Bukkit Vector
     */
    public ImmutableVector(Vector vector) {
        super(vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    @NotNull
    public ImmutableVector add(@NotNull Vector vec) {
        return new ImmutableVector(this.mutableCopy().add(vec));
    }

    @Override
    @NotNull
    public ImmutableVector subtract(@NotNull Vector vec) {
        return new ImmutableVector(this.mutableCopy().subtract(vec));
    }

    @Override
    @NotNull
    public ImmutableVector multiply(@NotNull Vector vec) {
        return new ImmutableVector(this.mutableCopy().multiply(vec));
    }

    @Override
    @NotNull
    public ImmutableVector divide(@NotNull Vector vec) {
        return new ImmutableVector(this.mutableCopy().divide(vec));
    }

    @Override
    @NotNull
    public ImmutableVector copy(@NotNull Vector vec) {
        throw new UnsupportedOperationException("Vector is immutable");
    }

    @Override
    @NotNull
    public ImmutableVector midpoint(@NotNull Vector other) {
        return new ImmutableVector(this.mutableCopy().midpoint(other));
    }

    @Override
    @NotNull
    public ImmutableVector multiply(int m) {
        return new ImmutableVector(this.mutableCopy().multiply(m));
    }

    @Override
    @NotNull
    public ImmutableVector multiply(double m) {
        return new ImmutableVector(this.mutableCopy().multiply(m));
    }

    @Override
    @NotNull
    public ImmutableVector multiply(float m) {
        return new ImmutableVector(this.mutableCopy().multiply(m));
    }

    @Override
    @NotNull
    public ImmutableVector crossProduct(@NotNull Vector o) {
        return new ImmutableVector(this.mutableCopy().crossProduct(o));
    }

    @Override
    @NotNull
    public ImmutableVector normalize() {
        return new ImmutableVector(this.mutableCopy().normalize());
    }

    @Override
    @NotNull
    public ImmutableVector zero() {
        return new ImmutableVector(this.mutableCopy().zero());
    }

    @Override
    @NotNull
    public ImmutableVector rotateAroundX(double angle) {
        return new ImmutableVector(this.mutableCopy().rotateAroundX(angle));
    }

    @Override
    @NotNull
    public ImmutableVector rotateAroundY(double angle) {
        return new ImmutableVector(this.mutableCopy().rotateAroundY(angle));
    }

    @Override
    @NotNull
    public ImmutableVector rotateAroundZ(double angle) {
        return new ImmutableVector(this.mutableCopy().rotateAroundZ(angle));
    }

    @Override
    @NotNull
    public ImmutableVector rotateAroundAxis(@NotNull Vector axis, double angle) throws IllegalArgumentException {
        return new ImmutableVector(this.mutableCopy().rotateAroundAxis(axis, angle));
    }

    @Override
    @NotNull
    public ImmutableVector rotateAroundNonUnitAxis(@NotNull Vector axis, double angle) throws IllegalArgumentException {
        return new ImmutableVector(this.mutableCopy().rotateAroundNonUnitAxis(axis, angle));
    }

    @Override
    @NotNull
    public ImmutableVector setX(int x) {
        return new ImmutableVector(this.mutableCopy().setX(x));
    }

    @Override
    @NotNull
    public ImmutableVector setX(double x) {
        return new ImmutableVector(this.mutableCopy().setX(x));
    }

    @Override
    @NotNull
    public ImmutableVector setX(float x) {
        return new ImmutableVector(this.mutableCopy().setX(x));
    }

    @Override
    @NotNull
    public ImmutableVector setY(int y) {
        return new ImmutableVector(this.mutableCopy().setY(y));
    }

    @Override
    @NotNull
    public ImmutableVector setY(double y) {
        return new ImmutableVector(this.mutableCopy().setY(y));
    }

    @Override
    @NotNull
    public ImmutableVector setY(float y) {
        return new ImmutableVector(this.mutableCopy().setY(y));
    }

    @Override
    @NotNull
    public ImmutableVector setZ(int z) {
        return new ImmutableVector(this.mutableCopy().setZ(z));
    }

    @Override
    @NotNull
    public ImmutableVector setZ(double z) {
        return new ImmutableVector(this.mutableCopy().setZ(z));
    }

    @Override
    @NotNull
    public ImmutableVector setZ(float z) {
        return new ImmutableVector(this.mutableCopy().setZ(z));
    }

    @Override
    @NotNull
    public ImmutableVector clone() {
        return new ImmutableVector(this.mutableCopy().clone());
    }

    @Override
    @NotNull
    public ImmutableLocation toLocation(@NotNull World world) {
        return new ImmutableLocation(world, this.x, this.y, this.z);
    }

    @Override
    @NotNull
    public ImmutableLocation toLocation(@NotNull World world, float yaw, float pitch) {
        return new ImmutableLocation(world, this.x, this.y, this.z, yaw, pitch);
    }

    /**
     * Returns a mutable copy of this vector.
     * @return vector
     */
    @NotNull
    public Vector mutableCopy() {
        return new ImmutableVector(this.getX(), this.getY(), this.getZ());
    }

}
