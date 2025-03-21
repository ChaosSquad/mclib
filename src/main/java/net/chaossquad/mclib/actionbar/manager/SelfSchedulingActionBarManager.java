package net.chaossquad.mclib.actionbar.manager;

import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * See {@link ActionBarManager}.
 * This is the self scheduled version, which means that you just need to create an object of this class and everything should work.
 */
public class SelfSchedulingActionBarManager extends ActionBarManager {

    /**
     * Creates a new self-scheduling ActionBarManager.
     * @param plugin plugin
     * @param splitter splitter
     */
    public SelfSchedulingActionBarManager(@NotNull Plugin plugin, @NotNull Component splitter) {
        super(plugin, splitter);

        new BukkitRunnable() {

            @Override
            public void run() {
                mainTask();
            }

        }.runTaskTimer(this.getPlugin(), 1, 1);
    }

}
