package net.chaossquad.mclib.json;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.json.array.JSONEditorArraySubcommand;
import net.chaossquad.mclib.json.object.JSONEditorObjectSubcommand;
import net.chaossquad.mclib.json.utility.JSONEditorCleanupSubcommand;
import org.bukkit.command.Command;
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

/**
 * Command that allows editing JSON Objects and JSON Arrays.
 */
public final class JSONEditorCommand extends SubcommandCommand {

    @NotNull private final Map<UUID, JSONObject> playerJSONObjectEditables;
    @NotNull private final Map<UUID, JSONArray> playerJSONArrayEditables;
    @Nullable private JSONObject consoleJSONObjectEditable;
    @Nullable private JSONArray consoleJSONArrayEditable;

    /**
     * Creates the JSONEditorCommand.
     * @param plugin plugin
     * @param permission permission
     */
    public JSONEditorCommand(@NotNull Plugin plugin, @Nullable String permission) {
        super(plugin, permission);

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

    /**
     * Returns the JSONObject the specified sender is currently editing.
     * @param sender sender
     * @return JSONObject editable
     */
    @Nullable
    public JSONObject getJSONObjectEditable(@NotNull CommandSender sender) {

        if (sender instanceof Player player) {
            return this.getPlayerJSONObjectEditable(player.getUniqueId());
        } else if (sender instanceof ConsoleCommandSender console) {
            return this.getConsoleJSONObjectEditable();
        } else {
            return null;
        }

    }

    /**
     * Returns the JSONArray the specified sender is currently editing.
     * @param sender sender
     * @return JSONArray editable
     */
    @Nullable
    public JSONArray getJSONArrayEditable(@NotNull CommandSender sender) {

        if (sender instanceof Player player) {
            return this.getPlayerJSONArrayEditable(player.getUniqueId());
        } else if (sender instanceof ConsoleCommandSender console) {
            return this.getConsoleJSONArrayEditable();
        } else {
            return null;
        }

    }

    /**
     * Sets the JSONObject editable for the specified sender.
     * @param sender sender
     * @param editable editable
     */
    public void setJSONObjectEditable(@NotNull CommandSender sender, @Nullable JSONObject editable) {

        if (sender instanceof Player player) {
            this.setPlayerJSONObjectEditable(player.getUniqueId(), editable);
        } else if (sender instanceof ConsoleCommandSender console) {
            this.setConsoleJSONObjectEditable(editable);
        } else {
            return;
        }

    }

    /**
     * Sets the JSONArray editable for the specified sender.
     * @param sender sender
     * @param editable editable
     */
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

    /**
     * Returns the JSONObject editable for the specified player.
     * @param player player
     * @return JSONObject editable
     */
    @Nullable
    public JSONObject getPlayerJSONObjectEditable(@NotNull UUID player) {
        return this.playerJSONObjectEditables.get(player);
    }

    /**
     * Sets the JSONObject Editable for the specified player.
     * @param player player
     * @param editable editable
     */
    public void setPlayerJSONObjectEditable(@NotNull UUID player, @Nullable JSONObject editable) {

        if (editable != null) {
            this.playerJSONObjectEditables.put(player, editable);
        } else {
            this.playerJSONObjectEditables.remove(player);
        }

    }

    /**
     * Returns the JSONArray editable for the specified player.
     * @param player player
     * @return editable
     */
    @Nullable
    public JSONArray getPlayerJSONArrayEditable(@NotNull UUID player) {
        return this.playerJSONArrayEditables.get(player);
    }

    /**
     * Sets the JSONArray editable for the specified player.
     * @param player player
     * @param editable editable
     */
    public void setPlayerJSONArrayEditable(@NotNull UUID player, @Nullable JSONArray editable) {

        if (editable != null) {
            this.playerJSONArrayEditables.put(player, editable);
        } else {
            this.playerJSONArrayEditables.remove(player);
        }

    }

    // CONSOLE

    /**
     * Returns the JSONObject editable of the console.
     * @return JSONObject editable
     */
    @Nullable
    public JSONObject getConsoleJSONObjectEditable() {
        return this.consoleJSONObjectEditable;
    }

    /**
     * Sets the JSONObject editable for the console.
     * @param editable editable
     */
    public void setConsoleJSONObjectEditable(@Nullable JSONObject editable) {
        this.consoleJSONObjectEditable = editable;
    }

    /**
     * Returns the JSONArray editable of the console.
     * @return editable
     */
    @Nullable
    public JSONArray getConsoleJSONArrayEditable() {
        return this.consoleJSONArrayEditable;
    }

    /**
     * Returns the JSONArray editable of the console.
     * @param editable editable
     */
    public void setConsoleJSONArrayEditable(@Nullable JSONArray editable) {
        this.consoleJSONArrayEditable = editable;
    }

    // ----- NO SUBCOMMANDS -----

    @Override
    protected void onExecutionWithoutSubcommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label) {
        sender.sendMessage("§cUsage: " + label + " (cleanup|object [...]|array [...])");
    }


    // ----- TASK -----

    /**
     * Removes the JSONObject and JSONArray editables of all players.
     */
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
