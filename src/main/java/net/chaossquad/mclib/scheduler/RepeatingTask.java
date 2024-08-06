package net.chaossquad.mclib.scheduler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Repeating task for the {@link TaskScheduler}.
 * See {@link TaskScheduler#scheduleRepeatingTask(TaskRunnable, long, long, RemoveCondition, String)}
 */
public class RepeatingTask extends Task {
    private final long interval;
    private long lastExecutionTick;

    protected RepeatingTask(long id, @NotNull TaskScheduler scheduler, @NotNull TaskRunnable runnable, @Nullable Task.RemoveCondition removeCondition, @Nullable String label, long initialDelay, long interval) {
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

    public long getInterval() {
        return this.interval;
    }

    public long getLastExecutionTick() {
        return this.lastExecutionTick;
    }

}
