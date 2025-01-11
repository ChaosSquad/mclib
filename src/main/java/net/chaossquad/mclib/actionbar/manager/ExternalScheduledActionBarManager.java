package net.chaossquad.mclib.actionbar.manager;

import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * See {@link ActionBarManager}.
 * This is the external scheduled version, which means that you have to manually call {@link this#run()} every tick.
 */
public class ExternalScheduledActionBarManager extends ActionBarManager implements Runnable {

    public ExternalScheduledActionBarManager(@NotNull Plugin plugin, @NotNull Component splitter) {
        super(plugin, splitter);
    }

    @Override
    public void run() {
        this.mainTask();
    }

}
