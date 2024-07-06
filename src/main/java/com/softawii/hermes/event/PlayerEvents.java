package com.softawii.hermes.event;

import com.softawii.hermes.HermesPlugin;
import com.softawii.hermes.entity.LocationModel;
import com.softawii.hermes.entity.PlayerModel;
import com.softawii.hermes.exceptions.DuplicateKeyException;
import com.softawii.hermes.service.PlayerService;
import com.softawii.hermes.states.CompassStateInterface;
import com.softawii.hermes.states.CompassStateNoTarget;
import com.softawii.hermes.utils.CompassUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

public class PlayerEvents implements Listener {

    private final HermesPlugin plugin;
    private final PlayerService service;
    private final Player player;
    private CompassStateInterface state;
    private Instant lastActiveBar;
    private Instant lastUnskippableBar;

    public PlayerEvents(HermesPlugin plugin, PlayerService service, Player player) {
        // Player Connect
        this.plugin = plugin;
        this.player = player;
        this.service = service;
        this.lastActiveBar = Instant.now();
        this.lastUnskippableBar = Instant.now();

        if(!hasCompass()) {
            sendTextComponent(ChatColor.GOLD + "Hermes Compass added to your inventory.", true, false);
        } else {
            removeCompass();
        }

        // Adding New Compass
        ItemStack compass = createCompass();
        this.player.getInventory().addItem(compass);
        this.state = new CompassStateNoTarget(this.plugin, this.service, this, this.player);

        if(this.service.getPlayerById(this.player.getUniqueId()).isEmpty()) {
            this.service.createEmptyUser(this.player.getUniqueId());
        }
    }

    //region: Action Bar Coordinates
    public void updatePlayerActionBar() {
        sendTextComponent(getCurrentLocation(), false, false);
    }

    public void sendTextComponent(String message, boolean activeBar, boolean unskippable) {
        if (SECONDS.between(this.lastActiveBar, Instant.now()) < 2 && !activeBar ) return;
        if (SECONDS.between(this.lastUnskippableBar, Instant.now()) < 2) return;

        this.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));

        if(activeBar) this.lastActiveBar = Instant.now();
        if(unskippable) this.lastUnskippableBar = Instant.now();
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        this.state.onPlayerMoveEvent(event);
    }

    private String getCurrentLocation() {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC
                      + "X: " + player.getLocation().getBlockX()
                      + " Y: " + player.getLocation().getBlockY()
                      + " Z: " + player.getLocation().getBlockZ();
    }
    //endregion

    //region: Compass Utils
    private boolean hasCompass() {
        for(ItemStack item : player.getInventory().getContents()) {
            if(CompassUtils.isCompass(plugin, item))
                return true;
        }
        return false;
    }

    public ItemStack getCompass() {
        for(ItemStack item : player.getInventory().getContents()) {
            if(CompassUtils.isCompass(plugin, item))
                return item;
        }
        return null;
    }

    private void removeCompass() {
        for(ItemStack item : player.getInventory().getContents()) {
            if(CompassUtils.isCompass(plugin, item))
                player.getInventory().remove(item);
        }
    }

    public ItemStack createCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.GOLD + "Hermes Compass");
        compassMeta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "hermes-compass"), PersistentDataType.STRING, "hermes-compass");
        compass.setItemMeta(compassMeta);
        return compass;
    }
    //endregion

    //region: Events
    public void onPlayerItemDropEvent(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if(CompassUtils.isCompass(plugin, item)) {
            event.setCancelled(true);
            sendTextComponent(ChatColor.RED + "" + ChatColor.ITALIC + "You cannot drop the Hermes Compass.", true, true);
        }
    }

    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        this.state.onPlayerInteractEvent(event);
    }

    public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent event) {
        this.state.onWorldChange(event);
    }

    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        // Remove Compass
        for(ItemStack item : event.getDrops()) {
            if(CompassUtils.isCompass(this.plugin, item)) {
                event.getDrops().remove(item);
                break;
            }
        }

        // Save Last Death Point to Database
        Location location = player.getLocation();
        Optional<PlayerModel> modelOptional = this.service.getPlayerById(player.getUniqueId());

        if(modelOptional.isEmpty()) return;

        PlayerModel model = modelOptional.get();
        String deathName = model.getNextDeathName();

        LocationModel target = new LocationModel(location.getWorld().getName(), deathName, location.getX(), location.getY(), location.getZ());
        try {
            this.service.addLocation(player.getUniqueId(), target);
        } catch (DuplicateKeyException e) {
            // Do Nothing
        }
    }

    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if(!hasCompass()) {
            ItemStack compass = createCompass();
            this.player.getInventory().addItem(compass);
            this.state = new CompassStateNoTarget(this.plugin, this.service, this, this.player);
        }
    }
    //endregion

    public void changeState(CompassStateInterface state) {
        this.state = state;
    }
}
