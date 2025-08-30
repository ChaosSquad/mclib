package net.chaossquad.mclib.scheduler;

import net.chaossquad.mclib.misc.Removable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * A task scheduler that uses another task scheduler as parent.<br/>
 * The {@link net.chaossquad.mclib.commands.TasksSubcommand} recognizes this type of tasks and makes it possible to manage them.
 */
public class ChildTaskScheduler extends TaskScheduler implements TaskRunnable {

    /**
     * Creates a task scheduler.
     *
     * @param logger logger
     * @param parent the task scheduler that is used for tasks
     */
    public ChildTaskScheduler(@NotNull Logger logger, @NotNull SchedulerInterface parent, @Nullable Removable removeCondition, @NotNull String name) {
        super(logger);
        parent.scheduleRepeatingTask(this, 1, 1, removeCondition, "child_scheduler_" + name);
    }

    /**
     * Creates a task scheduler.
     *
     * @param logger logger
     */
    public ChildTaskScheduler(@NotNull Logger logger, @NotNull SchedulerInterface parent, @NotNull String name) {
        this(logger, parent, null, name);
    }

    @Override
    public void run(Task task) {
        this.tick();
    }

}
