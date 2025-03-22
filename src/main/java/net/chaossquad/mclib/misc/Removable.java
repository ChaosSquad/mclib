package net.chaossquad.mclib.misc;

/**
 * A class implementing this interface can "say" that it should be removed because it is no longer needed.
 */
public interface Removable {

    /**
     * Returns true when has to be removed.
     * @return to be removed
     */
    boolean toBeRemoved();

}
