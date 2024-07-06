package com.softawii.hermes.command;
import com.softawii.hermes.HermesPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Commands(@Command(name = "hermes", permission = "hermes.generate",
        permissionMessage = "You can't use this!", usage = "/hermes help"))
public class HermesCommand implements TabExecutor {
    private final HermesPlugin plugin;
    private final Map<String, TabExecutor> executor;

    public HermesCommand(HermesPlugin plugin, Map<String, TabExecutor> executor) {
        this.plugin = plugin;
        this.executor = executor;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length < 1) return false;

        TabExecutor subcommand = this.executor.get(args[0]);

        if (subcommand != null) {
            return subcommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;

        if (args.length == 1) return this.executor.keySet().stream().toList();

        TabExecutor subcommand = this.executor.get(args[0]);

        if (subcommand != null) {
            return subcommand.onTabComplete(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        } else {
            return null;
        }
    }
}