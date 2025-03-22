package net.chaossquad.mclib.actionbar.manager;

import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * See {@link ActionBarManager}.
 * This is the external scheduled version, which means that you have to manually call {@link #run()} every tick.
 */
public class ExternalScheduledActionBarManager extends ActionBarManager implements Runnable {

    /**
     * Creates a new ExternalScheduledActionBarManager.
     * @param plugin plugin
     * @param splitter splitter
     */
    public ExternalScheduledActionBarManager(@NotNull Plugin plugin, @NotNull Component splitter) {
        super(plugin, splitter);
    }

    /**
     * Has to be called by a task to run this ActionBarManager.
     */
    @Override
    public void run() {
        this.mainTask();
    }

}
