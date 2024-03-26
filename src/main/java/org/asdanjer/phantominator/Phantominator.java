package org.asdanjer.phantominator;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Statistic.TIME_SINCE_REST;

public final class Phantominator extends JavaPlugin implements CommandExecutor, Listener {


        private HashSet<UUID> phantomToggle = new HashSet<>();

    @Override
    public void onEnable() {
        loadData();
        Bukkit.getPluginManager().registerEvents(this, this);

        // Schedule a repeating task that runs every 20 ticks (1 second)

        getServer().getScheduler().runTaskTimer(this, this::resetInsomnia,0L, 1000L); //30 Minutes
    }
    public void onDisable() {
        saveData();
    }
    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent event) {
        resetInsomniaSingle(event.getPlayer());
    }

    private void resetInsomnia() {
        for (UUID playerUUID : phantomToggle) {
            Player player = (Player) getServer().getPlayer(playerUUID);
            resetInsomniaSingle(player);
            saveData();
        }
    }
    private void resetInsomniaSingle(Player player) {
        if (player!= null && phantomToggle.contains(player.getUniqueId())) {
            player.setStatistic(TIME_SINCE_REST, 0);
        }
    }
    public void saveData() {
        // Step 1: Create a NamespacedKey for your plugin
        NamespacedKey key = new NamespacedKey(this, "phantomToggle");
        // Step 2: Convert the HashSet<UUID> into a format that can be stored
        String serializedUUIDs = phantomToggle.stream()
                .map(UUID::toString)
                .collect(Collectors.joining(";"));

        // Step 4: Store the serialized HashSet<UUID> into the FileConfiguration
        getConfig().set(key.getKey(), serializedUUIDs);

        // Step 5: Save the plugin's config to persist the data
        this.saveConfig();
    }
    public void loadData() {
        // Step 1: Create a NamespacedKey for your plugin
        NamespacedKey key = new NamespacedKey(this, "phantomToggle");

        // Step 2: Retrieve the serialized HashSet<UUID> from the FileConfiguration
        String serializedUUIDs = getConfig().getString(key.getKey());

        // Step 3: Check if the serializedUUIDs is not null
        if (serializedUUIDs != null && !serializedUUIDs.isEmpty()) {
            // Step 4: Convert the serialized HashSet<UUID> back into a HashSet<UUID>
            phantomToggle = Arrays.stream(serializedUUIDs.split(";"))
                    .map(UUID::fromString)
                    .collect(Collectors.toCollection(HashSet::new));
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
                resetInsomniaSingle(player);
                player.sendMessage("Phantoms are now disabled for you.");
            }
        }
        return true;
    }
}