package net.chaossquad.mclib.scheduler;

import net.chaossquad.mclib.misc.Removable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Repeating task for the {@link TaskScheduler}.
 * See {@link TaskScheduler#scheduleRepeatingTask(TaskRunnable, long, long, net.chaossquad.mclib.misc.Removable, String)}
 */
public class RepeatingTask extends Task {
    private final long interval;
    private long lastExecutionTick;

    /**
     * Creates a new RepeatingTask
     * @param id id
     * @param scheduler scheduler
     * @param runnable runnable
     * @param removeCondition remove condition
     * @param label label
     * @param initialDelay initial delay
     * @param interval interval
     */
    @ApiStatus.Internal
    protected RepeatingTask(long id, @NotNull TaskScheduler scheduler, @NotNull TaskRunnable runnable, @Nullable Removable removeCondition, @Nullable String label, long initialDelay, long interval) {
        super(id, scheduler, runnable, removeCondition, label);

        this.interval = interval;
        this.lastExecutionTick = this.getScheduler().getTick() + initialDelay;
    }

    @Override
    protected void onRun() {
        this.lastExecutionTick = this.getScheduler().getTick();
    }

    @Override
    public boolean shouldRun() {

        // Check if paused

        if (this.isPaused()) {
            return false;
        }

        // Run if conditions are met

        return this.getScheduler().getTick() - this.lastExecutionTick >= this.interval;
    }

    @Override
    public boolean inheritedRemoveCondition() {
        return false;
    }

    @Override
    public String toString() {
        return "REPEATING";
    }

    /**
     * Returns the interval in ticks.
     * @return interval
     */
    public long getInterval() {
        return this.interval;
    }


    /**
     * Returns the last execution time.
     * @return last execution time
     */
    public long getLastExecutionTick() {
        return this.lastExecutionTick;
    }

}
