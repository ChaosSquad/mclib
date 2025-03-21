package net.chaossquad.mclib.json.object.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.json.object.JSONEditorObjectSubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.List;

public class JSONEditorObjectSetObjectSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorObjectSubcommand parent;

    public JSONEditorObjectSetObjectSubcommand(@NotNull JSONEditorObjectSubcommand parent) {
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
            sender.sendMessage("§cUsage: [...] " + label + " <key>\n" +
                    "§cPuts the currently saved array into the specified key"
            );
            return true;
        }

        String key = args[0];

        editable.put(key, new JSONObject());
        sender.sendMessage("§aSet " + key + " to a new json object");
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
