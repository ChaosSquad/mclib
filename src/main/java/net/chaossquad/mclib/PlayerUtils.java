package net.chaossquad.mclib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerUtils {

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

    /**
     * Get the player prefix.
     * @param player player
     * @return prefix
     */
    @NotNull
    public static Component getPlayerPrefix(@NotNull Player player, boolean allowLegacyColors) {

        if (Bukkit.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            LuckPerms luckPerms = LuckPermsProvider.get();

            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) return Component.empty();

            CachedMetaData metaData = user.getCachedData().getMetaData();
            String prefix = metaData.getPrefix();
            if (prefix == null) return Component.empty();

            if (allowLegacyColors) prefix = ChatColor.translateAlternateColorCodes('&', prefix);

            return MiniMessage.miniMessage().deserialize(prefix);
        }

        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {

            RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
            if (rsp == null) return Component.empty();
            Chat chat = rsp.getProvider();

            return MiniMessage.miniMessage().deserialize(chat.getPlayerPrefix(player));
        }

        return Component.empty();
    }

    /**
     * Get the player suffix.
     * @param player player
     * @return suffix
     */
    @NotNull
    public static Component getPlayerSuffix(@NotNull Player player, boolean allowLegacyColors) {

        if (Bukkit.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            LuckPerms luckPerms = LuckPermsProvider.get();

            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) return Component.empty();

            CachedMetaData metaData = user.getCachedData().getMetaData();
            String prefix = metaData.getSuffix();
            if (prefix == null) return Component.empty();

            if (allowLegacyColors) prefix = ChatColor.translateAlternateColorCodes('&', prefix);

            return MiniMessage.miniMessage().deserialize(prefix);
        }

        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {

            RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
            if (rsp == null) return Component.empty();
            Chat chat = rsp.getProvider();

            return MiniMessage.miniMessage().deserialize(chat.getPlayerSuffix(player));
        }

        return Component.empty();
    }

}
