package net.chaossquad.mclib;

import java.time.Duration;

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

}
