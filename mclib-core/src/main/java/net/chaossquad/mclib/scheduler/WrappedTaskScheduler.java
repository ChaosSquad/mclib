package net.chaossquad.mclib.scheduler;

import net.chaossquad.mclib.misc.Removable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A scheduler that uses another scheduler to schedule its tasks with a specific remove condition and name.<br/>
 * This removes the need to manually add the same remove condition for many tasks in the same context.
 * <br/>
 * Practical example: A ManagedEntity has its own WrappedTaskScheduler which is linked to the game's {@link TaskScheduler}.
 * When the entity is dead, all tasks of it will be removed because of the remove condition of this task scheduler.
 */
public final class WrappedTaskScheduler implements SchedulerInterface, Removable {
    @NotNull private final SchedulerInterface scheduler;
    @NotNull private final Removable removeCondition;
    @NotNull private final String name;

    /**
     * Creates a new WrappedTaskScheduler.
     * @param scheduler real task scheduler
     * @param removeCondition remove condition
     * @param name name prefix
     */
    public WrappedTaskScheduler(@NotNull SchedulerInterface scheduler, @NotNull Removable removeCondition, @NotNull String name) {
        this.scheduler = scheduler;
        this.removeCondition = removeCondition;
        this.name = name;
    }

    @Override
    public long scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, @Nullable Removable removeCondition, @Nullable String label) {
        return this.scheduler.scheduleRepeatingTask(runnable, delay, interval, () -> this.toBeRemoved() || (removeCondition != null && removeCondition.toBeRemoved()), this.name + "_" + label);
    }

    @Override
    public long runTaskLater(@NotNull TaskRunnable runnable, long delay, @Nullable Removable removeCondition, @Nullable String label) {
        return this.scheduler.runTaskLater(runnable, delay, () -> this.toBeRemoved() || (removeCondition != null && removeCondition.toBeRemoved()), this.name + "_" + label);
    }

    @Override
    public boolean toBeRemoved() {
        return this.removeCondition.toBeRemoved();
    }

    /**
     * Returns the name.
     * @return name
     */
    public @NotNull String getName() {
        return this.name;
    }

}
