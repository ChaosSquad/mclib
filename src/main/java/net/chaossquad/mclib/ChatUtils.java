package net.chaossquad.mclib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.trim.TrimMaterial;

import java.util.UUID;

/**
 * Utilities for the chat.
 */
public final class ChatUtils {

    private ChatUtils() {}

    // CHAT COLORS

    /**
     * Translates chat color into color
     * @param chatColor chat color
     * @return color
     */
    public static Color translateChatColorToColor(ChatColor chatColor) {

        return switch (chatColor) {
            case AQUA -> Color.AQUA;
            case BLACK -> Color.BLACK;
            case BLUE, DARK_AQUA, DARK_BLUE -> Color.BLUE;
            case DARK_GRAY, GRAY -> Color.GRAY;
            case GREEN -> Color.LIME;
            case DARK_GREEN -> Color.GREEN;
            case DARK_PURPLE, LIGHT_PURPLE -> Color.PURPLE;
            case DARK_RED, RED -> Color.RED;
            case GOLD, YELLOW -> Color.YELLOW;
            case WHITE -> Color.WHITE;
            default -> null;
        };

    }

    /**
     * Translates chat color into trim material
     * @param chatColor chat color
     * @return trim material
     */
    public static TrimMaterial translateChatColorToTrimMaterial(ChatColor chatColor) {

        return switch (chatColor) {
            case RED, DARK_RED -> TrimMaterial.REDSTONE;
            case GREEN, DARK_GREEN -> TrimMaterial.EMERALD;
            case BLUE, DARK_BLUE -> TrimMaterial.LAPIS;
            case AQUA, DARK_AQUA -> TrimMaterial.DIAMOND;
            case WHITE, GRAY -> TrimMaterial.IRON;
            case BLACK, DARK_GRAY -> TrimMaterial.NETHERITE;
            case YELLOW, GOLD -> TrimMaterial.GOLD;
            default -> null;
        };

    }

    // PLAYER STRINGS

    /**
     * Returns a string of the specified chat color.
     * @param color ChatColor
     * @return color string
     */
    public static String getBlockColorString(ChatColor color) {

        return switch (color) {
            case BLACK -> "BLACK";
            case DARK_BLUE -> "BLUE";
            case DARK_GREEN -> "GREEN";
            case DARK_AQUA -> "CYAN";
            case DARK_RED -> "RED";
            case DARK_PURPLE -> "PURPLE";
            case GOLD -> "ORANGE";
            case GRAY -> "LIGHT_GRAY";
            case DARK_GRAY -> "GRAY";
            case BLUE -> "LIGHT_BLUE";
            case GREEN -> "LIME";
            case AQUA -> "CYAN";
            case RED -> "RED";
            case LIGHT_PURPLE -> "MAGENTA";
            case YELLOW -> "YELLOW";
            case WHITE -> "WHITE";
            default -> null;
        };

    }

    /**
     * Returns the UUID of a player if the string is a valid UUID or an offline player with that UUID exists.
     * @param playerString UUID or player name
     * @return uuid if player exists or uuid valid, null if uuid invalid and player not found
     */
    public static UUID getPlayerUUIDFromString(String playerString) {

        UUID playerId = null;

        try {
            playerId = UUID.fromString(playerString);
        } catch (IllegalArgumentException e) {
            OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerString);
            if (player != null) {
                playerId = player.getUniqueId();
            }
        }

        return playerId;
    }

    /**
     * Returns a Player instance if the string is a valid uuid or player name of an online player.
     * Else, it returns null.
     * @param playerString UUID or player name
     * @return Bukkit player
     */
    public static Player getPlayerFromString(String playerString) {

        try {
            return Bukkit.getServer().getPlayer(UUID.fromString(playerString));
        } catch (IllegalArgumentException e) {
            return Bukkit.getServer().getPlayer(playerString);
        }

    }

}
