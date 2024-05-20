package net.chaossquad.mclib.actionbar;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * See {@link ActionBarManager}.
 * This is the self scheduled version, which means that you just need to create an object of this class and everything should work.
 */
public class SelfSchedulingActionBarManager extends ActionBarManager {

    public SelfSchedulingActionBarManager(Plugin plugin, BaseComponent splitter) {
        super(plugin, splitter);

        new BukkitRunnable() {

            @Override
            public void run() {
                mainTask();
            }

        }.runTaskTimer(this.getPlugin(), 1, 1);
    }

}
