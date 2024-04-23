package net.chaossquad.mclib.packetentity;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Represents a packet entity.
 * Contains the NMS entity, the players that can see it and custom data for that entity.
 * @param <T> entity type if relevant
 */
public class PacketEntity<T extends Entity> implements EntityInLevelCallback {
    private final PacketEntityManager manager;
    private final T entity;
    private final List<Player> players;
    private final Map<String, PacketEntityData<?>> data;

    public PacketEntity(PacketEntityManager manager, T entity) {
        this.manager = manager;
        this.entity = entity;
        this.entity.setLevelCallback(this);
        this.players = new ArrayList<>();
        this.data = new HashMap<>();
    }

    // ENTITY

    /**
     * Returns the entity.
     * @return
     */
    public T getEntity() {
        return this.entity;
    }

    /**
     * Checks if the entity is removed.
     * @return
     */
    public boolean isRemoved() {
        return this.entity.isRemoved();
    }

    /**
     * Set the entity removed and removes all players.
     */
    public void remove() {
        this.entity.setRemoved(Entity.RemovalReason.DISCARDED);
        this.removeAllPlayers();
    }

    // PLAYERS

    /**
     * Returns the condition if the entity should be showed to a player.
     * This does not check if the player is in the player list of that entity.
     * @param player player
     * @return true if entity can be shown to a player if the player is in the list for the entity
     */
    private boolean showEntityCondition(Player player) {
        return player != null && player.isOnline() && ((CraftWorld) player.getWorld()).getHandle() != this.entity.level();
    }

    /**
     * Removes all players from the packet entity.
     */
    public void cleanupPlayers() {

        for (Player player : List.copyOf(this.players)) {

            if (!this.showEntityCondition(player)) {
                this.players.remove(player);
            }

        }

    }

    /**
     * Returns a list of all players that can see the entity.
     * This does not include players in the list that don't meet the conditions for seeing the entity
     * @return list of players
     */
    public List<Player> getPlayers() {
        List<Player> playerList = new ArrayList<>();

        for (Player player : List.copyOf(this.players)) {
            if (this.showEntityCondition(player)) {
                playerList.add(player);
            }
        }

        return List.copyOf(playerList);
    }

    /**
     * Adds a player so that the player can see the entity.
     * @param player the player
     * @return true if adding the player was successful
     */
    public boolean addPlayer(Player player) {
        if (this.entity.isRemoved()) return false;
        if (!this.showEntityCondition(player)) return false;

        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(this.entity);
        ((CraftPlayer) player).getHandle().connection.send(packet);

        this.updateEntity();

        this.players.add(player);
        return true;
    }

    /**
     * Removes a player from seeing the entity.
     * @param player player that should be removed
     */
    public void removePlayer(Player player) {

        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(this.entity.getId());
        ((CraftPlayer) player).getHandle().connection.send(packet);

        this.players.remove(player);
    }

    /**
     * Removes all players from seeing the entity.
     */
    public void removeAllPlayers() {

        for (Player player : List.copyOf(this.players)) {
            this.removePlayer(player);
        }

    }

    // ENTITY DATA

    /**
     * Sends all entity data modifications to the players.
     */
    public void updateEntity() {

        List<SynchedEntityData.DataValue<?>> data = this.entity.getEntityData().packDirty();
        if (data == null) return;

        for (Player player : this.getPlayers()) {
            if (!this.showEntityCondition(player)) continue;

            ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(this.entity.getId(), data);
            ((CraftPlayer) player).getHandle().connection.send(packet);

        }

    }

    // CUSTOM DATA

    /**
     * Returns all custom entity data.
     * @return map of custom entity data
     */
    public Map<String, PacketEntityData<?>> getData() {
        return Map.copyOf(this.data);
    }

    /**
     * Add or update a custom entity data.
     * @param key key to add
     * @param data value to add
     */
    public void addData(String key, PacketEntityData<?> data) {
        this.data.put(key, data);
    }

    /**
     * Removes a custom entity data.
     * @param key key to remove
     */
    public void removeData(String key) {
        this.data.remove(key);
    }

    /**
     * Clears all custom entity data.
     */
    public void removeAllData() {
        this.data.clear();
    }

    // GETTER

    public PacketEntityManager getManager() {
        return manager;
    }

    // LEVEL CALLBACK

    @Override
    public void onMove() {
        this.updateEntity();
    }

    @Override
    public void onRemove(Entity.RemovalReason removalReason) {
        this.manager.cleanupEntities();
    }

}
