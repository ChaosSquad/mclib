package net.chaossquad.mclib.executable;

import net.chaossquad.mclib.misc.Removable;
import org.bukkit.event.Listener;

/**
 * A ManagedListener which consists of a Bukkit listener and a Removable.
 */
public interface ManagedListener extends Listener, Removable {}
