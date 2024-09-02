package net.chaossquad.mclib;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.UUID;

public final class ItemUtils {

    private ItemUtils() {}

    /**
     * Set a base64 encoded custom head to a {@link SkullMeta}.
     * @param skullMeta the {@link SkullMeta} the head should be set for
     * @param customHead base64-encoded player head
     */
    public static void setCustomHeadForSkullMeta(@NotNull SkullMeta skullMeta, @NotNull String customHead) {

        GameProfile cameraProfile = new GameProfile(UUID.randomUUID(), "");
        cameraProfile.getProperties().put("textures", new Property("textures", customHead));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, cameraProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
