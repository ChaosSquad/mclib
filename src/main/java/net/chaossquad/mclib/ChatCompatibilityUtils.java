package net.chaossquad.mclib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ChatCompatibilityUtils {

    private ChatCompatibilityUtils() {}

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
     * Converts the {@link NamedTextColor} from net.kyori.adventure to legacy {@link ChatColor}.
     * @param color original
     * @return converted
     */
    public static @Nullable ChatColor getChatColorFromTextColor(@NotNull NamedTextColor color) {
        try {
            return ChatColor.valueOf(color.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convert the legacy {@link ChatColor} to the {@link NamedTextColor} from net.kyori.adventure.
     * @param chatColor original
     * @return converted
     */
    public static @Nullable NamedTextColor getTextColorFromChatColor(@NotNull ChatColor chatColor) {
        return NamedTextColor.NAMES.value(chatColor.name());
    }

    /**
     * Converts legacy color codes to mini message.
     * @param message message with legacy color codes
     * @param character legacy color code character
     * @return message in minimessage format
     */
    public static @NotNull String convertLegacyChatToMiniMessage(@NotNull String message, char character) {
        Map<String, String> replacements = Map.ofEntries(
                Map.entry(character + "0", "<reset><black>"),
                Map.entry(character + "1", "<reset><dark_blue>"),
                Map.entry(character + "2", "<reset><dark_green>"),
                Map.entry(character + "3", "<reset><dark_aqua>"),
                Map.entry(character + "4", "<reset><dark_red>"),
                Map.entry(character + "5", "<reset><dark_purple>"),
                Map.entry(character + "6", "<reset><gold>"),
                Map.entry(character + "7", "<reset><gray>"),
                Map.entry(character + "8", "<reset><dark_gray>"),
                Map.entry(character + "9", "<reset><blue>"),
                Map.entry(character + "a", "<reset><green>"),
                Map.entry(character + "b", "<reset><aqua>"),
                Map.entry(character + "c", "<reset><red>"),
                Map.entry(character + "d", "<reset><light_purple>"),
                Map.entry(character + "e", "<reset><yellow>"),
                Map.entry(character + "f", "<reset><white>"),
                Map.entry(character + "k", "<obfuscated>"),
                Map.entry(character + "l", "<bold>"),
                Map.entry(character + "m", "<strikethrough>"),
                Map.entry(character + "n", "<underline>"),
                Map.entry(character + "o", "<italic>"),
                Map.entry(character + "p", "<reset>")
        );

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }

        return message;
    }

}
