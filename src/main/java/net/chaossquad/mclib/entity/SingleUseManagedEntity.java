package net.chaossquad.mclib.entity;

import net.chaossquad.mclib.misc.ListenerRegistrar;
import net.chaossquad.mclib.scheduler.SchedulerInterface;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * A managed entity that will be removed when the entity dies.
 * @param <ENTITY_TYPE> entity type
 */
public class SingleUseManagedEntity<ENTITY_TYPE extends Entity> extends ManagedEntity<ENTITY_TYPE> {

    /**
     * Creates a new SingleUseManagedEntity.
     * @param scheduler scheduler
     * @param listenerRegistrar listener registrar
     * @param creator creator
     */
    public SingleUseManagedEntity(@NotNull SchedulerInterface scheduler, @NotNull ListenerRegistrar listenerRegistrar, @NotNull EntityCreator<ENTITY_TYPE> creator) {
        super(scheduler, listenerRegistrar);
        this.setEntity(creator.create());
        super.setRemovable(() -> this.getEntity() == null || this.getEntity().isDead());
    }

}
