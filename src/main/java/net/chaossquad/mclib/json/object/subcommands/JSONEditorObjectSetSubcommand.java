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
public class JSONEditorObjectSetSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorObjectSubcommand parent;

    /**
     * Creates the object.
     * @param parent parent
     */
    public JSONEditorObjectSetSubcommand(@NotNull JSONEditorObjectSubcommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        JSONObject editable = this.parent.getManager().getJSONObjectEditable(sender);
        if (editable == null) {
            sender.sendMessage(JSONEditorObjectSubcommand.NO_EDITABLE_MESSAGE);
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: [...] " + label + " <key> (int|long|float|double|boolean|string) <value...>\n" +
                    "§cTo edit json objects or arrays, use load-object or load-array"
            );
            return true;
        }

        String key = args[0];
        String type = args[1];

        String value = "";
        for (int i = 2; i < args.length; i++) {
            value += args[i];
        }

        try {

            switch (type) {
                case "int" -> editable.put(key, Integer.parseInt(value));
                case "long" -> editable.put(key, Long.parseLong(value));
                case "float" -> editable.put(key, Float.parseFloat(value));
                case "double" -> editable.put(key, Double.parseDouble(value));
                case "boolean" -> editable.put(key, Boolean.parseBoolean(value));
                case "string" -> editable.put(key, value);
                default -> {
                    sender.sendMessage("§cInvalid type: " + type + " (Supported types: int, long, float, double, boolean, string)");
                    return true;
                }
            }

            sender.sendMessage("§aUpdated JSON: " + key + ": " + editable.get(key));

        } catch (JSONException e) {
            sender.sendMessage("§cFailed to set value of " + key + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cFailed to set value of " + key + ": Invalid type");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        return switch (args.length) {
            case 1 -> {
                JSONObject editable = this.parent.getManager().getJSONObjectEditable(sender);
                if (editable == null) yield List.of();
                yield List.copyOf(editable.keySet());
            }
            case 2 -> List.of("int", "long", "float", "double", "boolean", "string");
            default -> List.of();
        };

    }

    /**
     * Returns the parent
     * @return parent
     */
    public @NotNull JSONEditorObjectSubcommand getParent() {
        return parent;
    }
}
