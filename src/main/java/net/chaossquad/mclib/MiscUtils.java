package net.chaossquad.mclib;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;

public final class MiscUtils {

    private MiscUtils() {}

    public static String[] shiftArgs(String[] array, int shiftAmount) {
        int newLength = array.length - shiftAmount;
        if (newLength < 0) return new String[0];

        String[] shiftedArray = new String[newLength];

        for (int i = 0; i < shiftedArray.length; i++) {
            shiftedArray[i] = array[i + shiftAmount];
        }

        return shiftedArray;
    }

    public static String getDurationFormat(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        String formattedTime;

        if (duration.toHours() > 0) {
            formattedTime = String.format("%d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
        } else {
            formattedTime = String.format("%d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
        }

        return formattedTime;
    }

    /**
     * Clones a list and their elements.
     * @param cloneFrom the list that should be cloned
     * @param cloneInto the list the cloned objects should be put into. NEEDS TO BE MODIFIABLE!
     * @param clazz type of the objects that should be cloned
     * @return true if all objects in the cloneFrom list were cloned. false if at least one object has not been cloned successfully.
     * @param <T> type
     */
    public static <T> boolean cloneObjectsInto(List<T> cloneFrom, List<T> cloneInto, Class<T> clazz) {
        boolean success = true;

        for (T obj : cloneFrom) {

            // Try to clone via copy constructor

            try {

                Constructor<T> constructor = clazz.getConstructor(clazz);
                cloneInto.add(constructor.newInstance(obj));

                continue;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {}

            // Try to clone via clone method

            try {

                Method cloneMethod = obj.getClass().getMethod("clone");
                @SuppressWarnings("unchecked")
                T clonedObj = (T) cloneMethod.invoke(obj);
                cloneInto.add(clonedObj);

                continue;
            } catch (NoSuchMethodException | ClassCastException | IllegalAccessException | InvocationTargetException ignored) {}

            // Set success to false (RUN LAST!)

            success = false;

        }

        return success;
    }

}
