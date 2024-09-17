package net.chaossquad.mclib.json.array.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.json.array.JSONEditorArraySubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.util.List;

public class JSONEditorArrayShowSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final JSONEditorArraySubcommand parent;

    public JSONEditorArrayShowSubcommand(@NotNull JSONEditorArraySubcommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        JSONArray editable = this.parent.getManager().getJSONArrayEditable(sender);

        if (editable == null) {
            sender.sendMessage(JSONEditorArraySubcommand.NO_EDITABLE_MESSAGE);
            return true;
        }

        boolean formatted = false;
        if (args.length > 0) formatted = Boolean.parseBoolean(args[0]);

        String out;
        if (formatted) {
            out = editable.toString(4);
        } else {
            out = editable.toString();
        }

        sender.sendMessage("§7§lJSON ARRAY OUTPUT:§r\n§7" + out + "§r\n§7§l----- OUTPUT END -----");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return List.of("false", "true");
        return List.of();
    }

    public @NotNull JSONEditorArraySubcommand getParent() {
        return parent;
    }
}
