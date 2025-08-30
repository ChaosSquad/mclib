package net.chaossquad.mclib.scheduler;

import net.chaossquad.mclib.misc.Removable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A custom task scheduler.
 * The {@link TaskScheduler#tick()} method needs to be called in a bukkit task every tick.
 * The purpose of this class is to provide a custom task scheduler so that you don't need to remove tasks manually from the bukkit scheduler.
 * You can for example integrate this scheduler inside a gamemode class, and as soon as the gamemode has ended, all tasks are gone because the {@link TaskScheduler#tick()} method if this scheduler is no longer called.
 */
public class TaskScheduler implements SchedulerInterface {
    private final Logger logger;
    private final Map<Long, Task> tasks;
    private long nextTaskId;
    private long tick;

    /**
     * Creates a task scheduler.
     * @param logger logger
     */
    public TaskScheduler(Logger logger) {
        this.logger = logger;
        this.tasks = Collections.synchronizedMap(new HashMap<>());
        this.nextTaskId = 1;
        this.tick = 0;
    }

    // TICK

    /**
     * This method runs the scheduler.<br/>
     * Call it using a repeating bukkit task or something similar.
     * @return success
     */
    @SuppressWarnings("UnusedReturnValue")
    public final boolean tick() {
        boolean hadException = false;

        synchronized (this.tasks) {

            for (Map.Entry<Long, Task> entry : Map.copyOf(this.tasks).entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) continue;
                this.handleTask(entry.getKey(), entry.getValue());
            }

        }

        this.tick++;

        return hadException;
    }

    /**
     * Handles one specific task.
     * @param taskId task id
     * @param task task
     */
    private void handleTask(long taskId, @NotNull Task task) {

        // Conditions
        boolean valid = taskId == task.getId() && task.getScheduler() == this;
        boolean toBeRemoved = task.toBeRemoved();
        boolean shouldRun = task.shouldRun();
        boolean paused = task.isPaused();

        // Execution
        if (valid && !toBeRemoved && !paused && shouldRun) {

            try {
                task.run();
            } catch (Exception e) {
                this.logger.log(Level.WARNING, "Exception in scheduler task (id=" + taskId + ")", e);
            }

        }

        // Removal
        if (!valid || toBeRemoved) {
            this.tasks.remove(taskId);
            return;
        }

    }

    // TASK ID MANAGEMENT

    private long getFreeTaskId() {
        long taskId = this.nextTaskId;
        this.nextTaskId++;
        return taskId;
    }

    // GET TASKS

    /**
     * Returns a copy of the map where the tasks are stored.
     * @return map of tasks
     */
    public final Map<Long, Task> getTasks() {
        return Map.copyOf(this.tasks);
    }

    /**
     * Get the amount of all tasks.
     * @return tasks count
     */
    public final int getTaskCount() {
        return this.tasks.size();
    }

    /**
     * Returns the task with the specified id.
     * @param taskId task id
     * @return task
     */
    public final Task getTask(long taskId) {
        return this.tasks.get(taskId);
    }

    // REMOVE TASKS

    /**
     * Clears all tasks.
     */
    public final void clearTasks() {
        this.tasks.clear();
    }

    /**
     * Removes a task of the task scheduler.
     * @param id task id
     */
    public final void removeTask(int id) {
        this.tasks.remove(id);
    }

    // ADD TASKS

    /**
     * Schedules a repeating task.
     * @param runnable the runnable that should be executed
     * @param delay the initial delay (the amount of ticks to wait until the task will run the first time)
     * @param interval the interval (the amount of ticks to wait before the task runs again after the last execution)
     * @param removeCondition the condition the task should be stopped and removed
     * @return task id
     */
    public final long scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, @Nullable Removable removeCondition, @Nullable String label) {
        long taskId = this.getFreeTaskId();
        this.tasks.put(taskId, new RepeatingTask(taskId, this, runnable, removeCondition, label, delay, interval));
        return taskId;
    }

    /**
     * Schedules a one-time delayed task.
     * @param runnable the runnable that should be executed
     * @param delay the delay (the amount of ticks to wait until the task will run)
     * @param removeCondition the condition the task should be stopped and removed
     * @return task id
     */
    public final long runTaskLater(@NotNull TaskRunnable runnable, long delay, @Nullable Removable removeCondition, @Nullable String label) {
        long taskId = this.getFreeTaskId();
        this.tasks.put(taskId, new OneTimeTask(taskId, this, runnable, removeCondition, label, delay));
        return taskId;
    }

    // OTHER

    /**
     * Returns the current scheduler tick.
     * @return tick
     */
    public final long getTick() {
        return this.tick;
    }

}
