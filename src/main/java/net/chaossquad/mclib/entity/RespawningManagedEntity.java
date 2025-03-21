package net.chaossquad.mclib.entity;

import net.chaossquad.mclib.WorldUtils;
import net.chaossquad.mclib.misc.ListenerRegistrar;
import net.chaossquad.mclib.scheduler.SchedulerInterface;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A managed entity that respawns every time it is killed.
 * @param <ENTITY_TYPE> entity type
 */
public class RespawningManagedEntity<ENTITY_TYPE extends Entity> extends ManagedEntity<ENTITY_TYPE> {
    @NotNull private final World world;
    @NotNull private final Location location;
    @NotNull private final EntityCreator<ENTITY_TYPE> creator;
    private boolean enabled;

    public RespawningManagedEntity(@NotNull World world, @NotNull SchedulerInterface scheduler, @NotNull ListenerRegistrar listenerRegistrar, @NotNull Location location, @NotNull EntityCreator<ENTITY_TYPE> entityCreator, boolean enabled) {
        super(scheduler, listenerRegistrar);
        this.world = world;
        this.scheduler = scheduler;
        this.location = new Location(this.world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.creator = entityCreator;

        this.enabled = enabled;

        this.scheduleRepeatingTask(this::tick, 1, 200, "gui_npc_tick");
    }

    /**
     * Handles entity instance.<br/>
     * If the entity is enabled, it is spawned if it does not exist.<br/>
     * If the entity is disabled, it will be killed if it is existing.<br/>
     * This is called every 200 ticks, but it can be called manually by subclasses if necessary.
     */
    protected final void tick() {
        if (this.toBeRemoved()) return;

        // Remove entity if disabled
        if (!this.enabled) {

            if (this.getEntity() != null && !this.getEntity().isDead()) {
                this.getEntity().remove();
            }

            return;
        }

        // Spawn entity if not disabled
        if (this.getEntity() == null || this.getEntity().isDead()) {
            if (!WorldUtils.isChunkLoaded(this.location.clone())) return;

            this.setEntity(this.creator.create());
            this.getEntity().teleport(this.location.clone());

            return;
        }

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @NotNull
    public final World getWorld() {
        return this.world;
    }

}
