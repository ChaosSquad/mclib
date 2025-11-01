package net.chaossquad.mclib.combattracking;

import java.util.*;

/**
 * Represents a fight against one player.
 * You can use {@link PlayerFight#getStats()} to get the damaged caused by all players against the player that was damaged.
 * You can use {@link PlayerFight#getKiller(PlayerFight)} to get the player with the most damage, and {@link PlayerFight#getAssistants(PlayerFight, UUID)} to get all players that dealt damage and are not the killer.
 */
public class PlayerFight {
    private final Map<UUID, Double> damagers;
    private int expires;

    /**
     * Creates a player fight.
     */
    public PlayerFight() {
        this.damagers = Collections.synchronizedMap(new HashMap<>());
        this.expires = 30;
    }

    /**
     * Adds damage to the fight.
     * @param damager the damager that has dealt the damage
     * @param damage the damage that has been dealt
     * @return the total damage dealt by the damager
     */
    public double addDamage(UUID damager, double damage) {
        this.expires = 30;

        Double playerDamage = this.damagers.get(damager);

        if (playerDamage == null) {
            playerDamage = 0.0;
        }

        playerDamage = playerDamage + damage;
        this.damagers.put(damager, playerDamage);

        return playerDamage;
    }

    /**
     * The task. Has to be called by {@link CombatTracker#task()} to update the values here.
     */
    public void task() {

        if (this.expires > 0) {
            this.expires--;
        }

    }

    /**
     * Returns true when the fight has expired.
     * @return expired
     */
    public boolean hasExpired() {
        return this.expires <= 0;
    }

    /**
     * Returns a map of all damagers with their dealt damage.
     * @return damager-damage-map
     */
    public Map<UUID, Double> getStats() {
        return Map.copyOf(this.damagers);
    }

    /**
     * Returns the damage dealt by all players in total.
     * @return global dealt damage
     */
    public double getTotalDamage() {
        double totalDamage = 0;

        for (UUID playerId : Map.copyOf(this.damagers).keySet()) {
            Double damage = this.damagers.get(playerId);

            if (playerId == null) {
                continue;
            }

            totalDamage = totalDamage + damage;

        }

        return totalDamage;
    }

    /**
     * Returns the killer.<br/>
     * The killer is the player with the most damage.
     * @param fight fight
     * @return killer
     */
    public static UUID getKiller(PlayerFight fight) {

        if (fight == null) {
            return null;
        }

        List<Map.Entry<UUID, Double>> entries = new ArrayList<>(fight.getStats().entrySet());

        if (entries.isEmpty()) {
            return null;
        }

        entries.sort(Map.Entry.comparingByValue());

        return entries.get(entries.size() - 1).getKey();
    }

    /**
     * Returns a list of all kill assistants.
     * @param fight the fight
     * @param without damager to exclude
     * @return kill assistant list
     */
    public static List<UUID> getAssistants(PlayerFight fight, UUID without) {

        if (fight == null) {
            return List.of();
        }

        Map<UUID, Double> stats = new HashMap<>(fight.getStats());

        if (stats.isEmpty()) {
            return List.of();
        }

        stats.remove(getKiller(fight));

        if (without != null) {
            stats.remove(without);
        }

        return List.copyOf(stats.keySet());
    }
}
