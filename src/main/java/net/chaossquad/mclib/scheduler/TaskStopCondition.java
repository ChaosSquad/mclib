package net.chaossquad.mclib.scheduler;

/**
 * Stop condition for custom tasks.
 * When {@link this#shouldBeStopped()} returns true, the task will automatically be removed from the {@link TaskScheduler}.
 */
public interface TaskStopCondition {
    boolean shouldBeStopped();
}
