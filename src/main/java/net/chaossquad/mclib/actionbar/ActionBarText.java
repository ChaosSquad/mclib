package net.chaossquad.mclib.actionbar;

import net.md_5.bungee.api.chat.BaseComponent;

public record ActionBarText(BaseComponent[] content, long removeAt) {}
