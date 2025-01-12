package net.chaossquad.mclib;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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

}
