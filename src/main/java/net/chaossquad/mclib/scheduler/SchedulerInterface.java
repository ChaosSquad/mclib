package net.chaossquad.mclib.scheduler;

import net.chaossquad.mclib.misc.Removable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SchedulerInterface {

    // ----- REPEATING TASKS -----

    // With TaskRunnable

    /**
     * Creates a new repeating task.
     * @param runnable runnable of the task
     * @param delay how long should be waited to run the task the first time
     * @param interval how long should be waited to run the task again after the last execution
     * @param removeCondition a stop condition that will be checked every tick
     * @param label task label
     * @return task id (positive if successfully added)
     */
    long scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, @Nullable Removable removeCondition, @Nullable String label);

    default long scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, @Nullable Removable removeCondition) {
        return this.scheduleRepeatingTask(runnable, delay, interval, removeCondition, null);
    }

    default long scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, @Nullable String label) {
        return this.scheduleRepeatingTask(runnable, delay, interval, null, label);
    }

    default long scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval) {
        return this.scheduleRepeatingTask(runnable, delay, interval, (Removable) null);
    }

    // With Runnable

    default long scheduleRepeatingTask(@NotNull Runnable runnable, long delay, long interval, @Nullable Removable removeCondition, String label) {
        return this.scheduleRepeatingTask(TaskRunnable.fromRunnable(runnable), delay, interval, removeCondition, label);
    }

    default long scheduleRepeatingTask(@NotNull Runnable runnable, long delay, long interval, @Nullable Removable removeCondition) {
        return this.scheduleRepeatingTask(runnable, delay, interval, removeCondition, null);
    }

    @SuppressWarnings("UnusedReturnValue")
    default long scheduleRepeatingTask(@NotNull Runnable runnable, long delay, long interval, @Nullable String label) {
        return this.scheduleRepeatingTask(runnable, delay, interval, null, label);
    }

    @SuppressWarnings("UnusedReturnValue")
    default long scheduleRepeatingTask(@NotNull Runnable runnable, long delay, long interval) {
        return this.scheduleRepeatingTask(runnable, delay, interval, (Removable) null);
    }

    // ----- ONE TIME TASKS -----

    // With TaskRunnable

    /**
     * Creates a new one-time task.
     * @param runnable runnable of the task
     * @param delay how long should be waited in ticks before running the task
     * @param removeCondition a stop condition that will be checked every tick
     * @param label task label
     * @return task id (positive if successfully added)
     */
    long runTaskLater(@NotNull TaskRunnable runnable, long delay, @Nullable Removable removeCondition, @Nullable String label);

    default long runTaskLater(@NotNull TaskRunnable runnable, long delay, @Nullable Removable removeCondition) {
        return this.runTaskLater(runnable, delay, removeCondition, null);
    }

    default long runTaskLater(@NotNull TaskRunnable runnable, long delay, @Nullable String label) {
        return this.runTaskLater(runnable, delay, null, label);
    }

    default long runTaskLater(@NotNull TaskRunnable runnable, long delay) {
        return this.runTaskLater(runnable, delay, null, null);
    }

    // With Runnable

    default long runTaskLater(@NotNull Runnable runnable, long delay, @Nullable Removable removeCondition, String label) {
        return this.runTaskLater(TaskRunnable.fromRunnable(runnable), delay, removeCondition, label);
    }

    default long runTaskLater(@NotNull Runnable runnable, long delay, @Nullable Removable removeCondition) {
        return this.runTaskLater(runnable, delay, removeCondition, null);
    }

    default long runTaskLater(@NotNull Runnable runnable, long delay, @Nullable String label) {
        return this.runTaskLater(runnable, delay, null, label);
    }

    default long runTaskLater(@NotNull Runnable runnable, long delay) {
        return this.runTaskLater(runnable, delay, null, null);
    }

}
