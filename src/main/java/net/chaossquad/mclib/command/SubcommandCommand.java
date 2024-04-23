package net.chaossquad.mclib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * A command that can has multiple commands as subcommands.
 */
public class SubcommandCommand implements TabCompletingCommandExecutor {
    private final Plugin plugin;
    private final Map<String, SubcommandEntry> entries;
    private final DynamicSubcommandProvider dynamicSubcommandProvider;
    private String availableSubcommandsMessage;

    public SubcommandCommand(Plugin plugin, DynamicSubcommandProvider dynamicSubcommandProvider, String availableSubcommandsMessage) {
        this.plugin = plugin;
        this.entries = new HashMap<>();
        this.dynamicSubcommandProvider = dynamicSubcommandProvider;
        this.availableSubcommandsMessage = Objects.requireNonNullElse(availableSubcommandsMessage, "");
    }

    public SubcommandCommand(Plugin plugin, String availableSubcommandsMessage) {
        this(plugin, null, availableSubcommandsMessage);
    }

    // COMMAND

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

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

            if (sender != this.plugin.getServer().getConsoleSender() && subcommand.permission() != null && sender.hasPermission(subcommand.permission())) {
                sender.sendMessage("§cNo permission");
                return true;
            }

            subcommand.executor().onCommand(sender, cmd, args[0], this.subcommandArguments(args));

        } else {

            String message = this.availableSubcommandsMessage;

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

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length > 1) {

            SubcommandEntry subcommand = this.getSubcommand(args[0]);
            if (subcommand == null) return List.of();
            if (sender != this.plugin.getServer().getConsoleSender() && subcommand.permission() != null && sender.hasPermission(subcommand.permission())) return List.of();

            TabCompleter completer = subcommand.tabCompleter();
            if (completer == null) return List.of();

            return completer.onTabComplete(sender, cmd, args[0], this.subcommandArguments(args));

        } else if (args.length == 1) {
            return this.getSubcommandList(sender);
        } else {
            return List.of();
        }

    }

    // UTILITIES

    private String[] subcommandArguments(String[] args) {

        if (args.length < 2) {
            return new String[0];
        }

        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        return subArgs;

    }

    private SubcommandEntry getSubcommand(String name) {

        SubcommandEntry entry = null;

        if (this.dynamicSubcommandProvider != null) entry = this.dynamicSubcommandProvider.getDynamicSubcommands().get(name);
        if (entry == null) this.entries.get(name);

        return entry;
    }

    private List<String> getSubcommandList(CommandSender sender) {

        if (sender == null) return List.of();

        List<String> commandList = new ArrayList<>();

        for (String command : List.copyOf(this.entries.keySet())) {
            SubcommandEntry entry = this.entries.get(command);
            if (entry == null) continue;

            if (sender == this.plugin.getServer().getConsoleSender() || entry.permission() == null || sender.hasPermission(entry.permission())) {
                commandList.add(command);
            }

        }

        if (this.dynamicSubcommandProvider != null) {
            for (String command : List.copyOf(this.dynamicSubcommandProvider.getDynamicSubcommands().keySet())) {
                SubcommandEntry entry = this.entries.get(command);
                if (entry == null) continue;

                if (sender == this.plugin.getServer().getConsoleSender() || entry.permission() == null || sender.hasPermission(entry.permission())) {
                    commandList.add(command);
                }

            }
        }

        return commandList;

    }

    // MANAGE SUBCOMMANDS

    /**
     * Adds a subcommand.
     * @param command command string
     * @param data command data
     */
    public void addSubcommand(String command, SubcommandEntry data) {
        this.entries.put(command, data);
    }

    /**
     * Removes a subcommand.
     * @param command command string
     */
    public void removeSubcommand(String command) {
        this.entries.remove(command);
    }

    /**
     * Clears all subcommands.
     */
    public void clearSubcommands() {
        this.entries.clear();
    }

    // GETTER / SETTER

    /**
     * Returns the plugin.
     * @return plugin
     */
    public Plugin getPlugin() {
        return this.plugin;
    }

    /**
     * Returns a map of all registered subcommands.
     * @return map of subcommands
     */
    public Map<String, SubcommandEntry> getSubcommands() {
        return Map.copyOf(this.entries);
    }

    /**
     * Returns the dynamic subcommand provider.
     * Read {@link DynamicSubcommandProvider} for more information.
     * @return dynamic subcommand provider
     */
    public DynamicSubcommandProvider getDynamicSubcommandProvider() {
        return this.dynamicSubcommandProvider;
    }

    /**
     * Get message that is displayed when the user runs the command without arguments.
     * @return available subcommands message
     */
    public String getAvailableSubcommandsMessage() {
        return availableSubcommandsMessage;
    }

    /**
     * Set message that is displayed when the user runs the command without arguments.
     * @param availableSubcommandsMessage available subcommands message
     */
    public void setAvailableSubcommandsMessage(String availableSubcommandsMessage) {
        this.availableSubcommandsMessage = Objects.requireNonNullElse(availableSubcommandsMessage, "");
    }

}
