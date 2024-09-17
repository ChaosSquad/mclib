package net.chaossquad.mclib.json.object.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.json.object.JSONEditorObjectSubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class JSONEditorObjectGetArraySubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorObjectSubcommand parent;

    public JSONEditorObjectGetArraySubcommand(@NotNull JSONEditorObjectSubcommand parent) {
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
            JSONArray value = editable.getJSONArray(key);
            this.parent.getManager().setJSONArrayEditable(sender, value);
            sender.sendMessage("§aLoaded " + key + " as new json array: " + Objects.toIdentityString(value) + "\n" +
                    "§aPlease note that you are now modifying the loaded json array with the json array command!"
            );
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

    public @NotNull JSONEditorObjectSubcommand getParent() {
        return parent;
    }
}
