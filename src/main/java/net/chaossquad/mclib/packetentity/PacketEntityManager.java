package net.chaossquad.mclib.packetentity;

import net.minecraft.world.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PacketEntityManager implements Listener {
    private final Plugin plugin;
    private final List<PacketEntity<?>> entities;

    public PacketEntityManager(Plugin plugin) {
        this.plugin = plugin;
        entities = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                cleanupEntities();
                cleanupPlayers();
                updateEntities();
            }
        }.runTaskTimer(this.plugin, 1, 200);
    }

    // TASKS

    private void cleanupEntities() {

        for (PacketEntity<?> entity : List.copyOf(this.entities)) {

            if (entity.isRemoved()) {
                entity.removeAllPlayers();
                this.entities.remove(entity);
            }

        }

    }

    private void cleanupPlayers() {

        for (PacketEntity<?> entity : List.copyOf(this.entities)) {
            if (entity.isRemoved()) continue;
            entity.cleanupPlayers();
        }

    }

    private void updateEntities() {

        for (PacketEntity<?> entity : List.copyOf(this.entities)) {
            if (entity.isRemoved()) continue;

            if (entity.getEntity().getEntityData().isDirty()) {
                entity.updateEntity();
            }

        }

    }

    // EVENTS

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.cleanupPlayers();
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        this.cleanupPlayers();
    }

    // API

    /**
     * Returns the plugin.
     * @return plugin
     */
    public Plugin getPlugin() {
        return this.plugin;
    }

    /**
     * Returns a list of all available packet entities.
     * @return list of packet entities
     */
    public List<PacketEntity<?>> getEntities() {
        List<PacketEntity<?>> list = new ArrayList<>();

        for (PacketEntity<?> entity : List.copyOf(this.entities)) {
            if (entity.isRemoved()) continue;
            list.add(entity);
        }

        return list;
    }

    /**
     * Removes all packet entities.
     */
    public void removeAll() {

        for (PacketEntity<?> entity : List.copyOf(this.entities)) {
            entity.remove();
        }

        this.cleanupEntities();

    }

    /**
     * Searches for the {@link PacketEntity} that is managing the specified entity.
     * @param entity nms entity
     * @return PacketEntity
     */
    public PacketEntity<?> getPacketEntityFromEntity(Entity entity) {
        for (PacketEntity<?> packetEntity : this.entities) {
            if (packetEntity.getEntity().equals(entity)) return packetEntity;
        }
        return null;
    }

    /**
     * Adds a new packet entity.
     * @param entity nms entity
     * @return packet entity object
     */
    public PacketEntity<?> addEntity(Entity entity) {
        PacketEntity<?> packetEntity = new PacketEntity<>(entity);
        this.entities.add(packetEntity);
        return packetEntity;
    }

}
