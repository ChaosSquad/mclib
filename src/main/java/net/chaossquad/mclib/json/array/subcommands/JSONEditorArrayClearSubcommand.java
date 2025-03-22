package net.chaossquad.mclib.json.array.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.json.array.JSONEditorArraySubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.util.List;

/**
 * {@link net.chaossquad.mclib.json.JSONEditorCommand}.
 */
public class JSONEditorArrayClearSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorArraySubcommand parent;

    /**
     * Creates the object.
     * @param parent parent command
     */
    public JSONEditorArrayClearSubcommand(@NotNull JSONEditorArraySubcommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        JSONArray editable = this.parent.getManager().getJSONArrayEditable(sender);

        if (editable == null) {
            sender.sendMessage(JSONEditorArraySubcommand.NO_EDITABLE_MESSAGE);
            return true;
        }

        editable.clear();
        sender.sendMessage("§aCleared json array");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }

    /**
     * Returns the parent command.
     * @return parent command
     */
    public @NotNull JSONEditorArraySubcommand getParent() {
        return parent;
    }
}
