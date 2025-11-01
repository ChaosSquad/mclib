package net.chaossquad.mclib.commands;

import net.chaossquad.mclib.scheduler.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A command for managing tasks of the {@link TaskScheduler}.
 */
public final class TasksSubcommand {

    private TasksSubcommand() {}

    /**
     * Call it with {@link org.bukkit.command.CommandExecutor}.
     * @param taskScheduler task scheduler
     * @param sender sender
     * @param args args
     * @return success
     */
    @SuppressWarnings("SameReturnValue")
    public static boolean onCommand(TaskScheduler taskScheduler, CommandSender sender, String[] args) {
        try {

            if (args.length < 1) {

                Component out = Component.empty();

                out = out.append(Component.text("Registered Tasks:").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD));
                out = out.appendNewline();

                Map<Long, Task> taskMap = taskScheduler.getTasks();

                if (!taskMap.isEmpty()) {

                    Iterator<Long> iterator = taskMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        long taskId = iterator.next();
                        Task task = taskMap.get(taskId);

                        Component component = Component.empty();
                        component = component.append(Component.text("[" + taskId + "] " + task.getLabel()).color(NamedTextColor.GRAY));
                        if (iterator.hasNext()) component = component.append(Component.text(", ").color(NamedTextColor.GRAY));

                        component = component.hoverEvent(HoverEvent.showText(
                                Component.text("ID: " + task.getId()).color(NamedTextColor.GRAY)
                                        .appendNewline()
                                        .append(Component.text("Label: " + task.getLabel())).color(NamedTextColor.GRAY)
                                        .appendNewline()
                                        .append(Component.text("Type: " + task)).color(NamedTextColor.GRAY)
                        ));

                        out = out.append(component);

                    }

                } else {
                    out = out.append(Component.text("None").color(NamedTextColor.GRAY));
                }

                out = out.append(Component.newline()
                        .append(Component.text("Task amount: " + taskMap.size()).color(NamedTextColor.GRAY))
                                .appendNewline()
                        .append(Component.text("Current Tick: " + taskScheduler.getTick()).color(NamedTextColor.GRAY))
                );

                sender.sendMessage(out);

                return true;
            }

            int taskId = Integer.parseInt(args[0]);
            Task task = taskScheduler.getTask(taskId);

            if (task == null) {
                sender.sendMessage("§cTask does not exist");
                return true;
            }

            if (args.length < 2) {

                Component out = Component.empty()
                        .append(Component.text("Task Info:", NamedTextColor.GRAY, TextDecoration.BOLD))
                        .appendNewline()
                        .append(Component.text("ID: " + taskId, NamedTextColor.GRAY)).appendNewline()
                        .append(Component.text("Label: " + task.getLabel(), NamedTextColor.GRAY)).appendNewline()
                        .append(Component.text("Type: " + task.toString(), NamedTextColor.GRAY)).appendNewline()
                        .append(Component.text("Should run: " + task.shouldRun(), NamedTextColor.GRAY)).appendNewline()
                        .append(Component.text("Marked for removal: " + task.isMarkedForRemoval(), NamedTextColor.GRAY)).appendNewline()
                        .append(Component.text("Paused: " + task.isPaused(), NamedTextColor.GRAY));

                try {
                    out = out.appendNewline().append(Component.text("To be removed: " + task.toBeRemoved(), NamedTextColor.GRAY));
                } catch (Exception e) {
                    out = out.appendNewline().append(Component.text("To be removed: Failed to get status", NamedTextColor.GRAY));
                }

                out = out.appendNewline()
                        .append(Component.text("Runnable: ", NamedTextColor.GRAY)).appendNewline()
                        .append(Component.text(" - Identity: " + System.identityHashCode(task.getRunnable()), NamedTextColor.GRAY)).appendNewline()
                        .append(Component.text(" - Class: " + task.getRunnable().getClass().getName(), NamedTextColor.GRAY));

                try {
                    out = out.appendNewline().append(Component.text(" - Name: " + task.getRunnable().toString(), NamedTextColor.GRAY));
                } catch (Exception e) {
                    out = out.appendNewline().append(Component.text(" - Name: Failed to get name", NamedTextColor.GRAY));
                }

                out = out.appendNewline()
                        .append(Component.text("Remove Condition: ", NamedTextColor.GRAY)).appendNewline()
                        .append(Component.text(" - Identity: " + System.identityHashCode(task.getRemoveCondition()), NamedTextColor.GRAY)).appendNewline()
                        .append(Component.text(" - Class: " + task.getRemoveCondition().getClass().getName(), NamedTextColor.GRAY));

                try {
                    out = out.appendNewline().append(Component.text(" - Name: " + task.getRemoveCondition().toString(), NamedTextColor.GRAY));
                } catch (Exception e) {
                    out = out.appendNewline().append(Component.text(" - Name: Failed to get name", NamedTextColor.GRAY));
                }

                if (task.getRunnable() instanceof ChildTaskScheduler childScheduler) {
                    out = out.appendNewline().append(Component.text("Child scheduler:", NamedTextColor.GRAY)).appendNewline()
                            .append(Component.text(" - Scheduler is child scheduler", NamedTextColor.GRAY)).appendNewline()
                            .append(Component.text(" - Task amount: " + childScheduler.getTaskCount(), NamedTextColor.GRAY)).appendNewline()
                            .append(Component.text(" - Tick: " + childScheduler.getTick(), NamedTextColor.GRAY));
                }

                if (task instanceof RepeatingTask repeatingTask) {

                    out = out.appendNewline()
                            .append(Component.text("Repeating Task:", NamedTextColor.GRAY)).appendNewline()
                            .append(Component.text(" - Interval: " + repeatingTask.getInterval(), NamedTextColor.GRAY)).appendNewline()
                            .append(Component.text(" - Last execution: " + repeatingTask.getLastExecutionTick(), NamedTextColor.GRAY));

                } else if (task instanceof OneTimeTask oneTimeTask) {

                    out = out.appendNewline()
                            .append(Component.text("One-time Task:", NamedTextColor.GRAY)).appendNewline()
                            .append(Component.text(" - Executed: " + oneTimeTask.isExecuted(), NamedTextColor.GRAY)).appendNewline()
                            .append(Component.text(" - Added tick: " + oneTimeTask.getAddedTick(), NamedTextColor.GRAY)).appendNewline()
                            .append(Component.text("Delay: " + oneTimeTask.getDelay(), NamedTextColor.GRAY));

                }

                sender.sendMessage(out);

                return true;
            }

            switch (args[1]) {
                case "pause" -> {

                    if (args.length > 2) {
                        task.setPaused(Boolean.parseBoolean(args[2]));
                        sender.sendMessage("§aPaused status updated");
                    } else {
                        sender.sendMessage("§aPaused status: " + task.isPaused());
                    }

                }
                case "remove" -> {

                    // 1 remove i swear i know what I am doing

                    if (
                            args.length < 10 ||
                                    !args[2].equalsIgnoreCase("I") ||
                                    !args[3].equalsIgnoreCase("swear") ||
                                    !args[4].equalsIgnoreCase("I") ||
                                    !args[5].equalsIgnoreCase("know") ||
                                    !args[6].equalsIgnoreCase("what") ||
                                    !args[7].equalsIgnoreCase("I") ||
                                    !args[8].equalsIgnoreCase("am") ||
                                    !args[9].equalsIgnoreCase("doing")
                    ) {
                        sender.sendMessage("§cTo remove the task, write \"I swear I know what I am doing\" behind the command.");
                        return true;
                    }

                    sender.sendMessage("§aTask " + taskId + " was removed");
                    taskScheduler.removeTask(taskId);

                }
                case "run" -> {

                    try {
                        task.run();
                        sender.sendMessage("§aTask " + taskId + " was executed successfully");
                    } catch (Exception e) {
                        sender.sendMessage(
                                "§cException while executing task " + taskId + ":\n" +
                                "§r§cMessage: " + e.getMessage() + "\n" +
                                "§r§cStacktrace: " + Arrays.toString(e.getStackTrace())
                        );
                    }

                }
            }

            return true;

        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cIllegal argument");
            return true;
        }
    }

    /**
     * Call it with {@link org.bukkit.command.TabCompleter}.
     * @param taskScheduler task scheduler
     * @param sender sender
     * @param args args
     * @return completions list
     */
    public static List<String> onTabComplete(TaskScheduler taskScheduler, CommandSender sender, String[] args) {

        return switch (args.length) {
            case 1 -> taskScheduler.getTasks().keySet().stream().map(String::valueOf).collect(Collectors.toList());
            case 2 -> List.of("pause", "run", "remove");
            default -> List.of();
        };

    }

}
