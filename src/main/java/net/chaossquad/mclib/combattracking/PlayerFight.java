package net.chaossquad.mclib.combattracking;

import java.util.*;

public class PlayerFight {
    private final Map<UUID, Double> damagers;
    private int expires;

    public PlayerFight() {
        this.damagers = Collections.synchronizedMap(new HashMap<>());
        this.expires = 30;
    }

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

    public void task() {

        if (this.expires > 0) {
            this.expires--;
        }

    }

    public boolean hasExpired() {
        return this.expires <= 0;
    }

    public Map<UUID, Double> getStats() {
        return Map.copyOf(this.damagers);
    }

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
