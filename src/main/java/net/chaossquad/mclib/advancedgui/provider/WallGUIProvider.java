package net.chaossquad.mclib.advancedgui.provider;

import me.leoko.advancedgui.manager.GuiWallManager;
import me.leoko.advancedgui.manager.LayoutManager;
import me.leoko.advancedgui.utils.Direction;
import me.leoko.advancedgui.utils.GuiLocation;
import me.leoko.advancedgui.utils.GuiWallInstance;
import me.leoko.advancedgui.utils.Layout;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Provides utilities for easier handling of gui wall instances.
 */
public class WallGUIProvider {
    private final Layout layout;
    private final GuiLocation location;
    private final int interactionRadius;
    private GuiWallInstance instance;

    public WallGUIProvider(@NotNull String layoutId, @NotNull Location location, @NotNull Direction direction, int interactionRadius) {
        Layout layout = LayoutManager.getInstance().getLayout(layoutId);

        if (layout == null) {
            throw new IllegalArgumentException("Invalid Layout");
        }

        this.layout = layout;
        this.location = new GuiLocation(location.clone(), direction);
        this.interactionRadius = interactionRadius;

        this.instance = null;
    }

    // LAYOUT

    public Layout getLayout() {
        return this.layout;
    }

    public GuiLocation getLocation() {
        return this.location;
    }

    public int getInteractionRadius() {
        return this.interactionRadius;
    }

    // INSTANCE

    /**
     * Checks if the gui wall instance is registered.
     * @return registered
     */
    public final boolean isRegistered() {
        if (this.instance == null) return false;
        return List.copyOf(GuiWallManager.getInstance().getActiveInstances()).contains(this.instance);
    }

    /**
     * Registers the gui wall instance if it is not already registered.
     */
    public void register() {
        if (this.isRegistered()) return;

        this.instance = new GuiWallInstance(
                GuiWallManager.getInstance().getNextId(),
                this.layout,
                this.interactionRadius,
                this.location
        );

        GuiWallManager.getInstance().registerInstance(this.instance, false);
    }

    /**
     * Unregisters the gui wall instance if it is registered.
     */
    public void unregister() {
        if (!this.isRegistered()) return;
        GuiWallManager.getInstance().unregisterInstance(this.instance, false);
        this.instance = null;
    }

    /**
     * Get the instance.
     * @return gui wall instance
     */
    @Nullable
    public final GuiWallInstance getInstance() {
        if (!this.isRegistered()) return null;
        return this.instance;
    }

}
