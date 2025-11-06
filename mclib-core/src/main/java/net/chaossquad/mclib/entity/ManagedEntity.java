package net.chaossquad.mclib.entity;

import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.ListenerRegistrar;
import net.chaossquad.mclib.misc.Removable;
import net.chaossquad.mclib.scheduler.SchedulerInterface;
import net.chaossquad.mclib.scheduler.TaskRunnable;
import net.chaossquad.mclib.scheduler.WrappedTaskScheduler;
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
    @NotNull private final WrappedTaskScheduler scheduler;
    @Nullable private ENTITY_TYPE entity;
    private boolean removed;

    /**
     * Creates a ManagedEntity.
     * @param scheduler Task scheduler
     * @param registrar listener registrar
     */
    public ManagedEntity(@NotNull SchedulerInterface scheduler, @NotNull ListenerRegistrar registrar) {
        this.removable = () -> false;
        this.scheduler = new WrappedTaskScheduler(scheduler, this, this.toString());
        this.entity = null;
        this.removed = false;

        // This task is scheduled directly by the real task scheduler because it has a different remove condition. All other tasks are scheduled via the internal methods.
        scheduler.scheduleRepeatingTask(this::entityCleanupTask, 1, 200, this::isCleanedUp, this + "_entity_cleanup");
        // This task is responsible for marking the entity as removed when the remove condition is true. It can be stopped when the entity is marked as removed
        scheduler.scheduleRepeatingTask(this::removeTask, 1, 100, this, this + "_set_removed");

        registrar.registerListener(this);
    }

    // ----- TASKS -----

    /**
     * Marks the entity for removal when the remove condition is met.
     */
    private void removeTask() {

        try {

            if (this.removable.toBeRemoved()) {
                this.remove();
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.remove();
        }

    }

    /**
     * Stays active until the ManagedEntity is set as removed and the ManagedEntity is dead.<br/>
     * Ensures that the Entity will be removed when the ManagedEntity is removed.
     */
    private void entityCleanupTask() {
        if (!this.removed) return;

        if (this.entity == null) return;
        if (this.entity.isDead()) return;

        this.entity.remove();
    }

    // ----- ENTITY -----

    /**
     * Returns the entity.
     * @return entity
     */
    public final @Nullable ENTITY_TYPE getEntity() {
        return entity;
    }

    /**
     * Sets the ManagedEntity.<br/>
     * This will kill the old entity.
     * @param entity entity
     */
    protected final void setEntity(@NotNull ENTITY_TYPE entity) {
        if (this.removed) throw new IllegalStateException("ManagedEntity is already removed");

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
     * Returns true if the entity exist (not null or dead).
     * @return entity exists
     */
    public final boolean doesEntityExist() {
        return this.entity != null && !this.entity.isDead();
    }

    /**
     * This method is for overriding in superclasses.<br/>
     * It is called when the entity
     */
    @SuppressWarnings("EmptyMethod")
    @ApiStatus.OverrideOnly
    protected void onEntitySet() {}

    // ----- SCHEDULER -----

    /**
     * Returns the task scheduler of this entity.
     * @return scheduler
     */
    public final WrappedTaskScheduler getTaskScheduler() {
        return this.scheduler;
    }

    /**
     * Schedules a new repeating task for this entity.
     * @param runnable runnable of the task
     * @param delay how long should be waited to run the task the first time
     * @param interval how long should be waited to run the task again after the last execution
     * @param removeCondition a stop condition that will be checked every tick
     * @param label task label
     * @return task id
     * @deprecated Use {@link #getTaskScheduler()} ({@link WrappedTaskScheduler#scheduleRepeatingTask(Runnable, long, long, Removable, String)}
     */
    @Override
    @Deprecated
    public final long scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, Removable removeCondition, @Nullable String label) {
        return this.scheduler.scheduleRepeatingTask(runnable, delay, interval, removeCondition, label);
    }

    /**
     * Schedules a new one time task for this entity.
     * @param runnable runnable of the task
     * @param delay how long should be waited in ticks before running the task
     * @param removeCondition a stop condition that will be checked every tick
     * @param label task label
     * @return task id
     * @deprecated Use {@link #getTaskScheduler()} ({@link WrappedTaskScheduler#runTaskLater(Runnable, long, Removable, String)}).
     */
    @Override
    @Deprecated
    public final long runTaskLater(@NotNull TaskRunnable runnable, long delay, Removable removeCondition, @Nullable String label) {
        return this.scheduler.runTaskLater(runnable, delay, removeCondition, label);
    }

    // ----- REMOVABLE -----

    @Override
    public final boolean toBeRemoved() {
        return this.removed;
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
        return "ManagedEntity_" + System.identityHashCode(this);
    }

    /**
     * Returns true if the entity is marked for removal.
     * @return marked for removal
     */
    public final boolean isRemoved() {
        return this.removed;
    }

    /**
     * Mark the managed entity as removed.<br/>
     * This will remove the entity and stop all its tasks.
     */
    public final void remove() {
        this.removed = true;
        this.entityCleanupTask();
    }

    /**
     * Returns true if the ManagedEntity has reached the end of its lifecycle and is fully cleaned up.<br/>
     * This means that its entity is dead, and it is marked for removal.
     * @return cleaned up
     */
    public final boolean isCleanedUp() {
        return this.removed && (this.entity == null || this.entity.isDead());
    }

    // ----- CREATOR -----

    /**
     * An interface to be used by subclasses to create the entities because a class cannot be retrieved from a type parameter.
     * @param <ENTITY_TYPE> entity type
     * @param <MANAGED_ENTITY_TYPE> the type of the managed entity
     */
    public interface EntityCreator<ENTITY_TYPE extends Entity, MANAGED_ENTITY_TYPE extends ManagedEntity<ENTITY_TYPE>> {

        /**
         * Returns the created entity.
         * @param managedEntity the ManagedEntity that creates the entity
         * @return created entity
         */
        @NotNull ENTITY_TYPE create(@NotNull MANAGED_ENTITY_TYPE managedEntity);

    }

}
