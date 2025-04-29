package net.chaossquad.mclib;

import me.leoko.advancedgui.utils.components.Component;
import me.leoko.advancedgui.utils.components.GroupComponent;
import me.leoko.advancedgui.utils.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Utilities for the AdvancedGUI plugin.
 * Requires that the AdvancedGUI plugin is installed.
 */
public final class AdvancedGUIUtils {

    private AdvancedGUIUtils() {}

    /**
     * Updates the component id of the specified component via reflection since there is no way to clone a component with changed id.
     * @param component the component the name should be updated from
     * @param id the new id
     * @return success
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean updateComponentId(Component component, String id) {
        try {
            Field idFIeld = Component.class.getDeclaredField("id");
            idFIeld.setAccessible(true);
            idFIeld.set(component, id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Search for a specific component without searching all child components.
     * @param component group component
     * @param id component id to search for
     * @return found component or null when no result
     */
    public static Component findComponent(GroupComponent component, String id) {

        for (Component c : List.copyOf(component.getComponents())) {
            if (c.getId().equals(id)) return c;
        }

        return null;
    }

    /**
     * Replaces the component tree of an {@link Interaction} with a copy the specified {@link GroupComponent}.
     * For safety reasons, this method will clone the specified component tree and set the interaction of the component tree to the target interaction.
     * @param target target Interaction
     * @param componentTree GroupComponent to set
     * @return success
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean setInteractionComponentTree(@NotNull Interaction target, @NotNull GroupComponent componentTree) {

        try {
            Field field = Interaction.class.getDeclaredField("componentTree");
            field.setAccessible(true);

            field.set(target, componentTree.clone(target));

            return true;
        } catch (Exception e) {
            return false;
        }

    }

}
