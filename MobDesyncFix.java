package me.kapt3nen.mobdesyncfix;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class MobDesyncFix extends JavaPlugin {

    private enum SenderType {
        PLAYER,
        CONSOLE,
        OTHER
    }

    @Override
    public void onEnable() {
        logWithPrefix(Level.INFO, "MobDesyncFix has been enabled!");
    }

    @Override
    public void onDisable() {
        logWithPrefix(Level.INFO, "MobDesyncFix has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (isCommand(command, "mobfix")) {
            SenderType type = determineSenderType(sender);

            switch (type) {
                case PLAYER -> handlePlayerCommand((Player) sender);
                case CONSOLE -> sender.sendMessage(colorize("&cThis command cannot be run from console!"));
                default -> sender.sendMessage(colorize("&cUnknown sender type detected!"));
            }
            return true;
        }
        return false;
    }

    private void handlePlayerCommand(Player player) {
        Entity vehicle = player.getVehicle();

        if (vehicle == null) {
            player.sendMessage(colorize("&eYou are not riding any entity."));
            return;
        }

        EntityType type = vehicle.getType();
        Location loc = vehicle.getLocation();

        // Remove the old entity
        vehicle.remove();

        // Spawn a fresh one of the same type
        Entity newEntity = loc.getWorld().spawnEntity(loc, type);

        if (newEntity instanceof LivingEntity livingEntity) {
            // Optional: copy health, etc. if needed
            livingEntity.setCustomName(vehicle.getName());
        }

        // Mount player onto the new entity
        Bukkit.getScheduler().runTaskLater(this, () -> {
            newEntity.addPassenger(player);
            player.sendMessage(colorize("&aYour rideable mob has been replaced and resynced!"));
        }, 1L);
    }

    private SenderType determineSenderType(CommandSender sender) {
        if (sender instanceof Player) return SenderType.PLAYER;
        if (sender instanceof org.bukkit.command.ConsoleCommandSender) return SenderType.CONSOLE;
        return SenderType.OTHER;
    }

    private boolean isCommand(Command command, String expected) {
        return command.getName().equalsIgnoreCase(expected);
    }

    private void logWithPrefix(Level level, String message) {
        getLogger().log(level, "[MobDesyncFix] " + message);
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
