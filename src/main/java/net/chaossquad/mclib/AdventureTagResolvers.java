package net.chaossquad.mclib;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class AdventureTagResolvers {

    private AdventureTagResolvers() {}

    /**
     * Returns a TagResolver to resolve LuckPerms prefix, suffix and primary group.
     * @param tag tag name (e.g. luckperms)
     * @param player player to resolve the value for
     * @param allowLegacyFormat allows parsing legacy color values, but can cause issues
     * @return TagResolver
     */
    public static @NotNull TagResolver luckPermsPlayer(final @NotNull String tag, final @NotNull Player player, final boolean allowLegacyFormat) {
        return template(tag, player, (p, str) -> {
            LuckPerms luckPerms = LuckPermsProvider.get();

            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) return "";

            CachedMetaData metaData = user.getCachedData().getMetaData();

            switch (str) {
                case "prefix" -> {
                    return Objects.requireNonNullElse(metaData.getPrefix(), "");
                }
                case "suffix" -> {
                    return Objects.requireNonNullElse(metaData.getSuffix(), "");
                }
                case "group" -> {
                    return Objects.requireNonNullElse(metaData.getPrimaryGroup(), "");
                }
                default -> {
                    return "";
                }
            }

        }, allowLegacyFormat);
    }

    /**
     * Returns a TagResolver to resolve PlaceholderAPI tags.<br/>
     * Usage in MiniMessage: TAG: [placeholder]
     * @param tag tag name (e.g. papi)
     * @param player player to resolve the placeholders for
     * @param allowLegacyFormat allows parsing legacy color values, but can cause issues
     * @return TagResolver
     */
    public static @NotNull TagResolver placeholderAPI(final @NotNull String tag, final @NotNull Player player, boolean allowLegacyFormat) {
        return template(tag, player, (p, str) -> PlaceholderAPI.setPlaceholders(p, '%' + str + '%'), allowLegacyFormat);
    }

    /**
     * Returns a TagResolver to resolve Vault prefix, suffix and primary group.
     * @param tag tag name (e.g. vault)
     * @param player player to resolve the value for
     * @param allowLegacyFormat allows parsing legacy color values, but can cause issues
     * @return TagResolver
     */
    public static @NotNull TagResolver vault(final @NotNull String tag, final @NotNull Player player, final boolean allowLegacyFormat) {
        return template(tag, player, (p, str) -> {
            RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
            if (rsp == null) return "";
            Chat chat = rsp.getProvider();

            switch (str) {
                case "prefix" -> {
                    return Objects.requireNonNullElse(chat.getPlayerPrefix(player), "");
                }
                case "suffix" -> {
                    return Objects.requireNonNullElse(chat.getPlayerSuffix(player), "");
                }
                case "group" -> {
                    return Objects.requireNonNullElse(chat.getPrimaryGroup(player), "");
                }
                default -> {
                    return "";
                }
            }

        }, allowLegacyFormat);
    }

    public static @NotNull TagResolver template(final @NotNull String tag, final @NotNull Player player, TemplateParserProvider parser, final boolean allowLegacyFormat) {
        return TagResolver.resolver(tag, (argumentQueue, context) -> {
            final String papiPlaceholder = argumentQueue.popOr(tag + " tag requires an argument").value();

            String parsedPlaceholder = parser.parse(player, papiPlaceholder);

            if (allowLegacyFormat) {
                parsedPlaceholder = ChatCompatibilityUtils.convertLegacyChatToMiniMessage(parsedPlaceholder, '&');
            }

            final Component componentPlaceholder = MiniMessage.miniMessage().deserialize(parsedPlaceholder);

            return Tag.inserting(componentPlaceholder);
        });
    }

    public interface TemplateParserProvider {
        @NotNull String parse(final @NotNull Player player, final @NotNull String text);
    }

}
