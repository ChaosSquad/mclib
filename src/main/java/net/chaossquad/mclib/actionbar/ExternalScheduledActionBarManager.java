package net.chaossquad.mclib.actionbar;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.plugin.Plugin;

/**
 * See {@link ActionBarManager}.
 * This is the external scheduled version, which means that you have to manually call {@link this#run()} every tick.
 */
public class ExternalScheduledActionBarManager extends ActionBarManager implements Runnable {

    public ExternalScheduledActionBarManager(Plugin plugin, BaseComponent splitter) {
        super(plugin, splitter);
    }

    @Override
    public void run() {
        this.mainTask();
    }

}
