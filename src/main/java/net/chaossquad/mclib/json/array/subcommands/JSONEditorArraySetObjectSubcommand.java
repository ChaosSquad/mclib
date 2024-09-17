package net.chaossquad.mclib.json.array.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.json.array.JSONEditorArraySubcommand;
import net.chaossquad.mclib.json.object.JSONEditorObjectSubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONEditorArraySetObjectSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorArraySubcommand parent;

    public JSONEditorArraySetObjectSubcommand(@NotNull JSONEditorArraySubcommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        JSONArray editable = this.parent.getManager().getJSONArrayEditable(sender);
        if (editable == null) {
            sender.sendMessage(JSONEditorArraySubcommand.NO_EDITABLE_MESSAGE);
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: [...] " + label + " <index>\n" +
                    "§cPuts the currently saved object into the specified index"
            );
            return true;
        }

        int index;
        try {
            index = Integer.parseInt(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cYou need to specify a valid index");
            return true;
        }

        JSONObject object = this.parent.getManager().getJSONObjectEditable(sender);
        if (object == null) {
            sender.sendMessage(JSONEditorObjectSubcommand.NO_EDITABLE_MESSAGE);
            return true;
        }

        editable.put(index, new JSONObject(object.toString()));
        sender.sendMessage("§aSet " + index + " to the current saved json object");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            JSONArray editable = this.parent.getManager().getJSONArrayEditable(sender);
            if (editable == null || editable.isEmpty()) return List.of();

            List<String> result = new ArrayList<>();
            for (int i = 0; i < editable.length(); i++) {
                result.add(String.valueOf(i));
            }
            return result;
        }

        return List.of();
    }

    public @NotNull JSONEditorArraySubcommand getParent() {
        return parent;
    }
}
