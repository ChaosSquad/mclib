package net.chaossquad.mclib.commands;

import net.chaossquad.mclib.scheduler.OneTimeTask;
import net.chaossquad.mclib.scheduler.RepeatingTask;
import net.chaossquad.mclib.scheduler.Task;
import net.chaossquad.mclib.scheduler.TaskScheduler;
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

public class TasksSubcommand {

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

                String out = "§7§lTask Info:§r\n" +
                        "§7ID: " + taskId + " (" + task.getId() + ")\n" +
                        "§7Label: " + task.getLabel() + "\n" +
                        "§7Type: " + task + "\n" +
                        "§7Should run: " + task.shouldRun() + "\n" +
                        "§7To be removed: " + task.toBeRemoved() + "\n" +
                        "§7Marked for removal: " + task.isMarkedForRemoval() + "\n" +
                        "§7Paused: " + task.isPaused() + "\n" +
                        "§7Remove condition: " + task.getRemoveCondition();

                if (task instanceof RepeatingTask repeatingTask) {

                    out = out + "\n" +
                            "§7Interval: " + repeatingTask.getInterval() + "\n" +
                            "§7Last execution: " + repeatingTask.getLastExecutionTick();

                } else if (task instanceof OneTimeTask oneTimeTask) {

                    out = out + "\n" +
                            "§7Executed: " + oneTimeTask.isExecuted() + "\n" +
                            "§7Added tick: " + oneTimeTask.getAddedTick() + "\n" +
                            "§7Delay: " + oneTimeTask.getDelay();

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

    public static List<String> onTabComplete(TaskScheduler taskScheduler, CommandSender sender, String[] args) {

        return switch (args.length) {
            case 1 -> taskScheduler.getTasks().keySet().stream().map(String::valueOf).collect(Collectors.toList());
            case 2 -> List.of("pause", "run", "remove");
            default -> List.of();
        };

    }

}
