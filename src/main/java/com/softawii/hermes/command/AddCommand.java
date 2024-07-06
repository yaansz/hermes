package com.softawii.hermes.command;

import com.softawii.hermes.HermesPlugin;
import com.softawii.hermes.entity.LocationModel;
import com.softawii.hermes.event.ServerInteractionsEvents;
import com.softawii.hermes.exceptions.DuplicateKeyException;
import com.softawii.hermes.service.PlayerService;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Logger;

public class AddCommand implements TabExecutor {

    private final PlayerService service;
    private final HermesPlugin plugin;
    private final Logger logger;

    public AddCommand(HermesPlugin plugin, PlayerService service) {
        this.service = service;
        this.plugin = plugin;
        this.logger = this.plugin.getLogger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;

        if(args.length < 1) {
            player.sendMessage("Usage: /hermes add <name>");
            return false;
        }

        String name = String.join(" ", args);

        this.logger.info("AddCommand.onCommand : Player=" + player.getName() + " : Name=" + name);
        Location location = player.getLocation();
        LocationModel target = new LocationModel(location.getWorld().getName(), name, location.getX(), location.getY(), location.getZ());

        try {
            if(this.service.addLocation(player.getUniqueId(), target)) {
                player.sendMessage("Location " + name + " added.");
            } else {
                player.sendMessage("Failed to add location " + name + ".");
            }
        } catch (DuplicateKeyException e) {
            player.sendMessage("Location " + name + " already exists.");
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;

        if(args.length != 1) {
            player.sendMessage("Usage: /hermes add <name>");
            return null;
        }

        if(args[0].isEmpty()) {
            return List.of("<name>");
        }

        return null;
    }
}
