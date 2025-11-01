package net.chaossquad.mclib;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * Contains utilities for items.
 */
public final class ItemUtils {

    /**
     * A component that. when used as the parent component of an item lore line, clears the default color and italic of the lore.
     */
    public static final Component CLEARED_LORE_COMPONENT = Component.empty()
            .color(NamedTextColor.WHITE)
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);

    private static final Map<Integer, TrimMaterial> TEXT_COLOR_TRIM_MATERIALS = Map.ofEntries(
            Map.entry(NamedTextColor.RED.value(), TrimMaterial.REDSTONE),
            Map.entry(NamedTextColor.DARK_RED.value(), TrimMaterial.REDSTONE),
            Map.entry(NamedTextColor.YELLOW.value(), TrimMaterial.GOLD),
            Map.entry(NamedTextColor.GOLD.value(), TrimMaterial.GOLD),
            Map.entry(NamedTextColor.GREEN.value(), TrimMaterial.EMERALD),
            Map.entry(NamedTextColor.DARK_GREEN.value(), TrimMaterial.EMERALD),
            Map.entry(NamedTextColor.AQUA.value(), TrimMaterial.DIAMOND),
            Map.entry(NamedTextColor.DARK_AQUA.value(), TrimMaterial.DIAMOND),
            Map.entry(NamedTextColor.BLUE.value(), TrimMaterial.LAPIS),
            Map.entry(NamedTextColor.DARK_BLUE.value(), TrimMaterial.LAPIS),
            Map.entry(NamedTextColor.DARK_PURPLE.value(), TrimMaterial.AMETHYST),
            Map.entry(NamedTextColor.LIGHT_PURPLE.value(), TrimMaterial.AMETHYST),
            Map.entry(NamedTextColor.WHITE.value(), TrimMaterial.QUARTZ),
            Map.entry(NamedTextColor.DARK_GRAY.value(), TrimMaterial.NETHERITE),
            Map.entry(NamedTextColor.BLACK.value(), TrimMaterial.NETHERITE),
            Map.entry(NamedTextColor.GRAY.value(), TrimMaterial.IRON)
    );

    private ItemUtils() {}

    /**
     * Returns the {@link TrimMaterial} for the specified {@link NamedTextColor}.
     * @param color text color
     * @return trim material
     */
    @Nullable
    public static TrimMaterial translateTextColorToTrimMaterial(@NotNull NamedTextColor color) {
        return TEXT_COLOR_TRIM_MATERIALS.getOrDefault(color.value(), null);
    }

    /**
     * Set a base64 encoded custom head to a {@link SkullMeta}.
     * @param skullMeta the {@link SkullMeta} the head should be set for
     * @param customHead base64-encoded player head
     */
    public static void setCustomHeadForSkullMeta(@NotNull SkullMeta skullMeta, @NotNull String customHead) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
        profile.setProperty(new ProfileProperty("textures", customHead));
        skullMeta.setPlayerProfile(profile);
    }

}
