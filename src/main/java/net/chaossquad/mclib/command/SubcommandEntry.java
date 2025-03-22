package net.chaossquad.mclib.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A subcommand entry represents one command that was added to {@link SubcommandCommand}.
 * @param executor {@link CommandExecutor}.
 * @param tabCompleter {@link TabCompleter}
 * @param permission permission provider that is required to use the command or null for no permission
 */
public record SubcommandEntry(CommandExecutor executor, TabCompleter tabCompleter, @Nullable SubcommandCommand.PermissionProvider permission) {

    /**
     * Creates a SubcommandEntry.
     * @param command command with CommandExecutor and TabCompleter
     * @param permission permission
     * @return SubcommandEntry
     */
    public static SubcommandEntry of(@NotNull TabCompletingCommandExecutor command, @NotNull SubcommandCommand.PermissionProvider permission) {
        return new SubcommandEntry(command, command, permission);
    }

    /**
     * Creates a SubcommandEntry.
     * @param command command with CommandExecutor and TabCompleter
     * @param permission permission
     * @return SubcommandEntry
     */
    public static SubcommandEntry of(@NotNull TabCompletingCommandExecutor command, @Nullable String permission) {
        return new SubcommandEntry(command, command, permission != null ? sender -> sender.hasPermission(permission) : null);
    }

    /**
     * Creates a SubcommandEntry.
     * @param command command with CommandExecutor and TabCompleter
     * @return SubcommandEntry
     */
    public static SubcommandEntry of(@NotNull TabCompletingCommandExecutor command) {
        return new SubcommandEntry(command, command, null);
    }

}
