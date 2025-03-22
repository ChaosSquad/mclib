package net.chaossquad.mclib.dynamicevents;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An ExtendedListenerOwnerSource is a {@link ListenerOwnerSource} with the difference that it can provide multiple listener owners.
 */
public interface ExtendedListenerOwnerSource {

    /**
     * Returns the listener owners.
     * @return list of listener owners
     */
    @NotNull List<@NotNull ListenerOwner> get();

}
