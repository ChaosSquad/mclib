package net.chaossquad.mclib.json.object.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.json.object.JSONEditorObjectSubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * {@link net.chaossquad.mclib.json.JSONEditorCommand}.
 */
public class JSONEditorObjectGetSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorObjectSubcommand parent;

    /**
     * Creates the object.
     * @param parent parent
     */
    public JSONEditorObjectGetSubcommand(@NotNull JSONEditorObjectSubcommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        JSONObject editable = this.parent.getManager().getJSONObjectEditable(sender);
        if (editable == null) {
            sender.sendMessage(JSONEditorObjectSubcommand.NO_EDITABLE_MESSAGE);
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: [...] " + label + " <key>");
            return true;
        }

        String key = args[0];

        try {
            Object value = editable.get(key);

            sender.sendMessage("§7Key: " + args[0]);
            sender.sendMessage("§7Type: " + value.getClass().getName());
            sender.sendMessage("§7Value: " + value);
        } catch (JSONException e) {
            sender.sendMessage("§cFailed to get value of " + key + ": " + e.getMessage());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            JSONObject editable = this.parent.getManager().getJSONObjectEditable(sender);
            if (editable == null) return List.of();
            return List.copyOf(editable.keySet());
        }

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
