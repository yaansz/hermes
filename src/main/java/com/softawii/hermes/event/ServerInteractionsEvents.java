package com.softawii.hermes.event;

import com.softawii.hermes.HermesPlugin;
import com.softawii.hermes.command.AddCommand;
import com.softawii.hermes.command.HermesCommand;
import com.softawii.hermes.command.RemoveCommand;
import com.softawii.hermes.entity.LocationModel;
import com.softawii.hermes.service.PlayerService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerInteractionsEvents implements Listener {

    private final HermesPlugin plugin;
    private final PlayerService playerService;
    private final HashMap<UUID, PlayerEvents> players;
    private final BukkitTask playerUpdateTask;
    private final Logger logger;

    public ServerInteractionsEvents(HermesPlugin plugin, PlayerService playerService) {
        this.plugin = plugin;
        this.playerService = playerService;

        this.players = new HashMap<>();
        this.logger = plugin.getLogger();

        this.playerUpdateTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this::UpdatePlayersLocation, 0, 5);

        Bukkit.getPluginManager().registerEvents(this, this.plugin);

        Map<String, TabExecutor> commands = new HashMap<>();
        commands.put("add", new AddCommand(this.plugin, this.playerService));
        commands.put("remove", new RemoveCommand(this.plugin, this.playerService));
        this.plugin.getCommand("hermes").setExecutor(new HermesCommand(this.plugin, commands));

        this.logger.info("ServerInteractionsEvents : registered");

        checkOnlinePlayers();
    }

    private void checkOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.players.containsKey(player.getUniqueId())) {
                this.players.put(player.getUniqueId(), new PlayerEvents(this.plugin, this.playerService, player));
                this.logger.info("ServerInteractionsEvents.checkOnlinePlayers : registered for " + player.getName());
            }
        }
    }

    private void UpdatePlayersLocation() {
        for (PlayerEvents playerEvents : this.players.values()) {
            playerEvents.updatePlayerActionBar();
        }
    }

    public void close() {
        this.players.clear();
        this.playerUpdateTask.cancel();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.players.put(player.getUniqueId(), new PlayerEvents(this.plugin, this.playerService, player));
        this.logger.info("ServerInteractionsEvents.onPlayerJoin : registered for " + player.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.players.remove(player.getUniqueId());
        this.logger.info("ServerInteractionsEvents.onPlayerQuit : removing " + player.getName());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        this.players.get(player.getUniqueId()).onPlayerMove(event);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        this.players.get(player.getUniqueId()).onPlayerChangeWorldEvent(event);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        this.players.get(player.getUniqueId()).onPlayerInteractEvent(event);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.players.get(player.getUniqueId()).onPlayerDeathEvent(event);
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        this.players.get(player.getUniqueId()).onPlayerRespawnEvent(event);
    }

    @EventHandler
    public void onPlayerItemDropEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        this.players.get(player.getUniqueId()).onPlayerItemDropEvent(event);
    }
}
