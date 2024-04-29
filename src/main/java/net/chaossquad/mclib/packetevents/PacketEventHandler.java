package net.chaossquad.mclib.packetevents;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.chaossquad.mclib.PacketUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.NoSuchElementException;
import java.util.UUID;

public final class PacketEventHandler implements Listener {
    private final Plugin plugin;
    private final UUID uuid;

    public PacketEventHandler(Plugin plugin) {
        this.plugin = plugin;
        this.uuid = UUID.randomUUID();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    // PACKET LISTENERS

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Connection connection = PacketUtils.getConnection(((CraftPlayer) event.getPlayer()).getHandle());

        if (connection == null) {
            return;
        }

        connection.channel.pipeline().addBefore("packet_handler", this.getReaderName(), new ChannelInboundHandlerAdapter() {

            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                boolean cancelled = false;
                Packet replacement = null;

                if (msg instanceof Packet<?> packet) {

                    ClientboundPacketEvent packetEvent = new ClientboundPacketEvent(event.getPlayer(), packet);
                    plugin.getServer().getPluginManager().callEvent(packetEvent);
                    cancelled = packetEvent.isCancelled();
                    replacement = packetEvent.getReplacement();

                }

                if (cancelled) {
                    return;
                }

                if (replacement != null) {
                    ctx.fireChannelRead(replacement);
                    return;
                }

                ctx.fireChannelRead(msg);
            }

        });

        connection.channel.pipeline().addBefore("packet_handler", this.getWriterName(), new ChannelOutboundHandlerAdapter() {

            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                boolean cancelled = false;
                Packet replacement = null;

                if (msg instanceof Packet<?> packet) {

                    ServerboundPacketEvent packetEvent = new ServerboundPacketEvent(event.getPlayer(), packet);
                    plugin.getServer().getPluginManager().callEvent(packetEvent);
                    cancelled = packetEvent.isCancelled();
                    replacement = packetEvent.getReplacement();

                }

                if (cancelled) {
                    return;
                }

                if (replacement != null) {
                    ctx.write(replacement, promise);
                    return;
                }

                ctx.write(msg, promise);
            }

        });

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Connection connection = PacketUtils.getConnection(((CraftPlayer) event.getPlayer()).getHandle());

        if (connection == null) {
            return;
        }

        try {
            connection.channel.pipeline().remove(this.getReaderName());
        } catch (NoSuchElementException ignored) {
            // should already be removed at this point
        }

        try {
            connection.channel.pipeline().remove(this.getWriterName());
        } catch (NoSuchElementException ignored) {
            // should already be removed at this point
        }

    }

    // GETTER

    public Plugin getPlugin() {
        return plugin;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getReaderName() {
        return "MCLIB-READER-" + uuid.toString();
    }

    public String getWriterName() {
        return "MCLIB-WRITER-" + uuid.toString();
    }

}
