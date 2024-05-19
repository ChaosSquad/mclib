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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

/**
 * The dynamic world loading system allows you to create a copy of a template world and load it.
 * The system can create multiple copies of the specified template.
 * Dynamic worlds will be automatically deleted when they are not loaded anymore.
 *
 * Known drawbacks/issues:
 * 1. The {@link this#getTemplateWorldName(World)} is unsafe because it generates the template world name of the world name.
 * 2. Dynamic worlds cannot be cleaned up when the plugin is disabled or the server is stopped, because worlds are not unloaded synchronously. The world only can be cleaned up when the dynamic world loading system was created again.
 *
 * Normal usage:
 * - Create a world of a template by using {@link this#createWorldFromTemplate(String)}.
 * - Unload the world as you would unload any normal bukkit world ({@link org.bukkit.Server#unloadWorld(String, boolean)}) and the world folder of the dynamic world will be cleaned up.
 * - Running {@link this#cleanupDynamicWorldDirectory(String)} or {@link this#cleanupUnloadedDynamicWorldDirectories()} is not required, only use them if you know what you are doing.
 * - If you want to remove/disable the DynamicWorldLoadingSystem manually, you can call {@link this#remove()}, but be aware that there is no way back.
 */
public final class DynamicWorldLoadingSystem implements Listener {
    public static final List<String> DISALLOWED_WORLD_NAMES = List.of("cache", "config", "libraries", "logs", "plugins", "versions", "world");
    private static final String PREFIX = "dynamicworlds-";

    // UTILITY CLASSES

    public static final class DefaultCleanupTask extends BukkitRunnable {
        private final DynamicWorldLoadingSystem system;

        private DefaultCleanupTask(DynamicWorldLoadingSystem system) {
            this.system = system;
        }

        @Override
        public void run() {
            this.system.cleanupUnloadedDynamicWorldDirectories();
        }

        public DynamicWorldLoadingSystem getSystem() {
            return system;
        }

    }

    public static final class SingleWorldCleanupTask extends BukkitRunnable {
        private final DynamicWorldLoadingSystem system;
        private final World world;

        private SingleWorldCleanupTask(DynamicWorldLoadingSystem system, World world) {
            this.system = system;
            this.world = world;
        }

        @Override
        public void run() {

            if (this.system.getPlugin().getServer().getWorld(this.world.getUID()) == null) {
                this.system.cleanupDynamicWorldDirectory(this.world.getName());
                this.cancel();
                return;
            }

        }

        public DynamicWorldLoadingSystem getSystem() {
            return system;
        }

    }

    /**
     * Event Listener.
     */
    public static final class EventListener implements Listener {
        private final DynamicWorldLoadingSystem system;

        public EventListener(DynamicWorldLoadingSystem system) {
            this.system = system;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin() != this.system.getPlugin()) return;
            if (this.system.getDynamicWorlds().isEmpty()) return;
            this.system.getPlugin().getLogger().log(
                    Level.WARNING,
                    "\n" +
                    "---------- WARNING ----------\n" +
                    "There are still dynamic worlds loaded.\n" +
                    "They will not be cleaned up because it is not possible.\n" +
                    "All tasks of the plugin will be stopped when the plugin disables, so it is not possible to wait until the world is unloaded.\n" +
                    "-------------------"
            );
        }

        /**
         * Cleans up the dynamic world when has been unloaded successfully.
         */
        @EventHandler(priority = EventPriority.MONITOR)
        public void onWorldUnload(WorldUnloadEvent event) {
            if (event.isCancelled()) return;
            if (!this.system.getDynamicWorlds().contains(event.getWorld())) return;
            new SingleWorldCleanupTask(this.system, event.getWorld()).runTaskTimer(this.system.getPlugin(), 1, 1);
        }

        public DynamicWorldLoadingSystem getSystem() {
            return system;
        }

    }

    // MAIN CLASS

    private final Plugin plugin;
    private final BukkitTask task;
    private final EventListener listener;
    private int nextId;

    public DynamicWorldLoadingSystem(Plugin plugin) {
        this.plugin = plugin;
        this.listener = new EventListener(this);
        this.nextId = 0;

        this.task = new DefaultCleanupTask(this).runTaskTimer(this.plugin, 1, 1200);
        this.plugin.getServer().getPluginManager().registerEvents(this.listener, this.plugin);
    }

    // WORLD DIRECTORIES

    /**
     * Cleans up a single dynamic world directory if it is not loaded.
     * @param name world name
     * @return success
     */
    public boolean cleanupDynamicWorldDirectory(String name) {
        Path serverDirectory = this.getServerDirectory();
        Path worldPath = serverDirectory.resolve(name).toAbsolutePath().normalize();

        if (!Files.isDirectory(worldPath)) return false;
        if (!worldPath.startsWith(serverDirectory)) return false;
        if (!worldPath.getFileName().toString().startsWith(this.getPrefix())) return false;

        World world = this.plugin.getServer().getWorld(name);
        if (world != null) return false;

        if (deleteDirectory(worldPath)) {
            this.plugin.getLogger().log(Level.INFO, "Deleted dynamic world " + worldPath.getFileName().toString());
            return true;
        }

        return false;
    }

    /**
     * Cleans up all dynamic world directories of worlds that are not loaded.
     */
    public void cleanupUnloadedDynamicWorldDirectories() {
        try {

            Path serverDirectory = this.getServerDirectory();

            for (Path path : Files.list(serverDirectory).toList()) {
                this.cleanupDynamicWorldDirectory(path.getFileName().toString());
            }

        } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to cleanup unloaded worlds", e);
        }
    }

    // WORLD LOADING

    /**
     * Creates a full copy of a world and loads it.
     * @param name world directory name
     * @return loaded world or null if the world was not loaded
     */
    public World createWorldFromTemplate(String name) {
        if (DISALLOWED_WORLD_NAMES.contains(name)) return null;

        // Check if directory exists

        Path worldPath = this.getServerDirectory().resolve(name);
        if (!Files.exists(worldPath) && !Files.isDirectory(worldPath)) return null;

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

    // WORLD INFO

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

    /**
     * Returns the name of the template world.
     * @deprecated Unsafe, only use it if you absolutely must.
     * @return
     */
    @Deprecated
    public String getTemplateWorldName(World world) {
        if (world == null || !this.getDynamicWorlds().contains(world)) return null;

        try {

            Path serverDirectory = this.getServerDirectory();
            for (Path path : Files.list(serverDirectory).toList()) {
                if (!Files.exists(path) || !Files.isDirectory(path)) continue;
                if (!path.getFileName().toString().equals(world.getName())) continue;

                String worldName = world.getName().replace(this.getPrefix(), "");
                String[] splittedWorldName = worldName.split("-");

                return splittedWorldName[0];
            }

        } catch (IOException e) {
            return null;
        }

        return null;
    }

    // UTILITIES

    /**
     * Returns the prefix of the world directories for this dynamic world loading system.
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

    // GETTER

    public DynamicWorldLoadingSystem getSelf() {
        return this;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the event listener of this system.
     * This listener must be added to the exception list if you are using a dynamic world listener system.
     * @return event listener of this system
     */
    public EventListener getListener() {
        return this.listener;
    }

    // REMOVE

    /**
     * Removes the bukkit task and the event handlers.
     * @param cleanup If true, all dynamically loaded worlds will be cleaned up. Else, they will persist until you delete them manually or there is another DynamicWorldLoadingSystem that deletes it.
     */
    public void remove(boolean cleanup) {

        if (cleanup) {
            this.getDynamicWorlds().forEach(world -> {
                this.plugin.getServer().unloadWorld(world, false);
                new SingleWorldCleanupTask(this, world).runTaskTimer(this.plugin, 1, 1);
            });
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
