package net.chaossquad.mclib;

import org.bukkit.ChatColor;

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
