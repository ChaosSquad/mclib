package net.chaossquad.mclib;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public final class ChatUtils {

    private ChatUtils() {}

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
