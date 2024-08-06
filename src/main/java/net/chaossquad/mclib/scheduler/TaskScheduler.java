package net.chaossquad.mclib.scheduler;

import org.bukkit.plugin.Plugin;

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
    private final Map<Integer, Task> tasks;
    private long tick;

    public TaskScheduler(Plugin plugin) {
        this.plugin = plugin;
        this.tasks = Collections.synchronizedMap(new HashMap<>());
        this.tick = 0;
    }

    // TICK

    public boolean tick() {
        boolean hadException = false;

        synchronized (this.tasks) {

            for (Integer taskId : Map.copyOf(this.tasks).keySet()) {

                if (taskId == null) {
                    this.tasks.remove(null);
                    continue;
                }

                Task task = this.tasks.get(taskId);

                if (task == null) {
                    this.tasks.remove(taskId);
                    continue;
                }

                if (task.shouldRun() && !task.isPaused()) {

                    try {
                        task.run(this, taskId);
                    } catch (Exception e) {
                        this.plugin.getLogger().log(Level.WARNING, "Exception in scheduler task with id " + taskId, e);
                        hadException = true;
                    }

                }

                if (task.shouldBeRemoved()) {
                    this.tasks.remove(taskId);
                    continue;
                }

            }

        }

        this.tick++;

        return hadException;
    }

    // TASK ID MANAGEMENT

    private int getFreeTaskId() {

        for (int i = 0; i < Integer.MAX_VALUE; i++) {

            if (this.tasks.containsKey(i)) {
                continue;
            }

            return i;

        }

        return -1;
    }

    // GET TASKS

    /**
     * Returns a copy of the map where the tasks are stored.
     * @return map of tasks
     */
    public Map<Integer, Task> getTasks() {
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
     * @param stopCondition the condition the task should be stopped and removed
     * @return task id
     */
    public int scheduleRepeatingTask(TaskRunnable runnable, long delay, long interval, TaskStopCondition stopCondition, String label) {
        int taskId = this.getFreeTaskId();
        this.tasks.put(taskId, new RepeatingTask(this, runnable, label, delay, interval, stopCondition));
        return taskId;
    }
    public int scheduleRepeatingTask(TaskRunnable runnable, long delay, long interval, TaskStopCondition stopCondition) {
        return this.scheduleRepeatingTask(runnable, delay, interval, stopCondition, null);
    }
    public int scheduleRepeatingTask(TaskRunnable runnable, long delay, long interval, String label) {
        return this.scheduleRepeatingTask(runnable, delay, interval, null, label);
    }
    public int scheduleRepeatingTask(TaskRunnable runnable, long delay, long interval) {
        return this.scheduleRepeatingTask(runnable, delay, interval, (TaskStopCondition) null);
    }
    public int scheduleRepeatingTask(Runnable runnable, long delay, long interval, TaskStopCondition stopCondition, String label) {
        return this.scheduleRepeatingTask(TaskRunnable.fromRunnable(runnable), delay, interval, stopCondition, label);
    }
    public int scheduleRepeatingTask(Runnable runnable, long delay, long interval, TaskStopCondition stopCondition) {
        return this.scheduleRepeatingTask(runnable, delay, interval, stopCondition, null);
    }
    public int scheduleRepeatingTask(Runnable runnable, long delay, long interval, String label) {
        return this.scheduleRepeatingTask(runnable, delay, interval, null, label);
    }
    public int scheduleRepeatingTask(Runnable runnable, long delay, long interval) {
        return this.scheduleRepeatingTask(runnable, delay, interval, (TaskStopCondition) null);
    }

    /**
     * Schedules a one-time delayed task.
     * @param runnable the runnable that should be executed
     * @param delay the delay (the amount of ticks to wait until the task will run)
     * @return task id
     */
    public int runTaskLater(TaskRunnable runnable, long delay, String label) {
        int taskId = this.getFreeTaskId();
        this.tasks.put(taskId, new OneTimeTask(this, runnable, label, delay));
        return taskId;
    }
    public int runTaskLater(TaskRunnable runnable, long delay) {
        return this.runTaskLater(runnable, delay, null);
    }
    public int runTaskLater(Runnable runnable, long delay, String label) {
        return this.runTaskLater(TaskRunnable.fromRunnable(runnable), delay, label);
    }
    public int runTaskLater(Runnable runnable, long delay) {
        return this.runTaskLater(runnable, delay, null);
    }

    // OTHER

    public long getTick() {
        return this.tick;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

}
