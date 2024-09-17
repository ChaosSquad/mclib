package net.chaossquad.mclib.json;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.json.array.JSONEditorArraySubcommand;
import net.chaossquad.mclib.json.object.JSONEditorObjectSubcommand;
import net.chaossquad.mclib.json.utility.JSONEditorCleanupSubcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JSONEditorCommand extends SubcommandCommand {

    @NotNull private final Map<UUID, JSONObject> playerJSONObjectEditables;
    @NotNull private final Map<UUID, JSONArray> playerJSONArrayEditables;
    @Nullable private JSONObject consoleJSONObjectEditable;
    @Nullable private JSONArray consoleJSONArrayEditable;

    public JSONEditorCommand(@NotNull Plugin plugin) {
        super(plugin, "Available subcommands: cleanup, object, array");

        this.playerJSONObjectEditables = new HashMap<>();
        this.playerJSONArrayEditables = new HashMap<>();
        this.consoleJSONObjectEditable = null;
        this.consoleJSONArrayEditable = null;

        this.addSubcommand("cleanup", SubcommandEntry.of(new JSONEditorCleanupSubcommand(this)));
        this.addSubcommand("object", SubcommandEntry.of(new JSONEditorObjectSubcommand(this)));
        this.addSubcommand("array", SubcommandEntry.of(new JSONEditorArraySubcommand(this)));
    }

    // ----- EDITABLES -----

    // GLOBAL

    public JSONObject getJSONObjectEditable(@NotNull CommandSender sender) {

        if (sender instanceof Player player) {
            return this.getPlayerJSONObjectEditable(player.getUniqueId());
        } else if (sender instanceof ConsoleCommandSender console) {
            return this.getConsoleJSONObjectEditable();
        } else {
            return null;
        }

    }

    public JSONArray getJSONArrayEditable(@NotNull CommandSender sender) {

        if (sender instanceof Player player) {
            return this.getPlayerJSONArrayEditable(player.getUniqueId());
        } else if (sender instanceof ConsoleCommandSender console) {
            return this.getConsoleJSONArrayEditable();
        } else {
            return null;
        }

    }

    public void setJSONObjectEditable(@NotNull CommandSender sender, @Nullable JSONObject editable) {

        if (sender instanceof Player player) {
            this.setPlayerJSONObjectEditable(player.getUniqueId(), editable);
        } else if (sender instanceof ConsoleCommandSender console) {
            this.setConsoleJSONObjectEditable(editable);
        } else {
            return;
        }

    }

    public void setJSONArrayEditable(@NotNull CommandSender sender, @Nullable JSONArray editable) {

        if (sender instanceof Player player) {
            this.setPlayerJSONArrayEditable(player.getUniqueId(), editable);
        } else if (sender instanceof ConsoleCommandSender console) {
            this.setConsoleJSONArrayEditable(editable);
        } else {
            return;
        }

    }

    // PLAYER

    @Nullable
    public JSONObject getPlayerJSONObjectEditable(@NotNull UUID player) {
        return this.playerJSONObjectEditables.get(player);
    }

    public void setPlayerJSONObjectEditable(@NotNull UUID player, @Nullable JSONObject editable) {

        if (editable != null) {
            this.playerJSONObjectEditables.put(player, editable);
        } else {
            this.playerJSONObjectEditables.remove(player);
        }

    }

    @Nullable
    public JSONArray getPlayerJSONArrayEditable(@NotNull UUID player) {
        return this.playerJSONArrayEditables.get(player);
    }

    public void setPlayerJSONArrayEditable(@NotNull UUID player, @Nullable JSONArray editable) {

        if (editable != null) {
            this.playerJSONArrayEditables.put(player, editable);
        } else {
            this.playerJSONArrayEditables.remove(player);
        }

    }

    // CONSOLE

    @Nullable
    public JSONObject getConsoleJSONObjectEditable() {
        return this.consoleJSONObjectEditable;
    }

    public void setConsoleJSONObjectEditable(@Nullable JSONObject editable) {
        this.consoleJSONObjectEditable = editable;
    }

    @Nullable
    public JSONArray getConsoleJSONArrayEditable() {
        return this.consoleJSONArrayEditable;
    }

    public void setConsoleJSONArrayEditable(@Nullable JSONArray editable) {
        this.consoleJSONArrayEditable = editable;
    }

    // ----- TASK -----

    public void cleanup() {

        for (UUID playerId : Map.copyOf(this.playerJSONObjectEditables).keySet()) {
            Player player = this.getPlugin().getServer().getPlayer(playerId);
            if (player != null) continue;
            this.playerJSONObjectEditables.remove(playerId);
        }

        for (UUID playerId : Map.copyOf(this.playerJSONArrayEditables).keySet()) {
            Player player = this.getPlugin().getServer().getPlayer(playerId);
            if (player != null) continue;
            this.playerJSONArrayEditables.remove(playerId);
        }

    }

}
