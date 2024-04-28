package net.chaossquad.mclib.actionbar;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

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
