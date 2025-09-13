package net.chaossquad.mclib.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A class that can parse options from a command.
 */
public final class OptionParser {

    private OptionParser() {}

    /**
     * Option parser for {@link org.bukkit.command.CommandExecutor}.
     * @param arguments args
     * @return {@link Result}
     */
    public static Result parse(@NotNull String[] arguments) {
        List<String> args = new ArrayList<>(Arrays.asList(arguments));
        Map<@NotNull String, @NotNull String> options = new HashMap<>();

        Iterator<String> iterator = args.iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();

            // Parse long options with their values
            if (arg.startsWith("--")) {
                arg = arg.substring(2);

                String[] split = arg.split("=", 2);
                options.put(split[0], split.length > 1 ? split[1] : "");

                iterator.remove();
                continue;
            }

            if (arg.startsWith("-")) {
                arg = arg.substring(1);

                for (char c : arg.toCharArray()) {
                    options.put(String.valueOf(c), "");
                }

                iterator.remove();
                continue;
            }

        }

        return new Result(args.toArray(String[]::new), options);
    }

    /**
     * Stores the result from parsing the options.
     * @param args arguments (without options)
     * @param options options
     */
    public record Result(String[] args, Map<@NotNull String, @NotNull String> options) {

        /**
         * Checks if the specified option is set.
         * @param option option
         * @return option set
         */
        public boolean hasOption(@NotNull String... option) {

            for (String s : option) {
                if (this.options.containsKey(s)) {
                    return true;
                }
            }

            return false;
        }

    }

    /**
     * Tab-completer for {@link org.bukkit.command.TabCompleter}.
     * @param sender sender
     * @param args args
     * @param argsCompleter Completer that completes command arguments (not options).
     * @param uncompletedAvailableOptions options that have no own tab completer (options without a value, format: --my-option)
     * @param completedAvailableOptions options that have an own completer (options with a value, format: --my-option=value)
     * @return available values
     */
    public static List<String> complete(@NotNull CommandSender sender, @NotNull Result args, @Nullable OptionCompleter argsCompleter, @Nullable Set<String> uncompletedAvailableOptions, @Nullable Map<@NotNull String, @NotNull OptionCompleter> completedAvailableOptions) {
        Set<String> currentOptions = new HashSet<>(args.options().keySet());

        List<String> suggestions = new ArrayList<>();

        // Complete available options without a completer
        if (uncompletedAvailableOptions != null) {

            for (String option : uncompletedAvailableOptions) {
                if (!currentOptions.contains(option)) {
                    suggestions.add("--" + option);
                }
            }

        }

        // Complete available options with a completer
        if (completedAvailableOptions != null) {

            for (String option : completedAvailableOptions.keySet()) {
                if (currentOptions.contains(option)) continue;

                OptionCompleter optionCompleter = completedAvailableOptions.get(option);
                if (optionCompleter != null) {

                    try {
                        for (String value : optionCompleter.onTabComplete(sender, args)) {
                            suggestions.add("--" + option + "=" + value);
                        }
                    } catch (Exception e) {
                        suggestions.add(e.getClass().getSimpleName() + ": " + e.getMessage());
                    }

                }

            }

        }

        // Complete arguments
        if (argsCompleter != null) {

            try {
                suggestions.addAll(argsCompleter.onTabComplete(sender, args));
            } catch (Exception e) {
                suggestions.add(e.getClass().getSimpleName() + ": " + e.getMessage());
            }

        }

        return suggestions;
    }

    /**
     * Tab-completer for the {@link #complete(CommandSender, Result, OptionCompleter, Set, Map)}  method.<br/>
     * Works as {@link TabCompleter}, but does not have the command and label arguments.
     */
    public interface OptionCompleter {

        /**
         * Called when an argument/option is completed.
         * @param sender sender
         * @param args args
         * @return available values
         */
        List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Result args);

    }

}
