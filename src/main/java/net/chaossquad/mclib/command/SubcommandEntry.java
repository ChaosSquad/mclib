package net.chaossquad.mclib.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

/**
 * A subcommand entry represents one command that was added to {@link SubcommandCommand}.
 * @param executor {@link CommandExecutor}.
 * @param tabCompleter {@link TabCompleter}
 * @param permission permission that is required to use the command or null for no permission
 */
public record SubcommandEntry(CommandExecutor executor, TabCompleter tabCompleter, String permission) {

    public static SubcommandEntry of(TabCompletingCommandExecutor command, String permission) {
        return new SubcommandEntry(command, command, permission);
    }

    public static SubcommandEntry of(TabCompletingCommandExecutor command) {
        return new SubcommandEntry(command, command, null);
    }

}
