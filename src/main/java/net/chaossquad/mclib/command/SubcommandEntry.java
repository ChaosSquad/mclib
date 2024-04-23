package net.chaossquad.mclib.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public record SubcommandEntry(CommandExecutor executor, TabCompleter tabCompleter, String permission) {

    public static SubcommandEntry of(TabCompletingCommandExecutor command, String permission) {
        return new SubcommandEntry(command, command, permission);
    }

    public static SubcommandEntry of(TabCompletingCommandExecutor command) {
        return new SubcommandEntry(command, command, null);
    }

}
