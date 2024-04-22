package net.chaossquad.mclib.scheduler;

public class RepeatingTask extends Task {
    private final long interval;
    private final TaskStopCondition stopCondition;
    private long lastExecutionTick;

    protected RepeatingTask(TaskScheduler scheduler, TaskRunnable runnable, String label, long initialDelay, long interval, TaskStopCondition stopCondition) {
        super(scheduler, runnable, label);

        this.interval = interval;
        this.stopCondition = stopCondition != null ? stopCondition : () -> false;

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
    public boolean shouldBeRemoved() {
        return this.stopCondition.shouldBeStopped();
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

    public TaskStopCondition getStopCondition() {
        return this.stopCondition;
    }

}
