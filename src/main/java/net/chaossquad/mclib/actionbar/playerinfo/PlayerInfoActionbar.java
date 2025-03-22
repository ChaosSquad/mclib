package net.chaossquad.mclib.actionbar.playerinfo;

import net.chaossquad.mclib.actionbar.manager.ActionBarManager;
import net.chaossquad.mclib.scheduler.Task;
import net.chaossquad.mclib.scheduler.TaskRunnable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An actionbar which shows the current player name and health in the actionbar.
 */
public final class PlayerInfoActionbar implements TaskRunnable {
    @NotNull private final PlayerProvider playerProvider;
    @NotNull private final ActionBarProvider actionBar;
    private final int range;

    /**
     * Creates a PlayerInfoActionbar.
     * @param playerProvider player provider
     * @param actionBar actionbar provider
     * @param range range
     */
    public PlayerInfoActionbar(@NotNull PlayerProvider playerProvider, @NotNull ActionBarProvider actionBar, int range) {
        this.playerProvider = playerProvider;
        this.actionBar = actionBar;
        this.range = range;
    }

    // ----- TASKS -----

    /**
     * Task
     * @param task task
     */
    @Override
    public void run(Task task) {

        for (Player player : this.playerProvider.getPlayers()) {

            Location eyeLocation = player.getEyeLocation().clone();
            RayTraceResult result = player.getLocation().getWorld().rayTrace(
                    eyeLocation,
                    eyeLocation.getDirection(),
                    this.range,
                    FluidCollisionMode.NEVER,
                    true,
                    0.5,
                    entity -> entity != player
            );

            if (result == null) continue;

            Entity entity = result.getHitEntity();
            if (!(entity instanceof Player target)) continue;

            AttributeInstance maxHealthAttribute = target.getAttribute(Attribute.MAX_HEALTH);
            String maxHealthString = maxHealthAttribute == null ? "E" : String.valueOf((int) maxHealthAttribute.getValue());

            this.actionBar.sendActionBar(
                    player,
                    Component.text("Looking at:").color(NamedTextColor.GRAY)
                            .appendSpace()
                            .append(target.displayName())
                            .appendSpace()
                            .append(Component.text("(❤" + (int) target.getHealth() + "/" + maxHealthString + ")"))
            );

        }

    }

    // ----- OTHER -----

    /**
     * Returns the PlayerProvider.
     * @return player provider
     */
    public @NotNull PlayerProvider getPlayerProvider() {
        return playerProvider;
    }

    /**
     * Returns the ActionbarProvider.
     * @return actionbar provider
     */
    public @NotNull ActionBarProvider getActionBarProvider() {
        return actionBar;
    }

    /**
     * Returns the range.
     * @return range
     */
    public int getRange() {
        return range;
    }

    // ----- INNER CLASSES -----

    /**
     * Provides the players for the PlayerInfoActionbar.
     */
    public interface PlayerProvider {

        /**
         * Returns the list of the provided players.
         * @return list of players
         */
        @NotNull List<@NotNull Player> getPlayers();

    }

    /**
     * Provides the actionbar.
     */
    public interface ActionBarProvider {

        /**
         * Sends the actionbar to the player.
         * @param player player
         * @param message message
         */
        void sendActionBar(@NotNull Player player, @NotNull Component message);

        /**
         * Creates an ActionBarProvider using the MCLIB {@link ActionBarManager}.
         * @param manager manager
         * @param actionBarId actionbar id
         * @param duration duration
         * @return ActionbarProvider
         */
        static ActionBarProvider mclib(@NotNull ActionBarManager manager, String actionBarId, int duration) {
            return (player, message) -> manager.sendActionBarMessage(player, actionBarId, duration, message);
        }

        /**
         * Creates an ActionBarProvider using the default player method to send actionbars.
         * @return ActionBarProvider
         */
        static ActionBarProvider vanilla() {
            return Audience::sendActionBar;
        }

    }

}
