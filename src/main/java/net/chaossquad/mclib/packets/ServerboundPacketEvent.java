package net.chaossquad.mclib.packets;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerboundPacketEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Packet<?> packet;
    private boolean cancelled;
    private Packet<?> replacement;

    public ServerboundPacketEvent(Player player, Packet<?> packet) {
        super(true);
        this.player = player;
        this.packet = packet;
        this.cancelled = false;
        this.replacement = null;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public Packet<?> getReplacement() {
        return this.replacement;
    }

    public void setReplacement(Packet<?> replacement) {
        this.replacement = replacement;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
