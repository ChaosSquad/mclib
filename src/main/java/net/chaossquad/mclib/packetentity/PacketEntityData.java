package net.chaossquad.mclib.packetentity;

/**
 * Represents custom data stored in the {@link PacketEntity}
 * @param data
 * @param <T>
 */
public record PacketEntityData<T>(T data) {
}
