package net.chaossquad.mclib.world;

import net.chaossquad.mclib.MiscUtils;
import net.chaossquad.mclib.WorldUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * <h2>DynamicWorldLoadingSystem</h2>
 *
 * <h3>What is it?</h3>
 * <p>
 * The dynamic world loading system allows you to create a copy of a template world and load it.
 * The system can create multiple copies of the specified template.
 * Dynamic worlds will be automatically deleted when they are not loaded anymore.
 * </p>
 *
 * <h3>How to use it?</h3>
 * <p>
 * You need to have a template world directory inside your server folder (like a normal world on the server).
 * Then you can create a new dynamic world of that template using {@link #createWorldFromTemplate(String)}.
 * The template world is then copied and loaded.
 * Dynamic worlds are automatically deleted when they are no longer required.
 * </p>
 *
 * <h3>What do I need to know?</h3>
 * <p>
 * - You need to call {@link #remove()} inside your {@link Plugin#onDisable()} method to delete all dynamic worlds.
 * - Creating a DynamicWorldLoadingSystem will automatically delete all existing dynamic worlds.
 * - Worlds that have names starting with <code>dynamicworlds-PLUGIN_NAME-</code> will be considered as dynamic worlds
 *   and are therefore affected by actions of the DynamicWorldLoadingSystem, even if you have copied them manually to the server directory!
 * </p>
 */
public class DynamicWorldLoadingSystem implements Runnable {
    private static final List<String> DISALLOWED_WORLD_NAMES = List.of("cache", "config", "libraries", "logs", "plugins", "versions", "world");

    private static final String PREFIX = "dynamicworlds-";
    private static final String UID_FILE_NAME = "uid.dat";

    private final Plugin plugin;
    private final BukkitTask task;
    private final AtomicInteger nextId;

    /**
     * Creates a new dynamic world loading system.
     * @param plugin plugin
     */
    public DynamicWorldLoadingSystem(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.task = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this, 1, 10*20);
        this.nextId = new AtomicInteger(1);

        this.unloadAndDeleteAllDynamicWorlds();
    }

    // TASK

    @Override
    public void run() {
        this.deleteUnloadedDynamicWorlds();
    }

    // WORLD LOADING/UNLOADING

    /**
     * Creates a full copy of a world and loads it.
     * @param name world directory name
     * @return loaded world or null if the world was not loaded
     */
    public World createWorldFromTemplate(String name) {
        if (this.isRemoved()) return null;
        if (DISALLOWED_WORLD_NAMES.contains(name)) return null;

        // Check if directory exists

        Path worldPath = this.getServerDirectory().resolve(name);
        if (!Files.exists(worldPath) && !Files.isDirectory(worldPath)) return null;

        // Copy world directory

        String directoryName = this.getPrefix() + name + "-" + (this.nextId.getAndAdd(1));
        Path copyPath = this.getServerDirectory().resolve(directoryName).toAbsolutePath();
        if (Files.exists(copyPath) || Files.isDirectory(copyPath)) return null;
        if (!MiscUtils.copyDirectory(worldPath, copyPath)) return null;

        // Delete UID file

        try {
            Path uidFilePath = copyPath.resolve(UID_FILE_NAME).toAbsolutePath();
            Files.deleteIfExists(uidFilePath);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to delete UID file in world " + directoryName, e);
        }

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

    /**
     * Unloads a world and deletes its files if it is a dynamic world.
     * @param name world name
     */
    public void deleteWorld(String name) {

        World world = this.plugin.getServer().getWorld(name);
        if (world != null) {
            WorldUtils.unloadWorld(world, false);
        }

        Path path = this.getServerDirectory().resolve(name);
        if (!Files.exists(path)) return;
        if (!Files.isDirectory(path)) return;
        if (!this.isValidWorldPath(path)) return;

        MiscUtils.deleteDirectory(path);

    }

    // ----- WORLD CLEANUP -----

    /**
     * Unloads and deletes all dynamic worlds.
     */
    public void unloadAndDeleteAllDynamicWorlds() {

        for (Path path : this.getDynamicWorldDirectories()) {
            this.deleteWorld(path.getFileName().toString());
        }

        for (World world : this.getDynamicWorlds()) {
            this.deleteWorld(world.getName());
        }

    }

    /**
     * Deletes all unloaded dynamic worlds.
     */
    public void deleteUnloadedDynamicWorlds() {

        for (Path path : this.getDynamicWorldDirectories()) {

            World world = this.plugin.getServer().getWorld(path.getFileName().toString());
            if (world == null) {
                this.deleteWorld(path.getFileName().toString());
            }

        }

    }

    /**
     * Unloads deleted dynamic worlds.
     */
    public void unloadDeletedDynamicWorlds() {

        List<String> dynamicWorldNames = this.getDynamicWorldDirectories().stream().filter(this::isValidWorldPath).map(path -> path.getFileName().toString()).toList();
        for (World world : this.getDynamicWorlds()) {

            if (!dynamicWorldNames.contains(world.getName())) {
                this.deleteWorld(world.getName());
            }

        }

    }

    // WORLD INFO

    /**
     * Returns a list of dynamically loaded worlds.
     * @return list of worlds
     */
    public List<World> getDynamicWorlds() {
        List<World> worlds = new ArrayList<>();

        for (World world : List.copyOf(this.plugin.getServer().getWorlds())) {

            if (this.isDynamicWorld(world)) {
                worlds.add(world);
            }

        }

        return List.copyOf(worlds);
    }

    /**
     * Returns a list of all dynamic world paths.
     * @return list of dynamic world paths
     */
    public List<Path> getDynamicWorldDirectories() {

        try {
            List<Path> paths = new LinkedList<>();

            for (Path path : Files.list(this.getServerDirectory()).toList()) {
                path = path.toAbsolutePath().normalize();
                if (!this.isValidWorldPath(path)) continue;
                paths.add(path);
            }

            return List.copyOf(paths);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to get dynamic world directories", e);
            return List.of();
        }

    }

    /**
     * Returns if the specified path is a valid world path.
     * This does not check if the world directory does exist.
     * @param path path to check
     * @return true if world path is valid
     */
    public boolean isValidWorldPath(@NotNull Path path) {

        boolean isAbsolute = path.isAbsolute();
        boolean isDirectory = Files.isDirectory(path);
        boolean isInServerDirectory = path.startsWith(this.getServerDirectory());
        boolean hasDynamicWorldsPrefix = path.getFileName().toString().startsWith(this.getPrefix());
        boolean isNoForbiddenDirectory = !DISALLOWED_WORLD_NAMES.contains(path.getFileName().toString());

        return isAbsolute && isDirectory && isInServerDirectory && hasDynamicWorldsPrefix && isNoForbiddenDirectory;
    }

    /**
     * Returns true if the specified world is a dynamic world.
     * @param world world to check
     * @return true if world is dynamic world
     */
    public boolean isDynamicWorld(@NotNull World world) {
        return world.getName().startsWith(this.getPrefix());
    }

    // REMOVE

    /**
     * Deletes all dynamically created worlds and disables the dynamic world loading system.
     */
    public void remove() {
        this.task.cancel();
        this.unloadAndDeleteAllDynamicWorlds();
    }

    // OTHER

    /**
     * Returns the plugin.
     * @return plugin
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the next world id.
     * @return next world id
     */
    public int getNextId() {
        return this.nextId.get();
    }

    /**
     * Returns true if the dynamic world loading system is cancelled.
     * @return cancelled
     */
    public boolean isRemoved() {
        return this.task.isCancelled();
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

}
