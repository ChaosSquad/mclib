package net.chaossquad.mclib.json.array.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.json.array.JSONEditorArraySubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class JSONEditorArraySetSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorArraySubcommand parent;

    public JSONEditorArraySetSubcommand(@NotNull JSONEditorArraySubcommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        JSONArray editable = this.parent.getManager().getJSONArrayEditable(sender);
        if (editable == null) {
            sender.sendMessage(JSONEditorArraySubcommand.NO_EDITABLE_MESSAGE);
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: [...] " + label + " <index> (int|long|float|double|boolean|string) <value...>\n" +
                    "§cTo edit json objects or arrays, use load-object or load-array"
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

        String type = args[1];

        String value = "";
        for (int i = 2; i < args.length; i++) {
            value += args[i];
        }

        try {

            switch (type) {
                case "int" -> editable.put(index, Integer.parseInt(value));
                case "long" -> editable.put(index, Long.parseLong(value));
                case "float" -> editable.put(index, Float.parseFloat(value));
                case "double" -> editable.put(index, Double.parseDouble(value));
                case "boolean" -> editable.put(index, Boolean.parseBoolean(value));
                case "string" -> editable.put(index, value);
                default -> {
                    sender.sendMessage("§cInvalid type: " + type + " (Supported types: int, long, float, double, boolean, string)");
                    return true;
                }
            }

            sender.sendMessage("§aUpdated JSON: " + index + ": " + editable.get(index));

        } catch (JSONException e) {
            sender.sendMessage("§cFailed to set value of index " + index + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cFailed to set value of index " + index + ": Invalid type");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        return switch (args.length) {
            case 1 -> {
                JSONArray editable = this.parent.getManager().getJSONArrayEditable(sender);
                if (editable == null || editable.isEmpty()) yield  List.of();

                List<String> result = new ArrayList<>();
                for (int i = 0; i < editable.length(); i++) {
                    result.add(String.valueOf(i));
                }
                yield  result;
            }
            case 2 -> List.of("int", "long", "float", "double", "boolean", "string");
            default -> List.of();
        };

    }

    public @NotNull JSONEditorArraySubcommand getParent() {
        return parent;
    }
}
