package com.softawii.hermes.command;

import com.softawii.hermes.HermesPlugin;
import com.softawii.hermes.entity.LocationModel;
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

public class RemoveCommand implements TabExecutor {

    private final PlayerService service;
    private final HermesPlugin plugin;
    private final Logger logger;

    public RemoveCommand(HermesPlugin plugin, PlayerService service) {
        this.service = service;
        this.plugin = plugin;
        this.logger = this.plugin.getLogger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;

        if(args.length < 1) {
            player.sendMessage("Usage: /hermes remove <name>");
            return false;
        }

        String name = String.join(" ", args);

        this.logger.info("RemoveCommand.onCommand : Player=" + player.getName() + " : Name=" + name);

        if(this.service.removeLocation(player.getUniqueId(), name)) {
            player.sendMessage("Location " + name + " removed.");
        } else {
            player.sendMessage("Failed to remove location " + name + ".");
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;

        if(args.length < 1) {
            player.sendMessage("Usage: /hermes remove <name>");
            return null;
        }

        String text = String.join(" ", args);
        return this.service.getMatchingLocations(player.getUniqueId(), text);
    }
}
