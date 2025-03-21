package net.chaossquad.mclib.misc;

import org.jetbrains.annotations.Nullable;

/**
 * This interface provides a {@link Removable}.<br/>
 * It is mainly used in lambda expressions to dynamically provide remove conditions from the outside of the class where it is stored.
 */
public interface RemovableHolder {
    @Nullable Removable getRemovable();
}
