package me.kapt3nen.mobdesyncfix;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        registerCommands();
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
                case PLAYER -> handlePlayerCommand((Player) sender, args);
                case CONSOLE -> sender.sendMessage(colorize("&cThis command cannot be run from console!"));
                default -> sender.sendMessage(colorize("&cUnknown sender type detected!"));
            }
            return true;
        }
        return false;
    }

    private void handlePlayerCommand(Player player, String[] args) {
        boolean fixableMobFound = searchForFixableMob(player);

        if (!fixableMobFound) {
            player.sendMessage(colorize("&eNo fixable mob found."));
        } else {
            player.sendMessage(colorize("&aFixable mob found and synced!"));
        }
    }

    private boolean searchForFixableMob(Player player) {
        Bukkit.getScheduler().runTaskLater(this, () ->
                player.sendMessage(colorize("&7Searching for mobs...")), 10L);
        return false;
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

    private void registerCommands() {
        logWithPrefix(Level.FINE, "Registering commands...");
    }
}
