package net.chaossquad.mclib.combattracking;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    // ----- FUNCTIONALITY -----

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
    public double onPlayerDamage(@NotNull UUID player, @NotNull UUID damager, double damage) {
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
    public @Nullable PlayerFight onPlayerDeath(@NotNull UUID playerId) {
        return this.playerFights.remove(playerId);
    }

    // ----- INFORMATION -----

    /**
     * Returns the map of the currently ongoing PlayerFights.
     * @return map of player fights
     */
    public @NotNull Map<UUID, PlayerFight> getPlayerFights() {
        return this.playerFights;
    }

    /**
     * Returns a PlayerFight without removing it.
     * @param playerId player uuid
     * @return player fight or null
     */
    public @Nullable PlayerFight getPlayerFight(@NotNull UUID playerId) {
        return this.playerFights.get(playerId);
    }

    /**
     * Returns all fights the player is involved to.
     * @param playerId player id
     * @return list of fights
     */
    public @NotNull List<PlayerFight> getInvolvedFights(@NotNull UUID playerId) {
        List<PlayerFight> involvedFights = new ArrayList<>();

        for (Map.Entry<UUID, PlayerFight> entry : this.playerFights.entrySet()) {

            if (entry.getKey().equals(playerId) || entry.getValue().getStats().containsKey(playerId)) {
                involvedFights.add(entry.getValue());
            }

        }

        return involvedFights;
    }

    /**
     * Returns true if the specified player has an ongoing fight.
     * @param playerId player uuid
     * @return player in combat
     */
    public boolean isInCombat(@NotNull UUID playerId) {
        return !this.getInvolvedFights(playerId).isEmpty();
    }

}
