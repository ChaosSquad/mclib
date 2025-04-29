package net.chaossquad.mclib.json.array.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.json.array.JSONEditorArraySubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.util.List;
import java.util.Objects;

/**
 * {@link net.chaossquad.mclib.json.JSONEditorCommand}.
 */
public class JSONEditorArrayUnloadSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorArraySubcommand parent;

    /**
     * Creates the object
     * @param parent parent
     */
    @ApiStatus.Internal
    public JSONEditorArrayUnloadSubcommand(@NotNull JSONEditorArraySubcommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        JSONArray editable = this.parent.getManager().getJSONArrayEditable(sender);

        if (editable == null) {
            sender.sendMessage(JSONEditorArraySubcommand.NO_EDITABLE_MESSAGE);
            return true;
        }

        this.parent.getManager().setJSONArrayEditable(sender, null);
        sender.sendMessage("§aUnloaded json array: " + Objects.toIdentityString(editable));
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
    public @NotNull JSONEditorArraySubcommand getParent() {
        return parent;
    }
}
