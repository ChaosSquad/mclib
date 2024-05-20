package net.chaossquad.mclib.scheduler;

/**
 * A runnable for the {@link TaskScheduler}.
 */
public interface TaskRunnable {

    static TaskRunnable fromRunnable(Runnable runnable) {
        return (scheduler, taskId) -> runnable.run();
    }

    void run(TaskScheduler scheduler, int taskId);

}
