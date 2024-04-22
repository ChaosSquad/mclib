package net.chaossquad.mclib.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public record SubcommandEntry(CommandExecutor executor, TabCompleter tabCompleter, String permission) {
}
