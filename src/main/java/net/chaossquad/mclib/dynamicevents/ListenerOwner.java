package net.chaossquad.mclib.dynamicevents;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A listener owner provides a list of listeners.
 */
public interface ListenerOwner {

    /**
     * Returns a list containing all listeners of this executable.
     * @return list of listeners
     */
    @NotNull
    List<Listener> getListeners();

}
