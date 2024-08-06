package net.chaossquad.mclib.scheduler;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * A custom task scheduler.
 * The {@link this#tick()} method needs to be called in a bukkit task every tick.
 * The purpose of this class is to provide a custom task scheduler so that you don't need to remove tasks manually from the bukkit scheduler.
 * You can for example integrate this scheduler inside a gamemode class, and as soon as the gamemode has ended, all tasks are gone because the {@link this#tick()} method if this scheduler is no longer called.
 */
public final class TaskScheduler implements SchedulerInterface {
    private final Plugin plugin;
    private final Map<Long, Task> tasks;
    private long nextTaskId;
    private long tick;

    public TaskScheduler(Plugin plugin) {
        this.plugin = plugin;
        this.tasks = Collections.synchronizedMap(new HashMap<>());
        this.nextTaskId = 1;
        this.tick = 0;
    }

    // TICK

    public boolean tick() {
        boolean hadException = false;

        synchronized (this.tasks) {

            for (Map.Entry<Long, Task> entry : this.tasks.entrySet()) {
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
                this.plugin.getLogger().log(Level.WARNING, "Exception in scheduler task (id=" + taskId + ")", e);
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
    public Map<Long, Task> getTasks() {
        return Map.copyOf(this.tasks);
    }

    /**
     * Get the amount of all tasks.
     * @return tasks count
     */
    public int getTaskCount() {
        return this.tasks.size();
    }

    public Task getTask(int taskId) {
        return this.tasks.get(taskId);
    }

    // REMOVE TASKS

    /**
     * Clears all tasks.
     */
    public void clearTasks() {
        this.tasks.clear();
    }

    /**
     * Removes a task of the task scheduler.
     * @param id task id
     */
    public void removeTask(int id) {
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
    public long scheduleRepeatingTask(@NotNull TaskRunnable runnable, long delay, long interval, @Nullable Task.RemoveCondition removeCondition, @Nullable String label) {
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
    public long runTaskLater(@NotNull TaskRunnable runnable, long delay, @Nullable Task.RemoveCondition removeCondition, @Nullable String label) {
        long taskId = this.getFreeTaskId();
        this.tasks.put(taskId, new OneTimeTask(taskId, this, runnable, removeCondition, label, delay));
        return taskId;
    }

    // OTHER

    public long getTick() {
        return this.tick;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

}
