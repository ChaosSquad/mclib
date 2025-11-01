package net.chaossquad.mclib.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

/**
 * This class is a combined {@link CommandExecutor} and {@link TabCompleter}.
 */
public interface TabCompletingCommandExecutor extends CommandExecutor, TabCompleter {
}
