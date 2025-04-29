package net.chaossquad.mclib.json.utility;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.json.JSONEditorCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * {@link JSONEditorCommand}.
 */
public class JSONEditorCleanupSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorCommand parent;

    /**
     * Creates the object
     * @param parent parent
     */
    @ApiStatus.Internal
    public JSONEditorCleanupSubcommand(@NotNull JSONEditorCommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        this.parent.cleanup();
        sender.sendMessage("§aCleaned objects of unused players");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }

    /**
     * Returns the parent
     * @return parent
     */
    public JSONEditorCommand getParent() {
        return parent;
    }
}
