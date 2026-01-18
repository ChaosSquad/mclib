package net.chaossquad.mclib.scheduler;

import net.chaossquad.mclib.misc.Removable;
import org.jetbrains.annotations.ApiStatus;
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
    private final Removable removeCondition;
    private final String label;
    private boolean removed;
    private boolean paused;
    private long lastExecutionDuration;

    /**
     * Creates a task.
     * @param id id
     * @param scheduler task scheduler
     * @param runnable runnable
     * @param removeCondition remove condition
     * @param label label
     */
    @ApiStatus.Internal
    protected Task(long id, @NotNull TaskScheduler scheduler, @NotNull TaskRunnable runnable, @Nullable Removable removeCondition, @Nullable String label) {
        this.id = id;
        this.scheduler = scheduler;
        this.runnable = runnable;
        this.removeCondition = removeCondition != null ? removeCondition : () -> false;
        this.label = label != null ? label.replace(",", "").replace(" ", "") : "unnamed";
        this.removed = false;
        this.paused = false;
        this.lastExecutionDuration = 0L;
    }

    // RUN

    /**
     * This will run the task.
     * This does not check any conditions, so only use it if you know what you are doing!
     */
    @ApiStatus.Internal
    public final void run() {

        this.onRun();

        long startTime = System.nanoTime();
        this.runnable.run(this);
        this.lastExecutionDuration = System.nanoTime() - startTime;

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
     * Returns if the task has been marked for removal.
     * If the task is marked for removal, it will no longer execute.
     * This method does not return if the task has been or should be removed.
     * If you want to check if the task has been or should be removed, use {@link #toBeRemoved()}.
     * @return marked for removal
     */
    public boolean isMarkedForRemoval() {
        return this.removed;
    }

    /**
     * Mark the task for removal.
     */
    public void remove() {
        this.removed = true;
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

    /**
     * Returns the duration of the last task execution in nanoseconds.
     * @return duration in nanoseconds
     */
    public final long getLastExecutionDuration() {
        return this.lastExecutionDuration;
    }

    /**
     * Returns if the task should be removed.
     * This is the case if it is marked for removal, the remove condition is true or the inherited remove condition is true.
     * @return if the task should be removed
     */
    public final boolean toBeRemoved() {
        return this.removed || this.removeCondition.toBeRemoved() || this.inheritedRemoveCondition();
    }

    /**
     * Returns the label which is like a task name.
     * @return label
     */
    public final String getLabel() {
        return this.label;
    }

    /**
     * Returns the runnable object.<br/>
     * <b>NEVER CALL THIS MANUALLY! ALWAYS USE {@link #run()}</b>
     * @return task runnable
     */
    public final @NotNull TaskRunnable getRunnable() {
        return this.runnable;
    }

    /**
     * Returns the remove condition object.
     * @return remove condition
     */
    public final Removable getRemoveCondition() {
        return this.removeCondition;
    }

    @Override
    public String toString() {
        return "UNSPECIFIED";
    }

    // INNER CLASSES

    /**
     * Stop condition for custom tasks.
     * When {@link RemoveCondition#toBeRemoved()} returns true, the task will automatically be removed from the {@link TaskScheduler}.
     * @deprecated Use {@link Removable} directly
     */
    @Deprecated
    public interface RemoveCondition extends Removable {
        boolean toBeRemoved();
    }

}
