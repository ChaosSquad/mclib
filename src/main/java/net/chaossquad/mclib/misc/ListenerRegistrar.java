package net.chaossquad.mclib.misc;

import net.chaossquad.mclib.executable.ManagedListener;
import org.jetbrains.annotations.NotNull;

/**
 * Provides a method to register a {@link ManagedListener}.
 */
public interface ListenerRegistrar {
    void registerListener(@NotNull ManagedListener listener);
}
