package net.chaossquad.mclib.actionbar.manager;

import net.kyori.adventure.text.Component;

/**
 * Stores information about an actionbar text for the {@link ActionBarManager}.
 * @param content content
 * @param removeAt remote at tick
 */
public record ActionBarText(Component content, long removeAt) {}
