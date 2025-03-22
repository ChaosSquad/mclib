package net.chaossquad.mclib.dynamicevents;

import org.jetbrains.annotations.Nullable;

/**
 * Provides a ListenerOwner.
 */
public interface ListenerOwnerSource {

    /**
     * Provides a ListenerOwner.
     * @return ListenerOwner
     */
    @Nullable ListenerOwner get();

}
