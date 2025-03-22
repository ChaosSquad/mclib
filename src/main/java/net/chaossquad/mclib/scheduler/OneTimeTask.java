package net.chaossquad.mclib.scheduler;

import net.chaossquad.mclib.misc.Removable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A one-time task for {@link TaskScheduler}.
 * See {@link TaskScheduler#runTaskLater(TaskRunnable, long, String)}.
 */
public class OneTimeTask extends Task {
    private final long delay;
    private final long addedTick;
    private boolean executed;

    /**
     * Creates a OneTimeTask.
     * @param id id
     * @param scheduler scheduler
     * @param runnable runnable
     * @param removeCondition removable condition
     * @param label label
     * @param delay delay
     */
    @ApiStatus.Internal
    protected OneTimeTask(long id, @NotNull TaskScheduler scheduler, @NotNull TaskRunnable runnable, @Nullable Removable removeCondition, @Nullable String label, long delay) {
        super(id, scheduler, runnable, removeCondition, label);
        this.delay = delay;
        this.addedTick = this.getScheduler().getTick();
        this.executed = false;
    }

    @Override
    protected void onRun() {
        this.executed = true;
    }

    @Override
    public boolean shouldRun() {

        // Check if paused

        if (this.isPaused()) {
            return false;
        }

        // If already executed

        if (this.executed) {
            return false;
        }

        // Run if conditions are met

        return this.getScheduler().getTick() - this.addedTick >= this.delay;
    }

    @Override
    public boolean inheritedRemoveCondition() {
        return this.executed;
    }

    @Override
    public String toString() {
        return "ONE_TIME";
    }

    /**
     * Returns true if the task has been executed.
     * @return executed
     */
    public boolean isExecuted() {
        return this.executed;
    }

    /**
     * Get the tick where the task has been added to the scheduler
     * @return added tick
     */
    public long getAddedTick() {
        return this.addedTick;
    }

    /**
     * Get the delay in ticks after which the task will be executed.
     * @return delay
     */
    public long getDelay() {
        return this.delay;
    }

}
