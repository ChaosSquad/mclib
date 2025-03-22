package net.chaossquad.mclib;

import org.bukkit.Bukkit;

/**
 * A class which checks for plugin dependencies.
 */
public final class IntegrationsCheck {

    private IntegrationsCheck() {}

    /**
     * Checks if LuckPerms is available.
     * @return available
     */
    public static boolean luckPerms() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) return false;

        try {
            Class.forName("net.luckperms.api.LuckPerms");
            Class.forName("net.luckperms.api.LuckPermsProvider");
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Checks if Vault is available.
     * @return available
     */
    public static boolean vault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return false;

        try {
            Class.forName("net.milkbowl.vault.chat.Chat");
            Class.forName("net.milkbowl.vault.permission.Permission");
            Class.forName("net.milkbowl.vault.economy.AbstractEconomy");

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Checks if PlaceholderAPI is available.
     * @return available
     */
    public static boolean placeholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return false;

        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
