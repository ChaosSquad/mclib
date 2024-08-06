package net.chaossquad.mclib.scheduler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SchedulerInterface {

    int scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, @Nullable TaskStopCondition stopCondition, @Nullable String label);

    default int scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, @Nullable TaskStopCondition stopCondition) {
        return this.scheduleRepeatingTask(runnable, delay, interval, stopCondition, null);
    }

    default int scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, @Nullable String label) {
        return this.scheduleRepeatingTask(runnable, delay, interval, null, label);
    }

    default int scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval) {
        return this.scheduleRepeatingTask(runnable, delay, interval, (TaskStopCondition) null);
    }

    default int scheduleRepeatingTask(@NotNull Runnable runnable, long delay, long interval, @Nullable TaskStopCondition stopCondition, String label) {
        return this.scheduleRepeatingTask(TaskRunnable.fromRunnable(runnable), delay, interval, stopCondition, label);
    }

    default int scheduleRepeatingTask(@NotNull Runnable runnable, long delay, long interval, @Nullable TaskStopCondition stopCondition) {
        return this.scheduleRepeatingTask(runnable, delay, interval, stopCondition, null);
    }

    default int scheduleRepeatingTask(@NotNull Runnable runnable, long delay, long interval, @Nullable String label) {
        return this.scheduleRepeatingTask(runnable, delay, interval, null, label);
    }

    default int scheduleRepeatingTask(@NotNull Runnable runnable, long delay, long interval) {
        return this.scheduleRepeatingTask(runnable, delay, interval, (TaskStopCondition) null);
    }

}
