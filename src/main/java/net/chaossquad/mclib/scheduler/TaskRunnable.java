package net.chaossquad.mclib.scheduler;

/**
 * A runnable for the {@link TaskScheduler}.
 */
public interface TaskRunnable {

    static TaskRunnable fromRunnable(Runnable runnable) {
        return (task) -> runnable.run();
    }

    void run(Task task);

}
