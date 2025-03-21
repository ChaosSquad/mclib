package net.chaossquad.mclib.world;

import net.chaossquad.mclib.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class WorldsCommand {

    private WorldsCommand() {}

    @SuppressWarnings("SameReturnValue")
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {

        if (args.length < 1) {
            sender.sendMessage("§cUsage: worlds (list|load <name>|unload <index|id|name>|teleport <index|id|name>)");
            return true;
        }

        switch (args[0]) {
            case "list": {

                String message = "§7Loaded worlds:\n";

                int i = 0;
                for (World world : List.copyOf(Bukkit.getServer().getWorlds())) {

                    message = message + "§7[" + i + "] " + world.getName() + " (" + world.getUID() + ");\n";
                    i++;

                }

                sender.sendMessage(message);

                return true;
            }
            case "load": {

                if (args.length < 2) {
                    sender.sendMessage("§cYou need to specify a world name");
                    return true;
                }

                if (Bukkit.getServer().getWorld(args[1]) != null) {
                    sender.sendMessage("§cWorld already loaded");
                    return true;
                }

                sender.sendMessage("§eLoading/creating world...");
                Bukkit.getServer().createWorld(new WorldCreator(args[1]));
                sender.sendMessage("§aWorld successfully loaded/created");

                return true;
            }
            case "unload": {

                if (args.length < 2) {
                    sender.sendMessage("§cYou need to specify a world name/uid/index");
                    return true;
                }

                World world = null;

                try {
                    world = Bukkit.getServer().getWorld(UUID.fromString(args[1]));
                } catch (IllegalArgumentException e) {

                    try {
                        world = Bukkit.getServer().getWorlds().get(Integer.parseInt(args[1]));
                    } catch (IllegalArgumentException e2) {
                        world = Bukkit.getServer().getWorld(args[1]);
                    }

                }

                if (world == null) {
                    sender.sendMessage("§cWorld is not loaded");
                    return true;
                }

                if (world == Bukkit.getServer().getWorlds().get(0)) {
                    sender.sendMessage("§cYou cannot unload the default world");
                    return true;
                }

                for (Player player : List.copyOf(world.getPlayers())) {
                    player.teleport(new Location(Bukkit.getServer().getWorlds().get(0), 0.5, 0, 0.5));
                }

                boolean save = false;

                if (args.length >= 3) {
                    save = Boolean.parseBoolean(args[2]);
                }

                Bukkit.getServer().unloadWorld(world, save);
                sender.sendMessage("§aUnloaded world (save=" + save + ")");

                return true;
            }
            case "teleport": {

                if (args.length < 2) {
                    sender.sendMessage("§cYou need to specify a world name/uid/index");
                    return true;
                }

                World world = null;

                try {
                    world = Bukkit.getServer().getWorld(UUID.fromString(args[1]));
                } catch (IllegalArgumentException e) {

                    try {
                        world = Bukkit.getServer().getWorlds().get(Integer.parseInt(args[1]));
                    } catch (IllegalArgumentException e2) {
                        world = Bukkit.getServer().getWorld(args[1]);
                    }

                }

                if (world == null) {
                    sender.sendMessage("§cWorld is not loaded");
                    return true;
                }

                Location location = new Location(world, 0, 0, 0, 0, 0);

                if (args.length >= 3) {

                    Player player = PlayerUtils.getPlayerFromString(args[2]);

                    if (player == null) {
                        sender.sendMessage("§cPlayer not online");
                        return true;
                    }

                    player.teleport(location);
                    sender.sendMessage("§aTeleporting " + player.getName() + " to " + world.getName());

                } else {

                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§cYou need to be a player to teleport yourself");
                        return true;
                    }

                    ((Player) sender).teleport(location);
                    sender.sendMessage("§aTeleporting yourself to " + world.getName());

                }

                return true;
            }
            default:
                sender.sendMessage("§cUnknown subcommand");
                return true;
        }
    }

    public static List<String> onTabComplete(String[] args) {

        switch (args.length) {
            case 1 -> {
                return List.of("list", "load", "unload", "teleport");
            }
            case 2 -> {

                switch (args[0]) {
                    case "unload", "teleport" -> {
                        return Bukkit.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
                    }
                    default -> {
                        return List.of();
                    }
                }

            }
            default -> {
                return List.of();
            }
        }

    }
}
