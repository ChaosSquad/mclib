package net.chaossquad.mclib.combattracking;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Combat tracking system.
 * You need to call {@link CombatTracker#onPlayerDamage(UUID, UUID, double)} when a player is damaged by a player and {@link CombatTracker#onPlayerDeath(UUID)} when the player died.
 * You also need to call {@link CombatTracker#task()} every second.
 * If you call {@link CombatTracker#onPlayerDeath(UUID)}, you will get the current player fight returned, which contains the information about all players involved in this fight.
 * Look at {@link PlayerFight} to see how to use it.
 */
public class CombatTracker {
    private final Map<UUID, PlayerFight> playerFights;

    /**
     * Creates a CombatTracker.
     */
    public CombatTracker() {
        this.playerFights = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * Counts down the expiration countdowns.
     * Call every second.
     */
    public void task() {

        for (UUID playerId : Map.copyOf(this.playerFights).keySet()) {
            PlayerFight fight = this.playerFights.get(playerId);

            if (fight == null) {
                continue;
            }

            fight.task();

            if (fight.hasExpired()) {
                this.playerFights.remove(playerId);
            }

        }

    }

    /**
     * Adds player damage.
     * Should be called in damage events.
     * @param player the damaged player
     * @param damager the player who dealt the damage
     * @param damage the amount of damage
     * @return the amount of damage the player who dealt the damage has dealt in total
     */
    public double onPlayerDamage(UUID player, UUID damager, double damage) {
        PlayerFight fight = this.playerFights.get(player);

        if (fight == null) {
            fight = new PlayerFight();
            this.playerFights.put(player, fight);
        }

        return fight.addDamage(damager, damage);
    }

    /**
     * Removes a combat.
     * Should be called on player death.
     * @param playerId the player who died
     * @return the player fight for using the tracked data
     */
    public PlayerFight onPlayerDeath(UUID playerId) {
        return this.playerFights.remove(playerId);
    }
}
