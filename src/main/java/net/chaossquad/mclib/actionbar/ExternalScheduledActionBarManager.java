package net.chaossquad.mclib.actionbar;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.plugin.Plugin;

public class ExternalScheduledActionBarManager extends ActionBarManager implements Runnable {

    public ExternalScheduledActionBarManager(Plugin plugin, BaseComponent splitter) {
        super(plugin, splitter);
    }

    @Override
    public void run() {
        this.mainTask();
    }

}
