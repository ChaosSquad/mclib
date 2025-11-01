package net.chaossquad.mclib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

/**
 * A command that can has multiple commands as subcommands.<br/>
 * You can add subcommands with {@link #addSubcommand(String, SubcommandEntry)}.<br/>
 * You can remove subcommands with {@link #removeSubcommand(String)}.<br/>
 * You can set a global permission for the command in the constructor<br/>
 * You can set a {@link DynamicSubcommandProvider} that can provide subcommands dynamically in the constructor.<br/>
 * You can override {@link #onExecutionWithoutSubcommand(CommandSender, Command, String)} to run code when the command is executed without subcommands.<br/>
 * You can override {@link #onExecutionWithoutPermission(CommandSender, Command, String, String[], String)} to run code when the sender does not have the permission for the command.<br/>
 * You can override {@link #onExecutionWithUnknownSubcommand(CommandSender, Command, String, String[])} to run code when the given subcommand does not exist.
 */
public class SubcommandCommand implements TabCompletingCommandExecutor {
    @NotNull private final Plugin plugin;
    @NotNull private final Map<String, SubcommandEntry> entries;
    @Nullable private final DynamicSubcommandProvider dynamicSubcommandProvider;
    @Nullable private final PermissionProvider permissionProvider;
    @Nullable private final CommandExecutor noSubcommandExecutor;
    @Nullable private final CommandExecutor noPermissionExecutor;
    @Nullable private final CommandExecutor unknownSubcommandExecutor;

    /**
     * Creates a new SubcommandCommand.
     * @param plugin plugin
     * @param permissionProvider permission provider
     * @param dynamicSubcommandProvider dynamic subcommand provider
     * @param noSubcommandExecutor This is executed when there is no subcommand provided
     * @param noPermissionExecutor This is executed when the player has no permission.
     * @param unknownSubcommandExecutor This is executed when the player specified an invalid subcommand.
     */
    public SubcommandCommand(
            @NotNull Plugin plugin,
            @Nullable PermissionProvider permissionProvider,
            @Nullable DynamicSubcommandProvider dynamicSubcommandProvider,
            @Nullable CommandExecutor noSubcommandExecutor,
            @Nullable CommandExecutor noPermissionExecutor,
            @Nullable CommandExecutor unknownSubcommandExecutor
    ) {
        this.plugin = plugin;
        this.entries = new HashMap<>();
        this.dynamicSubcommandProvider = dynamicSubcommandProvider;
        this.permissionProvider = permissionProvider;
        this.noSubcommandExecutor = noSubcommandExecutor;
        this.noPermissionExecutor = noPermissionExecutor;
        this.unknownSubcommandExecutor = unknownSubcommandExecutor;
    }

    /**
     * Creates a new SubcommandCommand.
     * @param plugin plugin
     * @param permissionProvider permission provider
     * @param noSubcommandExecutor This is executed when there is no subcommand provided
     * @param noPermissionExecutor This is executed when the player has no permission.
     * @param unknownSubcommandExecutor This is executed when the player specified an invalid subcommand.
     */
    public SubcommandCommand(
            @NotNull Plugin plugin,
            @Nullable PermissionProvider permissionProvider,
            @Nullable CommandExecutor noSubcommandExecutor,
            @Nullable CommandExecutor noPermissionExecutor,
            @Nullable CommandExecutor unknownSubcommandExecutor
    ) {
        this(plugin, permissionProvider, null, noSubcommandExecutor, noPermissionExecutor, unknownSubcommandExecutor);
    }

    /**
     * Creates a new SubcommandCommand.
     * @param plugin plugin
     * @param dynamicSubcommandProvider dynamic subcommand provider
     * @param noSubcommandExecutor This is executed when there is no subcommand provided
     * @param noPermissionExecutor This is executed when the player has no permission.
     * @param unknownSubcommandExecutor This is executed when the player specified an invalid subcommand.
     */
    public SubcommandCommand(
            @NotNull Plugin plugin,
            DynamicSubcommandProvider dynamicSubcommandProvider,
            @Nullable CommandExecutor noSubcommandExecutor,
            @Nullable CommandExecutor noPermissionExecutor,
            @Nullable CommandExecutor unknownSubcommandExecutor
    ) {
        this(plugin, null, dynamicSubcommandProvider, noSubcommandExecutor, noPermissionExecutor, unknownSubcommandExecutor);
    }

    /**
     * Creates a new SubcommandCommand.
     * @param plugin plugin
     * @param permissionProvider permission provider
     * @param dynamicSubcommandProvider dynamic subcommand provider
     */
    public SubcommandCommand(@NotNull Plugin plugin, @Nullable PermissionProvider permissionProvider, @Nullable DynamicSubcommandProvider dynamicSubcommandProvider) {
        this(plugin, permissionProvider, dynamicSubcommandProvider, null, null, null);
    }

    /**
     * Creates a new SubcommandCommand.
     * @param plugin plugin
     * @param permissionProvider permission provider
     */
    public SubcommandCommand(@NotNull Plugin plugin, @NotNull PermissionProvider permissionProvider) {
        this(plugin, permissionProvider, null);
    }

    /**
     * Creates a new SubcommandCommand.
     * @param plugin plugin
     */
    public SubcommandCommand(@NotNull Plugin plugin) {
        this(plugin, (PermissionProvider) null, null);
    }

    /**
     * Creates a new SubcommandCommand.
     * @param plugin plugin
     * @param permission permission
     * @param dynamicSubcommandProvider dynamic subcommand provider
     */
    public SubcommandCommand(@NotNull Plugin plugin, @Nullable String permission, @Nullable DynamicSubcommandProvider dynamicSubcommandProvider) {
        this(plugin, permission != null ? sender -> sender.hasPermission(permission) : null, dynamicSubcommandProvider);
    }

    /**
     * Creates a new SubcommandCommand.
     * @param plugin plugin
     * @param permission permission
     */
    public SubcommandCommand(@NotNull Plugin plugin, @Nullable String permission) {
        this(plugin, permission, null);
    }

    // COMMAND

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!this.hasPermission(sender)) {
            this.onExecutionWithoutPermission(sender, cmd, label, args, null);
            return true;
        }

        if (args.length > 0) {

            SubcommandEntry subcommand = this.getSubcommand(args[0]);
            if (subcommand == null) {

                if (this.unknownSubcommandExecutor != null) {
                    this.unknownSubcommandExecutor.onCommand(sender, cmd, label, args);
                } else {
                    this.onExecutionWithUnknownSubcommand(sender, cmd, label, args);
                }

                return true;
            }

            if (subcommand.executor() == null) {
                sender.sendMessage("§cSubcommand cannot be executed");
                return true;
            }

            if (!this.hasCommandPermission(subcommand, sender)) {

                if (this.noPermissionExecutor != null) {
                    this.noPermissionExecutor.onCommand(sender, cmd, label, args);
                } else {
                    this.onExecutionWithoutPermission(sender, cmd, label, args, args[0]);
                }

                return true;
            }

            subcommand.executor().onCommand(sender, cmd, args[0], this.subcommandArguments(args));

        } else {

            if (this.noSubcommandExecutor != null) {
                this.noSubcommandExecutor.onCommand(sender, cmd, label, new String[]{});
            } else {
                this.onExecutionWithoutSubcommand(sender, cmd, label);
            }

        }

        return true;
    }

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {

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

    /**
     * This is executed when no subcommand is specified.<br/>
     * This method is not called when a noSubcommandExecutor is specified.
     * @param sender sender
     * @param cmd cmd
     * @param label label
     */
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

    /**
     * This is executed when the sender has no permission for the command.<br/>
     * This method is not called when a noPermissionExecutor is specified.
     * @param sender sender
     * @param cmd command
     * @param label label
     * @param args args
     * @param subcommand the subcommand the sender has executed (null if it is the main command)
     */
    protected void onExecutionWithoutPermission(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args, @Nullable String subcommand) {
        sender.sendMessage("§cNo permission");
    }

    /**
     * This is executed when the specified subcommand does not exist.<br/>
     * This method is not executed when a unknownSubcommandExecutor is specified.
     * @param sender sender
     * @param cmd cmd
     * @param label label
     * @param args args
     */
    protected void onExecutionWithUnknownSubcommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("§cUnknown subcommand");
    }

    // ----- UTILITIES -----

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasPermission(CommandSender sender) {
        if (this.permissionProvider == null) return true;
        if (sender == this.plugin.getServer().getConsoleSender()) return true;

        try {
            return this.permissionProvider.hasPermission(sender);
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to check command permission: Exception in permission provider", e);
            return false;
        }
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
        if (sender == this.plugin.getServer().getConsoleSender()) return true;
        if (command.permission() == null) return true;

        try {
            return command.permission().hasPermission(sender);
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to check command permission: Exception in permission provider", e);
            return false;
        }
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

    /**
     * Returns the executor that is called when no subcommand was specified.
     * @return command executor
     */
    @Nullable
    public final CommandExecutor getNoSubcommandExecutor() {
        return this.noSubcommandExecutor;
    }

    /**
     * Returns the executor that is called when the sender does not have the permission to run the command.
     * @return command executor
     */
    @Nullable
    public final CommandExecutor getNoPermissionExecutor() {
        return this.noPermissionExecutor;
    }

    /**
     * Returns the executor that is called when the specified subcommand does not exist.
     * @return command executor
     */
    @Nullable
    public final CommandExecutor getUnknownSubcommandExecutor() {
        return this.unknownSubcommandExecutor;
    }

    // ----- INNER CLASSES -----

    /**
     * Provides the permission for the command.<br/>
     * This allows for dynamically providing a permission instead of the permission string.
     */
    public interface PermissionProvider {

        /**
         * Returns true if the specified sender has the permission
         * @param sender sender
         * @return permission
         */
        boolean hasPermission(@NotNull CommandSender sender);

    }

}
