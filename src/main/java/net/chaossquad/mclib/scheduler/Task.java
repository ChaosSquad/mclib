package net.chaossquad.mclib.scheduler;

public abstract class Task {
    private final TaskScheduler scheduler;
    private final TaskRunnable runnable;
    private final String label;
    private boolean paused;

    protected Task(TaskScheduler scheduler, TaskRunnable runnable, String label) {
        this.scheduler = scheduler;
        this.runnable = runnable;
        this.label = label != null ? label.replace(",", "").replace(" ", "") : "unnamed";
    }

    // RUN

    public final void run(TaskScheduler scheduler, int taskId) {

        if (this.paused) {
            return;
        }

        this.onRun();
        this.runnable.run(scheduler, taskId);

    }

    // ABSTRACT

    /**
     * This method is called when the task scheduler runs the task.
     */
    protected abstract void onRun();

    /**
     * This method is called when the task scheduler checks if the task should be executed or not
     * @return true = should be executed, false = do not execute
     */
    public abstract boolean shouldRun();

    /**
     * This method is called when the task scheduler checks if the task should be removed.
     * @return true = task scheduler should remove the task, false = do not remove
     */
    public abstract boolean shouldBeRemoved();

    // GETTER / SETTER

    public final TaskScheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * Returns if the task is paused
     * @return true = task paused
     */
    public final boolean isPaused() {
        return this.paused;
    }

    /**
     * Sets the pause status of the task.
     * @param paused paused status
     */
    public final void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public String toString() {
        return "UNSPECIFIED";
    }

    public String getLabel() {
        return this.label;
    }

}
