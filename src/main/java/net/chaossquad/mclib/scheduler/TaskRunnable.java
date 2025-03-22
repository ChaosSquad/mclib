package net.chaossquad.mclib.scheduler;

/**
 * A runnable for the {@link TaskScheduler}.
 */
public interface TaskRunnable {

    /**
     * Creates a TaskRunnable from a Java Runnable
     * @param runnable Runnable
     * @return TaskRunnable
     */
    static TaskRunnable fromRunnable(Runnable runnable) {
        return (task) -> runnable.run();
    }

    /**
     * Tke task scheduler will run this method.
     * @param task task
     */
    void run(Task task);

}
