package net.chaossquad.mclib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

/**
 * A command that can has multiple commands as subcommands.<br/>
 * You can add subcommands with {@link this#addSubcommand(String, SubcommandEntry)}.<br/>
 * You can remove subcommands with {@link this#removeSubcommand(String)}.<br/>
 * You can set a global permission for the command in the constructor<br/>
 * You can set a {@link DynamicSubcommandProvider} that can provide subcommands dynamically in the constructor.<br/>
 * You can override {@link this#onExecutionWithoutSubcommand(CommandSender, Command, String)} to run code when the command is executed without subcommands.
 */
public class SubcommandCommand implements TabCompletingCommandExecutor {
    @NotNull private final Plugin plugin;
    @NotNull private final Map<String, SubcommandEntry> entries;
    @Nullable private final DynamicSubcommandProvider dynamicSubcommandProvider;
    @Nullable private final String permission;

    public SubcommandCommand(@NotNull Plugin plugin, @Nullable String permission, @Nullable DynamicSubcommandProvider dynamicSubcommandProvider) {
        this.plugin = plugin;
        this.entries = new HashMap<>();
        this.dynamicSubcommandProvider = dynamicSubcommandProvider;
        this.permission = permission;
    }

    public SubcommandCommand(@NotNull Plugin plugin, @Nullable String permission) {
        this(plugin, permission, null);
    }

    public SubcommandCommand(@NotNull Plugin plugin) {
        this(plugin, null);
    }

    // COMMAND

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!this.hasPermission(sender)) {
            sender.sendMessage("§cNo permission");
            return true;
        }

        if (args.length > 0) {

            SubcommandEntry subcommand = this.getSubcommand(args[0]);
            if (subcommand == null) {
                sender.sendMessage("§cUnknown subcommand");
                return true;
            }

            if (subcommand.executor() == null) {
                sender.sendMessage("§cSubcommand cannot be executed");
                return true;
            }

            if (!this.hasCommandPermission(subcommand, sender)) {
                sender.sendMessage("§cNo permission");
                return true;
            }

            subcommand.executor().onCommand(sender, cmd, args[0], this.subcommandArguments(args));

        } else {
            this.onExecutionWithoutSubcommand(sender, cmd, label);
        }

        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        if (!this.hasPermission(sender)) {
            return List.of();
        }

        if (args.length > 1) {

            SubcommandEntry subcommand = this.getSubcommand(args[0]);
            if (subcommand == null) return List.of();
            if (!this.hasCommandPermission(subcommand, sender)) return List.of();

            TabCompleter completer = subcommand.tabCompleter();
            if (completer == null) return List.of();

            return completer.onTabComplete(sender, cmd, args[0], this.subcommandArguments(args));

        } else if (args.length == 1) {
            return this.getSubcommandList(sender);
        } else {
            return List.of();
        }

    }

    // ----- NO SUBCOMMAND ACTION -----

    protected void onExecutionWithoutSubcommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label) {

        String message = "§cAvailable subcommands: ";

        Iterator<String> iterator = this.getSubcommandList(sender).iterator();
        while (iterator.hasNext()) {
            String subcommand = iterator.next();

            message = message + subcommand;

            if (iterator.hasNext()) {
                message = message + ", ";
            }

        }

        sender.sendMessage(message);

    }

    // ----- UTILITIES -----

    private boolean hasPermission(CommandSender sender) {
        return this.permission == null || this.plugin.getServer().getConsoleSender() == sender || sender.hasPermission(this.permission);
    }

    private String[] subcommandArguments(String[] args) {

        if (args.length < 2) {
            return new String[0];
        }

        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        return subArgs;

    }

    private boolean hasCommandPermission(SubcommandEntry command, CommandSender sender) {
        return sender == this.plugin.getServer().getConsoleSender() || command.permission() == null || sender.hasPermission(command.permission());
    }

    private SubcommandEntry getSubcommand(String name) {

        SubcommandEntry entry = this.entries.get(name);

        if (entry != null) return entry; // Return if entry was found

        if (this.dynamicSubcommandProvider != null) {

            try {
                entry = this.dynamicSubcommandProvider.getDynamicSubcommands().get(name);
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "Exception in dynamic subcommand provider", e);
            }

        }

        return entry;
    }

    private List<String> getSubcommandList(CommandSender sender) {

        if (sender == null) return List.of();

        List<String> commandList = new ArrayList<>();

        for (String command : List.copyOf(this.entries.keySet())) {
            SubcommandEntry entry = this.entries.get(command);
            if (entry == null) continue;

            if (this.hasCommandPermission(entry, sender)) {
                commandList.add(command);
            }

        }

        if (this.dynamicSubcommandProvider != null) {
            Map<String, SubcommandEntry> entries = Map.copyOf(this.dynamicSubcommandProvider.getDynamicSubcommands());
            for (String command : entries.keySet()) {
                SubcommandEntry entry = entries.get(command);
                if (entry == null) continue;

                if (this.hasCommandPermission(entry, sender)) {
                    commandList.add(command);
                }

            }
        }

        return commandList;

    }

    // ----- MANAGE SUBCOMMANDS -----

    /**
     * Adds a subcommand.
     * @param command command string
     * @param data command data
     * @throws IllegalArgumentException when subcommand already exists
     */
    public final void addSubcommand(String command, SubcommandEntry data) {
        if (this.entries.containsKey(command)) throw new IllegalArgumentException("Duplicate subcommand");
        this.entries.put(command, data);
    }

    /**
     * Removes a subcommand.
     * @param command command string
     */
    public final void removeSubcommand(String command) {
        this.entries.remove(command);
    }

    /**
     * Clears all subcommands.
     */
    public final void clearSubcommands() {
        this.entries.clear();
    }

    // ----- GETTER / SETTER -----

    /**
     * Returns the plugin.
     * @return plugin
     */
    @NotNull
    public final Plugin getPlugin() {
        return this.plugin;
    }

    /**
     * Returns a map of all registered subcommands.
     * @return map of subcommands
     */
    @NotNull
    public final Map<String, SubcommandEntry> getSubcommands() {
        return Map.copyOf(this.entries);
    }

    /**
     * Returns the dynamic subcommand provider.
     * Read {@link DynamicSubcommandProvider} for more information.
     * @return dynamic subcommand provider
     */
    @Nullable
    public final DynamicSubcommandProvider getDynamicSubcommandProvider() {
        return this.dynamicSubcommandProvider;
    }

}
