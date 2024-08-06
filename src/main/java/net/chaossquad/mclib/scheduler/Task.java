package net.chaossquad.mclib.scheduler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A task for the {@link TaskScheduler}.
 * Can only be created by the {@link TaskScheduler} itself.
 */
public abstract class Task {
    private final long id;
    private final TaskScheduler scheduler;
    private final TaskRunnable runnable;
    private final RemoveCondition removeCondition;
    private final String label;
    private boolean paused;

    protected Task(long id, @NotNull TaskScheduler scheduler, @NotNull TaskRunnable runnable, @Nullable Task.RemoveCondition removeCondition, @Nullable String label) {
        this.id = id;
        this.scheduler = scheduler;
        this.runnable = runnable;
        this.removeCondition = removeCondition != null ? removeCondition : () -> false;
        this.label = label != null ? label.replace(",", "").replace(" ", "") : "unnamed";
    }

    // RUN

    protected final void run() {

        if (this.paused) {
            return;
        }

        this.onRun();
        this.runnable.run(this);

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
    protected abstract boolean inheritedRemoveCondition();

    // OTHER

    /**
     * Returns the task id.
     * @return task id
     */
    public final long getId() {
        return this.id;
    }

    /**
     * Returns the task scheduler.
     * @return task scheduler
     */
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

    public final boolean toBeRemoved() {
        return this.removeCondition.toBeRemoved() || this.inheritedRemoveCondition();
    }

    public final String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return "UNSPECIFIED";
    }

    // INNER CLASSES

    /**
     * Stop condition for custom tasks.
     * When {@link this#toBeRemoved()} returns true, the task will automatically be removed from the {@link TaskScheduler}.
     */
    public interface RemoveCondition {
        boolean toBeRemoved();
    }

}
