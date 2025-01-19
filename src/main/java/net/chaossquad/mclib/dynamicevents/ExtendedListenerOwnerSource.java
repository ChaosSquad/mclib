package net.chaossquad.mclib.dynamicevents;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ExtendedListenerOwnerSource {
    @NotNull List<@NotNull ListenerOwner> get();
}
