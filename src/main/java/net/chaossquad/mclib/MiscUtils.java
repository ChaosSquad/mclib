package net.chaossquad.mclib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

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

    /**
     * Parses a location list in the following format:<br/>
     * X Y Z<br/>
     * X Y Z<br/>
     * X Y Z<br/>
     * ...
     * @param data location list
     * @return list of location vectors
     */
    public static List<Vector> parseLocationList(String data) {
        List<Vector> locations = new ArrayList<>();

        String[] lines = data.split("\\R"); // "\\R" line breaks for all os
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 3) {
                try {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);
                    locations.add(new Vector(x, y, z));
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Not a Number: " + line);
                }
            } else {
                throw new NumberFormatException("Invalid coordinates: " + line);
            }
        }

        return locations;
    }

    /**
     * Parses an area list in the following format:<br/>
     * X1 Y1 Z1 X2 Y2 Z2<br/>
     * X1 Y1 Z1 X2 Y2 Z2<br/>
     * X1 Y1 Z1 X2 Y2 Z2<br/>
     * ...
     * @param data location list
     * @return list of location vectors
     */
    public static List<BoundingBox> parseAreaList(String data) {
        List<BoundingBox> locations = new ArrayList<>();

        String[] lines = data.split("\\R"); // "\\R" line breaks for all os
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 6) {
                try {
                    double ax = Double.parseDouble(parts[0]);
                    double ay = Double.parseDouble(parts[1]);
                    double az = Double.parseDouble(parts[2]);
                    double bx = Double.parseDouble(parts[3]);
                    double by = Double.parseDouble(parts[4]);
                    double bz = Double.parseDouble(parts[5]);
                    locations.add(new BoundingBox(ax, ay ,az, bx, by, bz));
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Not a Number: " + line);
                }
            } else {
                throw new NumberFormatException("Invalid coordinates: " + line);
            }
        }

        return locations;
    }

    /**
     * Returns the sword "Aspect of the World".<br/>
     * This is a sword we have found on mcstacker saved commands a long time ago.<br/>
     * We now have updated it to the newer versions.<br/>
     * See: <a href="https://gist.github.com/jandie1505/29cfc6b3ee7ab46e3df3dae5b4bcd848">Command version</a><br/>
     * @return aspect of the world item
     */
    public static ItemStack getAspectOfTheWorld() {
        ItemStack item = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Aspect of the World").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED));
        meta.lore(List.of(Component.text("A Sword which came from the world of \\\\\"Minceraft\\\\")));

        meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(Registry.ATTRIBUTE.getKeyOrThrow(Attribute.ARMOR), 10000, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(Registry.ATTRIBUTE.getKeyOrThrow(Attribute.ARMOR_TOUGHNESS), 10000, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(Registry.ATTRIBUTE.getKeyOrThrow(Attribute.ATTACK_SPEED), 10000, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.MOVEMENT_SPEED, new AttributeModifier(Registry.ATTRIBUTE.getKeyOrThrow(Attribute.MOVEMENT_SPEED), 0.3, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, new AttributeModifier(Registry.ATTRIBUTE.getKeyOrThrow(Attribute.KNOCKBACK_RESISTANCE), 10000, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(Registry.ATTRIBUTE.getKeyOrThrow(Attribute.ATTACK_DAMAGE), 10000, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.FOLLOW_RANGE, new AttributeModifier(Registry.ATTRIBUTE.getKeyOrThrow(Attribute.FOLLOW_RANGE), 10000, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.LUCK, new AttributeModifier(Registry.ATTRIBUTE.getKeyOrThrow(Attribute.LUCK), 10000, AttributeModifier.Operation.ADD_NUMBER));

        meta.addEnchant(Enchantment.AQUA_AFFINITY, 255, true);
        meta.addEnchant(Enchantment.BANE_OF_ARTHROPODS, 255, true);
        meta.addEnchant(Enchantment.BLAST_PROTECTION, 255, true);
        meta.addEnchant(Enchantment.CHANNELING, 255, true);
        meta.addEnchant(Enchantment.DEPTH_STRIDER, 255, true);
        meta.addEnchant(Enchantment.EFFICIENCY, 255, true);
        meta.addEnchant(Enchantment.FEATHER_FALLING, 255, true);
        meta.addEnchant(Enchantment.FIRE_PROTECTION, 255, true);
        meta.addEnchant(Enchantment.FLAME, 255, true);
        meta.addEnchant(Enchantment.FORTUNE, 255, true);
        meta.addEnchant(Enchantment.FROST_WALKER, 255, true);
        meta.addEnchant(Enchantment.IMPALING, 255, true);
        meta.addEnchant(Enchantment.INFINITY, 255, true);
        meta.addEnchant(Enchantment.KNOCKBACK, 255, true);
        meta.addEnchant(Enchantment.LOOTING, 255, true);
        meta.addEnchant(Enchantment.LOYALTY, 255, true);
        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 255, true);
        meta.addEnchant(Enchantment.LURE, 255, true);
        meta.addEnchant(Enchantment.MENDING, 255, true);
        meta.addEnchant(Enchantment.MULTISHOT, 255, true);
        meta.addEnchant(Enchantment.PIERCING, 255, true);
        meta.addEnchant(Enchantment.POWER, 255, true);
        meta.addEnchant(Enchantment.PROJECTILE_PROTECTION, 255, true);
        meta.addEnchant(Enchantment.PROTECTION, 255, true);
        meta.addEnchant(Enchantment.PUNCH, 255, true);
        meta.addEnchant(Enchantment.QUICK_CHARGE, 255, true);
        meta.addEnchant(Enchantment.RESPIRATION, 255, true);
        meta.addEnchant(Enchantment.RIPTIDE, 255, true);
        meta.addEnchant(Enchantment.SHARPNESS, 255, true);
        meta.addEnchant(Enchantment.SILK_TOUCH, 255, true);
        meta.addEnchant(Enchantment.SMITE, 255, true);
        meta.addEnchant(Enchantment.SOUL_SPEED, 255, true);
        meta.addEnchant(Enchantment.SWEEPING_EDGE, 255, true);
        meta.addEnchant(Enchantment.THORNS, 255, true);
        meta.addEnchant(Enchantment.THORNS, 255, true);
        meta.addEnchant(Enchantment.UNBREAKING, 255, true);

        meta.setUnbreakable(true);

        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

}
