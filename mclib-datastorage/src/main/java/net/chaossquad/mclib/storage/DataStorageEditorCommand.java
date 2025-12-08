package net.chaossquad.mclib.storage;

import net.jandie1505.datastorage.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A command which allows to edit a data storage.<br/>
 * You need your own command executor and call the methods in this class.
 */
public final class DataStorageEditorCommand {

    private DataStorageEditorCommand() {}

    /**
     * Runs the command.<br/>
     * {@link org.bukkit.command.CommandExecutor#onCommand(CommandSender, Command, String, String[])}.
     * @param storage data storage
     * @param sender sender
     * @param label label
     * @param args args
     * @param censoredValues values the command will not show (useful for passwords stored in the config)
     * @return result
     */
    @SuppressWarnings("SameReturnValue")
    public static boolean onCommand(@NotNull DataStorage storage, @NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Set<String> censoredValues) {

        if (args.length < 1) {
            sender.sendMessage("§cUsage: " + label + " (list [section]|get <key>|set <key> <type> <value...>|remove <key>)");
            return true;
        }

        try {

            DataStorage cv = new DataStorage();
            if (sender != Bukkit.getConsoleSender()) {
                for (String key : censoredValues) {
                    cv.set(key, true);
                }
            }

            switch (args[0]) {
                case "list" -> {

                    sender.sendMessage("§7§lStorage contents:");

                    if (args.length > 1) {

                        DataStorage section = storage.getSection(args[1]);
                        DataStorage cvSection = cv.getSection(args[1]);

                        for (Map.Entry<String, Object> entry : section.entrySet()) {
                            sender.sendMessage("§7" + args[1] + "§r§7." + entry.getKey() + (cvSection.optBoolean(entry.getKey(), false) ? " has been censored" : "§r§7: " + entry.getValue()));
                        }

                    } else {

                        for (Map.Entry<String, Object> entry : storage) {
                            sender.sendMessage("§7" + entry.getKey() + (cv.optBoolean(entry.getKey(), false) ? " has been censored" : "§r§7: " + entry.getValue()));
                        }

                    }

                }
                case "get" -> {

                    if (args.length < 2) {
                        sender.sendMessage("§cUsage: " + label + " get <key>");
                        return true;
                    }

                    Object value = storage.get(args[1]);

                    if (value == null) {
                        sender.sendMessage("§7Value of key " + args[1] + " not set");
                        return true;
                    }

                    if (cv.optBoolean(args[1], false)) {
                        sender.sendMessage("§cThe value of key " + args[1] + " has been censored");
                        return true;
                    }

                    sender.sendMessage("§7Value of type §b" + args[1] + "§r§7:");
                    sender.sendMessage("§7Type: §b" + value.getClass().getName());
                    sender.sendMessage("§7Value: §b" + value);

                }
                case "set" -> {

                    if (args.length < 4) {
                        sender.sendMessage("§cUsage: " + label + " set <key> <type> <value>");
                        return true;
                    }

                    Object value;

                    switch (args[2]) {
                        case "int" -> value = Integer.parseInt(args[3]);
                        case "long" -> value = Long.parseLong(args[3]);
                        case "double" -> value = Double.parseDouble(args[3]);
                        case "float" -> value = Float.parseFloat(args[3]);
                        case "boolean" -> value = Boolean.parseBoolean(args[3]);
                        case "string" -> {

                            if (args[3].contains("\\empty") && !args[3].contains("\\\\empty")) {
                                value = "";
                            } else {
                                String combined = "";
                                for (int i = 3; i < args.length; i++) {
                                    combined += args[i] + " ";
                                }
                                combined = combined.substring(0, combined.length() - 1);
                                value = combined;
                            }

                        }
                        case "byte" -> value = Byte.parseByte(args[3]);
                        case "short" -> value = Short.parseShort(args[3]);
                        case "char" -> value = args[3].charAt(0);
                        default -> {
                            sender.sendMessage("§cInvalid type");
                            return true;
                        }
                    }

                    storage.set(args[1], value);
                    sender.sendMessage("§aSet type of key §b" + args[1] + "§r§a to §b" + value + "§r");

                }
                case "remove" -> {

                    if (args.length < 2) {
                        sender.sendMessage("§cUsage: " + label + " remove <key>");
                        return true;
                    }

                    Object removed = storage.remove(args[1]);

                    if (removed != null) {
                        sender.sendMessage("§aRemoved value of key " + args[1]);
                    } else {
                        sender.sendMessage("§cValue of key " + args[1] + " not set");
                    }

                }
                default -> sender.sendMessage("§cInvalid subcommand. Run command without arguments for help.");
            }

        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cIllegal Argument: " + e.getMessage());
        }

        return true;
    }

    /**
     * Runs the command.<br/>
     * {@link org.bukkit.command.CommandExecutor#onCommand(CommandSender, Command, String, String[])}.
     * @param storage data storage
     * @param sender sender
     * @param label label
     * @param args args
     * @return result
     */
    public static boolean onCommand(@NotNull DataStorage storage, @NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        return onCommand(storage, sender, label, args, Set.of());
    }

    /**
     * TabCompletes the command.<br/>
     * {@link org.bukkit.command.TabCompleter#onTabComplete(CommandSender, Command, String, String[])}.
     * @param storage data storage
     * @param sender sender
     * @param args args
     * @return list of completion
     */
    public static List<String> onTabComplete(@NotNull DataStorage storage, @NotNull CommandSender sender, @NotNull String[] args) {

        if (args.length < 1) {
            return List.of();
        }

        if (args.length == 1) {
            return List.of("list", "get", "set", "remove");
        }

        try {

            return switch (args[0]) {
                case "list" -> {
                    if (args.length != 2) yield List.of();

                    String[] splitInput = args[1].split("\\.");
                    if (args[1].endsWith(".")) {
                        splitInput = Arrays.copyOf(splitInput, splitInput.length + 1);
                        splitInput[splitInput.length - 1] = "";
                    }
                    String currentKey = "";

                    for (int i = 0; i < splitInput.length - 1; i++) {
                        currentKey += splitInput[i] + ".";
                    }
                    if (!currentKey.isEmpty()) currentKey = currentKey.substring(0, currentKey.length() - 1);

                    DataStorage section = currentKey.isEmpty() ? storage : storage.getSection(currentKey);

                    String finalCurrentKey = currentKey;
                    yield section.getSections().keySet().stream().map(s -> finalCurrentKey + (finalCurrentKey.isEmpty() ? "" : ".") + s).toList();
                }
                case "get", "remove" -> {
                    if (args.length != 2) yield List.of();
                    yield storage.keySet().stream().toList();
                }
                case "set" -> {
                    if (args.length != 3) yield List.of();
                    yield List.of("int", "long", "double", "float", "boolean", "string", "byte", "short", "char");
                }
                default -> List.of();
            };

        } catch (Exception ignored) {
            // i don't care
            return List.of();
        }

    }

}
