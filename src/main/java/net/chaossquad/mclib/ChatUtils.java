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

}
