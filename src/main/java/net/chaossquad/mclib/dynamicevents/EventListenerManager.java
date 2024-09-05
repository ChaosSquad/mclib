package net.chaossquad.mclib.dynamicevents;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EventListenerManager {
    @NotNull private final Plugin plugin;
    @NotNull private final List<ListenerOwnerSource> sources;
    @NotNull private final List<Listener> exceptedListeners;

    public EventListenerManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.sources = Collections.synchronizedList(new ArrayList<>());
        this.exceptedListeners = Collections.synchronizedList(new ArrayList<>());
    }

    // ----- LISTENER MANAGEMENT -----

    /**
     * Run both the cleanup and the add task.
     */
    public void manageListeners() {
        this.cleanupListeners();
        this.addListenersOfAll();
    }

    // ----- LISTENER CLEANUP -----

    /**
     * This method cleans up all listeners which must not exist.
     * It is used for removing game and map listeners of games/maps which are not loaded anymore.
     */
    public void cleanupListeners() {

        for (Listener listener : this.getListeners()) {
            if (listener == null) continue;
            this.cleanupListener(listener);
        }

    }

    /**
     * This method cleans up the specified listener if it has to be cleaned up.
     * @param listener listener
     */
    private void cleanupListener(@NotNull Listener listener) {

        // We don't care about the plugin being a listener itself
        if (listener == this.plugin) return;

        // We don't care about excepted listeners
        for (Listener l : List.copyOf(this.exceptedListeners)) if (l == listener) return;

        // Return if listener is registered
        for (ListenerOwner owner : this.getListenerOwners()) if (this.containsExactly(owner.getListeners(), listener)) return;

        HandlerList.unregisterAll(listener);
        this.plugin.getLogger().fine("Removed listener " + listener);

    }

    // ----- LISTENER ADDING -----

    /**
     * This method checks of any listener of any listener owner has to be added.
     */
    public void addListenersOfAll() {
        for (ListenerOwner owner : this.getListenerOwners()) {
            this.addListenersOf(owner);
        }
    }

    /**
     * This method checks of any listener of the specified listener owner has to be added.
     * @param owner owner
     */
    public void addListenersOf(@NotNull ListenerOwner owner) {
        List<Listener> listeners = this.getListeners();

        for (Listener listener : List.copyOf(owner.getListeners())) {
            if (this.containsExactly(List.copyOf(this.exceptedListeners), listener)) continue;
            if (this.containsExactly(listeners, listener)) continue;
            this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
            this.plugin.getLogger().fine("Added listener " + listener);
        }

    }

    // ----- LISTS -----

    public void addSource(@NotNull ListenerOwnerSource source) {
        this.sources.add(source);
    }

    public void removeSource(ListenerOwnerSource source) {
        this.sources.remove(source);
    }

    @NotNull
    public List<ListenerOwnerSource> getSources() {
        return List.copyOf(this.sources);
    }

    public void addExceptedListener(@NotNull Listener exceptedListener) {
        if (this.containsExactly(this.exceptedListeners, exceptedListener)) return;
        this.exceptedListeners.add(exceptedListener);
    }

    public void removeExceptedListener(Listener exceptedListener) {
        this.exceptedListeners.remove(exceptedListener);
    }

    @NotNull
    public List<Listener> getExceptedListeners() {
        return List.copyOf(this.exceptedListeners);
    }

    // ----- OTHER -----

    /**
     * Get a list of all listeners (not registered listeners).
     * This can be used to clear listeners of the game.
     * @return List of listeners
     */
    @NotNull
    public List<Listener> getListeners() {
        List<RegisteredListener> registeredListenerList = List.copyOf(HandlerList.getRegisteredListeners(this.plugin));
        List<Listener> listenerList = new ArrayList<>();

        for (RegisteredListener registeredListener : registeredListenerList) {

            if (!listenerList.contains(registeredListener.getListener())) {
                listenerList.add(registeredListener.getListener());
            }

        }

        return listenerList;
    }

    /**
     * Returns the list of the currently available listener owners.
     * @return list of listener owners
     */
    @NotNull
    public List<ListenerOwner> getListenerOwners() {
        List<ListenerOwner> listenerOwners = new ArrayList<>();
        for (ListenerOwnerSource source : List.copyOf(this.sources)) {
            if (source == null) continue;
            ListenerOwner owner = source.get();
            if (owner == null) continue;
            listenerOwners.add(owner);
        }
        return listenerOwners;
    }

    private boolean containsExactly(List<Listener> list, Listener object) {
        for (Listener o : List.copyOf(list)) if (o == object) return true;
        return false;
    }

    @NotNull
    public Plugin getPlugin() {
        return this.plugin;
    }

}
