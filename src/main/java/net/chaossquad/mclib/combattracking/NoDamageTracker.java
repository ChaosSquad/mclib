package net.chaossquad.mclib.combattracking;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks the ticks a player has not taken damage.<br/>+
 * You need to call {@link #run()} every tick and {@link #onPlayerDamage(Player)} every time the player takes damage/dies.
 */
public class NoDamageTracker implements Runnable {
    @NotNull private final ConcurrentHashMap<UUID, Integer> players;

    /**
     * Creates a new NoDamageTracker.
     */
    public NoDamageTracker() {
        this.players = new ConcurrentHashMap<>();
    }

    // ----- TASKS/EVENTS -----

    /**
     * The task that has to be executed.
     */
    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isDead()) continue;

            int amount = this.players.getOrDefault(player.getUniqueId(), 0);
            if (amount >= Integer.MAX_VALUE - 1) continue;

            this.players.put(player.getUniqueId(), amount + 1);
        }

        this.players.keySet().removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
    }

    /**
     * The method that has to be called when the player takes damage or dies.
     * @param player the player that took damage or died.
     */
    public void onPlayerDamage(@NotNull Player player) {
        this.players.put(player.getUniqueId(), 0);
    }

    // ----- GET INFO -----

    /**
     * Returns the amount of ticks the player did not take damage.
     * @param player player
     * @return no damage ticks
     */
    public int getNoDamageTicks(@NotNull Player player) {
        if (player.isDead()) return 0;
        return this.players.getOrDefault(player.getUniqueId(), 0);
    }

    /**
     * Returns the internal map of players and their no damage ticks.
     * @return map
     */
    public @NotNull ConcurrentHashMap<UUID, Integer> getPlayers() {
        return this.players;
    }

}
