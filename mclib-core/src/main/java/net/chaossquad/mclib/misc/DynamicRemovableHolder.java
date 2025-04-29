package net.chaossquad.mclib.misc;

import org.jetbrains.annotations.Nullable;

/**
 * This interface is an extended version of the {@link RemovableHolder}, which also allows setting the removable.<br/>
 * It is normally implemented by a class which stores a removable.<br/>
 * That removable can set after the object creation or change.
 */
public interface DynamicRemovableHolder extends RemovableHolder {

    /**
     * Sets the removable.
     * @param removable removable
     */
    void setRemovable(@Nullable Removable removable);

}
