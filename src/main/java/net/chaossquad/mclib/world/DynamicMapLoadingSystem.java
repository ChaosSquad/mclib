package net.chaossquad.mclib.world;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

/**
 * The dynamic map loading system allows you to create a copy of a template map and load it.
 * This copy will be automatically removed when the world is unloaded.
 */
public class DynamicMapLoadingSystem implements Listener, Runnable {
    public static final List<String> DISALLOWED_WORLD_NAMES = List.of("cache", "config", "libraries", "logs", "plugins", "versions", "world");
    private static final String PREFIX = "dynamicworlds-";

    private final Plugin plugin;
    private final BukkitTask task;
    private int nextId;

    public DynamicMapLoadingSystem(Plugin plugin) {
        this.plugin = plugin;
        this.nextId = 0;

        this.task = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this, 1, 1200);
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    // WORLD (UN)LOADING

    /**
     * Cleanup task.
     */
    @Override
    public void run() {
        try {

            Path serverDirectory = this.getServerDirectory();

            for (Path path : Files.list(serverDirectory).toList()) {
                if (!Files.isDirectory(path)) continue;
                if (!path.getFileName().toString().startsWith(this.getPrefix())) continue;

                World world = this.plugin.getServer().getWorld(path.getFileName().toString());
                if (world != null) continue;

                if (deleteDirectory(path)) {
                    this.plugin.getLogger().log(Level.INFO, "Deleted dynamic world " + path.getFileName());
                }

            }

        } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to cleanup unloaded worlds", e);
        }
    }

    /**
     * Creates a full copy of a world and loads it.
     * @param name world directory name
     * @return loaded world or null if the world was not loaded
     */
    public World loadWorld(String name) {
        if (DISALLOWED_WORLD_NAMES.contains(name)) return null;

        // Check if directory exists

        Path worldPath = this.getServerDirectory().resolve(name);
        if (!Files.isDirectory(worldPath)) return null;

        // Copy world directory

        String directoryName = this.getPrefix() + name + "-" + (++this.nextId);
        Path copyPath = this.getServerDirectory().resolve(directoryName);
        if (Files.exists(copyPath) || Files.isDirectory(copyPath)) return null;
        if (!copyDirectory(worldPath, copyPath)) return null;

        // Load world

        WorldCreator worldCreator = new WorldCreator(directoryName);
        World world = worldCreator.createWorld();
        if (world == null) return null;

        // Set values

        world.setAutoSave(false);

        // Log

        this.plugin.getLogger().info("Dynamically loaded world " + world.getUID() + " (name: " + world.getName() + ")");

        // Return

        return world;
    }

    // GETTER

    public DynamicMapLoadingSystem getSelf() {
        return this;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns a list of dynamically loaded worlds.
     * @return list of worlds
     */
    public List<World> getDynamicWorlds() {
        List<World> worlds = new ArrayList<>();

        for (World world : List.copyOf(this.plugin.getServer().getWorlds())) {

            if (world.getName().startsWith(this.getPrefix())) {
                worlds.add(world);
            }

        }

        return List.copyOf(worlds);
    }

    // UTILITIES

    /**
     * Returns the prefix of the world directories for this dynamic map loading system.
     * @return prefix
     */
    public String getPrefix() {
        return PREFIX + this.plugin.getName() + "-";
    }

    /**
     * Returns the absolute normalized path of the server directory.
     * @return server directory
     */
    public Path getServerDirectory() {
        return this.plugin.getServer().getWorldContainer().toPath().toAbsolutePath().normalize();
    }

    // EVENTS

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        this.run();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent event) {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, this, 1);
    }

    // REMOVE

    /**
     * Removes the bukkit task and the event handlers.
     * @param cleanup If true, all dynamically loaded worlds will be cleaned up. Else, they will persist until you delete them manually or there is another DynamicMapLoadingSystem that deletes it.
     */
    public void remove(boolean cleanup) {

        if (cleanup) {
            this.getDynamicWorlds().forEach(world -> this.plugin.getServer().unloadWorld(world, false));
            this.run();
        }

        this.task.cancel();
        HandlerList.unregisterAll(this);
    }

    /**
     * Removes the bukkit task and the event handlers.
     * Does not clean up the worlds.
     */
    public void remove() {
        this.remove(false);
    }

    // STATIC

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

}
