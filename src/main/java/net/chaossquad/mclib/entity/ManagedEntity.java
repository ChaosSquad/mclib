package net.chaossquad.mclib.entity;

import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.ListenerRegistrar;
import net.chaossquad.mclib.misc.Removable;
import net.chaossquad.mclib.scheduler.SchedulerInterface;
import net.chaossquad.mclib.scheduler.TaskRunnable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class provides a manager for entities.<br/>
 * It allows to schedule tasks for entities and works as managed listener.<br/>
 * The goal of this class is to allow scheduling tasks and listen to events while the entity is alive, and then auto-cleanup tasks and listeners when the entity is dead.<br/>
 * <b>DO NOT REGISTER THIS ENTITY DIRECTLY TO THE BUKKIT EVENT LISTENER!!!<br/>
 * Only register it to a {@link net.chaossquad.mclib.executable.CoreExecutable} or a similar solution!<br/>
 * If you register it directly to Bukkit, it causes a memory leak because the listener is never cleaned up!</b>
 * @param <ENTITY_TYPE> entity type
 */
public abstract class ManagedEntity<ENTITY_TYPE extends Entity> implements ManagedListener, Removable, SchedulerInterface {
    @NotNull private Removable removable;
    @NotNull SchedulerInterface scheduler;
    @Nullable private ENTITY_TYPE entity;
    private boolean removed;

    public ManagedEntity(@NotNull SchedulerInterface scheduler, @NotNull ListenerRegistrar registrar) {
        this.removable = () -> false;
        this.scheduler = scheduler;
        this.entity = null;
        this.removed = false;

        // This task is scheduled directly by the specified task scheduler because it has a different remove condition. All other tasks are scheduled via the internal methods.
        this.scheduler.scheduleRepeatingTask(this::entityCleanupTask, 1, 200, () -> (this.entity == null || this.entity.isDead()) && this.toBeRemoved(), this + "entity_cleanup");

        registrar.registerListener(this);
    }

    // ----- TASKS -----

    private void entityCleanupTask() {
        if (this.toBeRemoved() && this.entity != null && !this.entity.isDead()) {
            this.entity.remove();
        }
    }

    // ----- ENTITY -----

    public final @Nullable ENTITY_TYPE getEntity() {
        return entity;
    }

    protected final void setEntity(@NotNull ENTITY_TYPE entity) {

        // Remove previously existing entity
        if (this.entity != null && !this.entity.isDead()) {
            this.entity.remove();
        }

        // Set new entity
        this.entity = entity;
        this.entity.setPersistent(false);
        this.entity.addScoreboardTag("managed_entity");
        this.entity.addScoreboardTag("managed_entity_" + System.identityHashCode(this));
    }

    /**
     * This method is for overriding in superclasses.<br/>
     * It is called when the entity
     */
    @ApiStatus.OverrideOnly
    protected void onEntitySet() {}

    // ----- SCHEDULER INTERFACE -----

    @Override
    public final long scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, Removable removeCondition, @Nullable String label) {
        return this.scheduler.scheduleRepeatingTask(
                runnable,
                delay,
                interval,
                () -> this.toBeRemoved() || (removeCondition != null && removeCondition.toBeRemoved()),
                this + "_" + label
        );
    }

    @Override
    public final long runTaskLater(@NotNull TaskRunnable runnable, long delay, Removable removeCondition, @Nullable String label) {
        return this.scheduler.runTaskLater(
                runnable,
                delay,
                () -> this.toBeRemoved() || (removeCondition != null && removeCondition.toBeRemoved()),
                this + "_" + label
        );
    }

    // ----- REMOVABLE -----

    @Override
    public final boolean toBeRemoved() {
        return this.removed || this.removable.toBeRemoved();
    }

    /**
     * Get the removable that is currently set.
     * @return removable
     */
    protected @NotNull Removable getRemovable() {
        return removable;
    }

    /**
     * Set or remove the removable.
     * @param removable removable
     */
    protected void setRemovable(@Nullable Removable removable) {

        if (removable == null) {
            this.removable = () -> false;
            return;
        }

        this.removable = removable;
    }

    // ----- OTHER -----

    @Override
    public String toString() {
        return "ManagedEntity_" + System.identityHashCode(this) + "_";
    }

    /**
     * Mark the managed entity as removed.<br/>
     * This will remove the entity and stop all its tasks.
     */
    public final void remove() {
        this.removed = true;
    }

    // ----- CREATOR -----

    /**
     * An interface to be used by subclasses to create the entities because a class cannot be retrieved from a type parameter.
     * @param <ENTITY_TYPE> entity type
     */
    public interface EntityCreator<ENTITY_TYPE extends Entity> {
        @NotNull ENTITY_TYPE create();
    }

}
