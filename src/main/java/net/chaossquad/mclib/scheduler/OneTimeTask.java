package net.chaossquad.mclib.scheduler;

/**
 * A one-time task for {@link TaskScheduler}.
 * See {@link TaskScheduler#runTaskLater(TaskRunnable, long, String)}.
 */
public class OneTimeTask extends Task {
    private final long delay;
    private final long addedTick;
    private boolean executed;

    protected OneTimeTask(TaskScheduler scheduler, TaskRunnable runnable, String label, long delay) {
        super(scheduler, runnable, label);
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
    public boolean shouldBeRemoved() {
        return this.executed;
    }

    @Override
    public String toString() {
        return "ONE_TIME";
    }

    public boolean isExecuted() {
        return this.executed;
    }

    public long getAddedTick() {
        return this.addedTick;
    }

    public long getDelay() {
        return this.delay;
    }

}
