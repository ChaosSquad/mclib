package net.chaossquad.mclib;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.lang.reflect.Field;

public final class PacketUtils {

    private PacketUtils() {}

    public static Connection getConnection(ServerPlayer serverPlayer) {

        try {
            ServerGamePacketListenerImpl serverGamePacketListener = serverPlayer.connection;
            Field field = ServerCommonPacketListenerImpl.class.getDeclaredField("c");
            field.setAccessible(true);

            return  (Connection) field.get(serverGamePacketListener);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }

    }

}
