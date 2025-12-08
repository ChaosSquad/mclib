package net.chaossquad.mclib.gamemode.executable;

import net.chaossquad.mclib.dynamicevents.EventListenerManager;
import net.chaossquad.mclib.dynamicevents.ListenerOwner;
import net.chaossquad.mclib.scheduler.TaskScheduler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which provides a {@link TaskScheduler} and {@link ManagedListener} cleanup system.<br/>
 * Can be a base class for gamemodes.
 */
public abstract class CoreExecutable implements ListenerOwner, ListenerRegistrar {
    @NotNull private final EventListenerManager eventListenerManager;
    @NotNull private final Logger logger;
    @NotNull private final TaskScheduler taskScheduler;
    @NotNull private final List<ManagedListener> listeners;

    /**
     * Creates a new CoreExecutable.
     * @param eventListenerManager EventListenerManager
     * @param logger bukkit logger
     */
    public CoreExecutable(@NotNull EventListenerManager eventListenerManager, @NotNull Logger logger) {
        this.eventListenerManager = eventListenerManager;
        this.logger = logger;
        this.taskScheduler = new TaskScheduler(this.logger);
        this.listeners = new ArrayList<>();

        this.taskScheduler.scheduleRepeatingTask(this::cleanupListeners, 1, 100);
    }

    // ----- PLUGIN -----

    /**
     * Return the {@link EventListenerManager}
     * @return {@link EventListenerManager}
     */
    @NotNull
    public final EventListenerManager getEventListenerManager() {
        return this.eventListenerManager;
    }

    // ----- TASKS -----

    /**
     * Returns the {@link TaskScheduler} that can be used to schedule repeating or one time tasks inside this executable.
     * @return task scheduler
     */
    @NotNull
    public final TaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    /**
     * Should be called by a bukkit task to execute the executables tasks.
     * @return true if executed successfully. False if it should be stopped by the plugin.
     */
    public final boolean tick() {

        try {

            if (!this.shouldExecute()) {
                return false;
            }

            this.taskScheduler.tick();
            return true;

        } catch (Exception e) {
            this.logger.log(Level.WARNING, "Exception in executable " + this + ": ", e);
        }

        return false;
    }

    /**
     * The tasks will be executed as long as this method returns true.<br/>
     * If it returns false, no tasks will be executed and the plugin is told that the executable should be removed.<br/>
     * <b>override only, this method always returns true in the base class</b>
     * @return true if the tasks should be executed
     */
    @SuppressWarnings("SameReturnValue")
    @ApiStatus.OverrideOnly
    public boolean shouldExecute() {
        return true;
    }

    // ----- LISTENERS -----

    /**
     * Returns a list containing all listeners of this executable.
     * @return list of listeners
     */
    @NotNull
    public final List<Listener> getListeners() {
        return List.copyOf(this.listeners);
    }

    /**
     * Returns a list containing all listeners of this executable.
     * @return list of listeners
     */
    @NotNull
    public final List<ManagedListener> getManagedListeners() {
        return List.copyOf(this.listeners);
    }

    /**
     * Registers the specified listener.
     * @param listener listener
     * @param instant register the listener instantly
     */
    public final void registerListener(@NotNull ManagedListener listener, boolean instant) {
        for (ManagedListener l : this.listeners) if (l == listener) return;
        this.listeners.add(listener);
        if (instant) this.eventListenerManager.manageListeners();
    }

    /**
     * Registers the specific listener.
     * @param listener listener
     */
    public final void registerListener(@NotNull ManagedListener listener) {
        this.registerListener(listener, false);
    }

    /**
     * Unregisters the specified listener.
     * @param listener listener
     * @param instant unregister the listener instantly
     */
    public final void unregisterListener(@NotNull ManagedListener listener, boolean instant) {
        this.listeners.remove(listener);
        if (instant) this.eventListenerManager.manageListeners();
    }

    /**
     * Unregisters the specific listener.
     * @param listener unregisters the listener
     */
    public final void unregisterListener(@NotNull ManagedListener listener) {
        this.unregisterListener(listener, false);
    }

    /**
     * Checks if the specified listener is registered.
     * @param listener listener
     * @return listener registered
     */
    public final boolean isListenerRegistered(@NotNull ManagedListener listener) {
        for (ManagedListener l : this.listeners) if (l == listener) return true;
        return false;
    }

    /**
     * Removes all listeners marked as to be removed.
     */
    public final void cleanupListeners() {
        for (ManagedListener listener : List.copyOf(this.listeners)) {
            if (listener.toBeRemoved()) this.listeners.remove(listener);
        }
    }

}
