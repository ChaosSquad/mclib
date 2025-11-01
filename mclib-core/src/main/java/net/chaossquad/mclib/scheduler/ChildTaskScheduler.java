package net.chaossquad.mclib.scheduler;

import net.chaossquad.mclib.misc.Removable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * A task scheduler that uses another task scheduler as parent.<br/>
 * It can be registered as a task to another TaskScheduler.<br/>
 * The {@link net.chaossquad.mclib.commands.TasksSubcommand} recognizes this type of tasks and makes it possible to manage them.
 */
public class ChildTaskScheduler extends TaskScheduler implements TaskRunnable {

    /**
     * Creates a task scheduler.
     *
     * @param logger logger
     * @param removeCondition the condition for when this scheduler is removed from its parent scheduler
     * @param name the task name for this scheduler in the parent scheduler
     */
    public ChildTaskScheduler(@NotNull Logger logger, @Nullable Removable removeCondition, @NotNull String name) {
        super(logger);
    }

    /**
     * Creates a task scheduler.
     *
     * @param logger logger
     * @param name the task name for this scheduler in the parent scheduler
     */
    public ChildTaskScheduler(@NotNull Logger logger, @NotNull String name) {
        this(logger, null, name);
    }

    @Override
    public void run(Task task) {
        this.tick();
    }

}
