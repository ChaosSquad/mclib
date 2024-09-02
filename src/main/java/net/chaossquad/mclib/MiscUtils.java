package net.chaossquad.mclib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

/**
 * Utilities that don't fit into the other categories.
 */
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

    /**
     * Clones a list and their elements.
     * @param list the list that should be cloned
     * @param clazz type of the objects that should be cloned
     * @return cloned unmodifiable list that contains all objects that have been cloned successfully
     * @param <T> type
     */
    public static <T> List<T> cloneWithObjects(List<T> list, Class<T> clazz) {
        List<T> clone = new ArrayList<>();
        cloneObjectsInto(list, clone, clazz);
        return List.copyOf(clone);
    }

    /**
     * Gets a resource embedded into the jar file.
     * @param plugin plugin
     * @param filename file name
     * @return content of the file
     */
    public static String getEmbeddedFile(Plugin plugin, String filename) {
        InputStream file = plugin.getResource(filename);
        if (file == null) return null;

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();

        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error while reading embedded resource " + filename, e);
            return null;
        }

    }

    /**
     * Deletes the specific directory, even if it is not empty.
     * @param path path of the directory to delete
     * @return success
     */
    public static boolean deleteDirectory(Path path) {
        path = path.toAbsolutePath().normalize();
        if (!Files.exists(path) || !Files.isDirectory(path)) return false;

        try {

            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException ignored) {}
                    });

            return true;

        } catch (IOException e) {
            return false;
        }

    }

    /**
     * Copies an entire file tree.
     * Only works if the target directory does not exist.
     * @param source source directory
     * @param target target directory
     * @return success
     */
    public static boolean copyDirectory(Path source, Path target) {

        if (Files.exists(target) || Files.isDirectory(target)) return false;
        if (!Files.isDirectory(source)) return false;

        try {

            Files.walkFileTree(source, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = target.resolve(source.relativize(dir));
                    try {
                        Files.copy(dir, targetDir);
                    } catch (FileAlreadyExistsException e) {
                        // Already exists, continue
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(source.relativize(file)));
                    return FileVisitResult.CONTINUE;
                }

            });

            return true;
        } catch (IOException e) {
            return false;
        }

    }

}
