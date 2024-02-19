package org.asdanjer.phantominator;

import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public final class Phantominator extends JavaPlugin implements CommandExecutor {

    private HashSet<UUID> phantomToggle = new HashSet<>();

    @Override
    public void onEnable() {
        getCommand("togglephantom").setExecutor(this);
        // Schedule a repeating task that runs every 20 ticks (1 second)
        getServer().getScheduler().runTaskTimer(this, this::resetInsomnia, 36000L, 36000L);
    }

    private void resetInsomnia() {
        for (UUID playerUUID : phantomToggle) {
            Player player = getServer().getPlayer(playerUUID);
            if (player != null) {
                player.setStatistic(Statistic.TIME_SINCE_REST, 0);
            }else {
                phantomToggle.remove(playerUUID);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("phantominator.toggle")) {
                player.sendMessage("You do not have permission to use this command.");
                return true;
            }
            UUID playerUUID = player.getUniqueId();
            if (phantomToggle.contains(playerUUID)) {
                phantomToggle.remove(playerUUID);
                player.sendMessage("Phantoms are now enabled for you.");
            } else {
                phantomToggle.add(playerUUID);
                player.sendMessage("Phantoms are now disabled for you.");
            }
        }
        return true;
    }
}