package net.chaossquad.mclib.json.object.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.json.object.JSONEditorObjectSubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

/**
 * {@link net.chaossquad.mclib.json.JSONEditorCommand}.
 */
public class JSONEditorObjectUnloadSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorObjectSubcommand parent;

    /**
     * Creates the object.
     * @param parent parent
     */
    public JSONEditorObjectUnloadSubcommand(@NotNull JSONEditorObjectSubcommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        JSONObject editable = this.parent.getManager().getJSONObjectEditable(sender);

        if (editable == null) {
            sender.sendMessage(JSONEditorObjectSubcommand.NO_EDITABLE_MESSAGE);
            return true;
        }

        this.parent.getManager().setJSONObjectEditable(sender, null);
        sender.sendMessage("§aUnloaded json object: " + Objects.toIdentityString(editable));
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
    public @NotNull JSONEditorObjectSubcommand getParent() {
        return parent;
    }
}
