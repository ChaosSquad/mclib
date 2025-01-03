package net.chaossquad.mclib.actionbar;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Solves the problem of handling multiple actionbars at once.<br/>
 * With {@link Player#sendActionBar(Component)}, one actionbar would remove another actionbar.<br/>
 * With the ActionBarManager, it is possible to display multiple actionbars at once.
 */
public abstract class ActionBarManager {
    private final Plugin plugin;
    private final Map<Player, Map<String, ActionBarText>> texts;
    private final Component splitter;
    private long tick;

    // CONSTRUCTOR

    public ActionBarManager(@NotNull Plugin plugin, @NotNull Component splitter) {
        this.plugin = plugin;
        this.texts = new HashMap<>();
        this.splitter = splitter;
        this.tick = 0;
    }

    // UTILITIES

    /**
     * Handles player.
     * Used by run.
     * @param player player that should be handled
     */
    protected void handlePlayer(Player player) {

        // Return if the player is not online or has an empty text map

        if (!player.isOnline()) return;

        Map<String, ActionBarText> playerTexts = this.texts.get(player);
        if (playerTexts == null || playerTexts.isEmpty()) return;

        // Modified variable

        boolean hasRemovedContents = false;

        // Create component list and add empty component to prevent the first component giving the format for all components

        Component component = Component.empty();

        // Manage components

        Iterator<String> iterator = List.copyOf(playerTexts.keySet()).iterator();
        while (iterator.hasNext()) {

            // Get text id and text

            String id = iterator.next();
            ActionBarText text = playerTexts.get(id);

            // Add text when it is available and not null or remove it when these conditions are not met

            if (text != null && this.tick <= text.removeAt()) {

                // Add components
                component = component.append(text.content());

                // Add splitter when it is required
                if (iterator.hasNext()) {
                    component = component.append(this.splitter);
                }

            } else {
                playerTexts.remove(id);
                hasRemovedContents = true;
            }

        }

        // Sends the action bar to the player when there are components available or when there were components removed (to clear the actionbar)

        if (!component.children().isEmpty() || hasRemovedContents) {
            player.sendActionBar(component);
        }

    }

    // TASKS

    /**
     * Runs everything.
     * Used by the task.
     */
    protected void mainTask() {

        if (this.texts.isEmpty()) return;

        for (Player player : List.copyOf(this.texts.keySet())) {

            // Remove when player is not online

            if (!player.isOnline()) {
                this.texts.remove(player);
                continue;
            }

            // Remove when empty

            Map<String, ActionBarText> playerTexts = this.texts.get(player);
            if (playerTexts == null || playerTexts.isEmpty()) {
                this.texts.remove(player);
                continue;
            }

            // Handle player

            this.handlePlayer(player);

        }

        if (this.tick < Long.MAX_VALUE) {
            this.tick++;
        } else {
            this.tick = 0;
            this.texts.clear();
        }

    }

    /**
     * Returns the player components map.
     * @param player player
     * @return text map
     */
    protected Map<String, ActionBarText> getPlayerTextMap(Player player) {
        if (player == null) return null;
        return this.texts.computeIfAbsent(player, k -> new HashMap<>());
    }

    // MANAGE COMPONENTS

    /**
     * Returns a list of all players that have a registered actionbar.
     * @return list of players with registered actionbar
     */
    @SuppressWarnings("unused")
    public List<Player> getRegisteredPlayers() {
        return List.copyOf(this.texts.keySet());
    }

    /**
     * Returns a map the actionbar texts of the specified player
     * @param player player
     * @return actionbars of specified player
     */
    @SuppressWarnings("unused")
    public Map<String, ActionBarText> getPlayerActionBar(Player player) {
        return Map.copyOf(this.getPlayerTextMap(player));
    }

    /**
     * Adds an actionbar text for the specified player
     * @param player player
     * @param id actionbar id
     * @param component chat component to send
     * @param duration how long the action bar should be displayed
     */
    @SuppressWarnings("unused")
    public void sendActionBarMessage(Player player, String id, int duration, Component component) {
        if (player == null || !player.isOnline() || id == null || component == null) return;
        this.getPlayerTextMap(player).put(id, new ActionBarText(component, this.tick + 1 + duration));
    }

    /**
     * Removes an actionbar text specified by id
     * @param player player
     * @param id actionbar text id
     */
    @SuppressWarnings("unused")
    public void removeActionBarMessage(Player player, String id) {
        if (player == null || id == null) return;
        this.getPlayerTextMap(player).remove(id);
    }

    /**
     * Clears all actionbar texts of the specified player.
     * @param player player
     */
    @SuppressWarnings("unused")
    public void clearActionBarMessages(Player player) {
        this.texts.remove(player);
    }

    // GETTER

    public Plugin getPlugin() {
        return this.plugin;
    }

    @SuppressWarnings("unused")
    public long getTick() {
        return this.tick;
    }

}
