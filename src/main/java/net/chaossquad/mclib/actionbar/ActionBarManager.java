package net.chaossquad.mclib.actionbar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Solves the problem of handling multiple actionbars at once.
 * With {@link org.bukkit.entity.Player.Spigot#sendMessage(ChatMessageType, BaseComponent...)}, one actionbar would remove another actionbar.
 * With the ActionBarManager, it is possible to display multiple actionbars at once.
 */
public abstract class ActionBarManager {
    private final Plugin plugin;
    private final Map<Player, Map<String, ActionBarText>> texts;
    private final BaseComponent splitter;
    private long tick;

    // CONSTRUCTOR

    public ActionBarManager(Plugin plugin, BaseComponent splitter) {
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

        List<BaseComponent> components = new ArrayList<>();
        components.add(new TextComponent(" "));

        // Manage components

        Iterator<String> iterator = List.copyOf(playerTexts.keySet()).iterator();
        while (iterator.hasNext()) {

            // Get text id and text

            String id = iterator.next();
            ActionBarText text = playerTexts.get(id);

            // Add text when it is available and not null or remove it when these conditions are not met

            if (text != null && this.tick <= text.removeAt()) {

                // Add components
                components.addAll(Arrays.stream(text.content()).toList());

                // Add splitter when it is required
                if (iterator.hasNext() && this.splitter != null) {
                    components.add(this.splitter);
                }

            } else {
                playerTexts.remove(id);
                hasRemovedContents = true;
            }

        }

        // Add another empty component to keep text symmetrical

        components.add(new TextComponent(" "));

        // Sends the action bar to the player when there are components available or when there were components removed (to clear the actionbar)

        if (components.size() > 2 || hasRemovedContents) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components.toArray(new BaseComponent[0]));
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
    public List<Player> getRegisteredPlayers() {
        return List.copyOf(this.texts.keySet());
    }

    /**
     * Returns a map the actionbar texts of the specified player
     * @param player player
     * @return actionbars of specified player
     */
    public Map<String, ActionBarText> getPlayerActionBar(Player player) {
        return Map.copyOf(this.getPlayerTextMap(player));
    }

    /**
     * Adds an actionbar text for the specified player
     * @param player player
     * @param id actionbar id
     * @param components chat components of the action bar
     * @param duration how long the action bar should be displayed
     */
    public void sendActionBarMessage(Player player, String id, BaseComponent[] components, int duration) {
        if (player == null || !player.isOnline() || id == null || components == null) return;
        this.getPlayerTextMap(player).put(id, new ActionBarText(components, this.tick + 1 + duration));
    }

    /**
     * Removes an actionbar text specified by id
     * @param player player
     * @param id actionbar text id
     */
    public void removeActionBarMessage(Player player, String id) {
        if (player == null || id == null) return;
        this.getPlayerTextMap(player).remove(id);
    }

    /**
     * Clears all actionbar texts of the specified player.
     * @param player player
     */
    public void clearActionBarMessages(Player player) {
        this.texts.remove(player);
    }

    // GETTER

    public Plugin getPlugin() {
        return this.plugin;
    }

    public long getTick() {
        return this.tick;
    }

    public ActionBarManager getSelf() {
        return this;
    }

}
