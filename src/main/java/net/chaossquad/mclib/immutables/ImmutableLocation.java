package net.chaossquad.mclib.immutables;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An immutable version of {@link Location}.
 */
public class ImmutableLocation extends Location {

    /**
     * Creates a new ImmutableLocation.
     * @param world world
     * @param x x
     * @param y y
     * @param z z
     */
    public ImmutableLocation(@Nullable World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    /**
     * Creates a new ImmutableLocation.
     * @param world world
     * @param x x
     * @param y y
     * @param z z
     * @param yaw yaw
     * @param pitch pitch
     */
    public ImmutableLocation(@org.jetbrains.annotations.Nullable World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    /**
     * Creates a new immutable location based on a mutable location.
     * @param location Bukkit Location
     */
    public ImmutableLocation(@NotNull Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void setWorld(@Nullable World world) {
        throw new UnsupportedOperationException("Location is immutable");
    }

    @Override
    public void setX(double x) {
        throw new UnsupportedOperationException("Location is immutable");
    }

    @Override
    public void setY(double y) {
        throw new UnsupportedOperationException("Location is immutable");
    }

    @Override
    public void setZ(double z) {
        throw new UnsupportedOperationException("Location is immutable");
    }

    @Override
    public void setYaw(float yaw) {
        throw new UnsupportedOperationException("Location is immutable");
    }

    @Override
    public void setPitch(float pitch) {
        throw new UnsupportedOperationException("Location is immutable");
    }

    @Override
    @NotNull
    public ImmutableLocation setDirection(@NotNull Vector vector) {
        Location location = this.mutableCopy();
        location.setDirection(vector);
        return new ImmutableLocation(location);
    }

    @Override
    @NotNull
    public ImmutableLocation add(@NotNull Location vec) {
        Location location = this.mutableCopy();
        location.add(vec);
        return new ImmutableLocation(location);
    }

    @Override
    @NotNull
    public ImmutableLocation add(@NotNull Vector vec) {
        Location location = this.mutableCopy();
        location.add(vec);
        return new ImmutableLocation(location);
    }

    @Override
    @NotNull
    public ImmutableLocation add(double x, double y, double z) {
        Location location = this.mutableCopy();
        location.add(x, y, z);
        return new ImmutableLocation(location);
    }

    @Override
    @NotNull
    public ImmutableLocation subtract(@NotNull Location vec) {
        Location location = this.mutableCopy();
        location.subtract(vec);
        return new ImmutableLocation(location);
    }

    @Override
    @NotNull
    public ImmutableLocation subtract(@NotNull Vector vec) {
        Location location = this.mutableCopy();
        location.subtract(vec);
        return new ImmutableLocation(location);
    }

    @Override
    @NotNull
    public ImmutableLocation subtract(double x, double y, double z) {
        return new ImmutableLocation(this.mutableCopy().subtract(x, y, z));
    }

    @Override
    @NotNull
    public ImmutableLocation multiply(double m) {
        return new ImmutableLocation(this.mutableCopy().multiply(m));
    }

    @Override
    @NotNull
    public Location zero() {
        return new ImmutableLocation(this.mutableCopy().zero());
    }

    @Override
    @NotNull
    public ImmutableVector toVector() {
        return new ImmutableVector(this.getX(), this.getY(), this.getZ());
    }

    @Override
    @NotNull
    public ImmutableLocation clone() {
        return new ImmutableLocation(super.clone());
    }

    /**
     * Returns a mutable copy of this location.
     * @return location
     */
    @NotNull
    public Location mutableCopy() {
        return new Location(this.getWorld(), this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
    }

}
