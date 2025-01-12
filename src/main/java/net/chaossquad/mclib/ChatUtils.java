package net.chaossquad.mclib;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Utilities for the chat.
 */
public final class ChatUtils {

    private ChatUtils() {}

    // CHAT COLORS

    @Deprecated(forRemoval = true)
    public static Color translateChatColorToColor(ChatColor chatColor) {
        return ChatCompatibilityUtils.translateChatColorToColor(chatColor);
    }

    /**
     * Translates chat color into trim material
     * @param chatColor chat color
     * @deprecated Use {@link ItemUtils#translateTextColorToTrimMaterial(NamedTextColor)}
     * @return trim material
     */
    @Deprecated(forRemoval = true)
    public static TrimMaterial translateChatColorToTrimMaterial(ChatColor chatColor) {
        return ItemUtils.translateTextColorToTrimMaterial(ChatCompatibilityUtils.getTextColorFromChatColor(chatColor));
    }

    // NEW AND LEGACY COLORS

    /**
     * Converts the {@link NamedTextColor} from net.kyori.adventure to legacy {@link ChatColor}.
     * @param color original
     * @deprecated Use {@link ChatCompatibilityUtils#getChatColorFromTextColor(NamedTextColor)}
     * @return converted
     */
    @Deprecated(forRemoval = true)
    public static @Nullable ChatColor getChatColorFromTextColor(@NotNull NamedTextColor color) {
        return ChatCompatibilityUtils.getChatColorFromTextColor(color);
    }

    /**
     * Convert the legacy {@link ChatColor} to the {@link NamedTextColor} from net.kyori.adventure.
     * @param chatColor original
     * @deprecated Use {@link ChatCompatibilityUtils#getTextColorFromChatColor(ChatColor)}
     * @return converted
     */
    @Deprecated(forRemoval = true)
    public static @Nullable NamedTextColor getTextColorFromChatColor(@NotNull ChatColor chatColor) {
        return ChatCompatibilityUtils.getTextColorFromChatColor(chatColor);
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
     * @deprecated Use {@link PlayerUtils#getPlayerUUIDFromString(String)}
     * @return uuid if player exists or uuid valid, null if uuid invalid and player not found
     */
    @Deprecated(forRemoval = true)
    public static UUID getPlayerUUIDFromString(String playerString) {
        return PlayerUtils.getPlayerUUIDFromString(playerString);
    }

    /**
     * Returns a Player instance if the string is a valid uuid or player name of an online player.
     * Else, it returns null.
     * @param playerString UUID or player name
     * @deprecated Use {@link PlayerUtils#getPlayerFromString(String)}
     * @return Bukkit player
     */
    @Deprecated(forRemoval = true)
    public static Player getPlayerFromString(String playerString) {
        return PlayerUtils.getPlayerFromString(playerString);
    }

}
