package com.softawii.hermes.states;

import com.softawii.hermes.HermesPlugin;
import com.softawii.hermes.entity.LocationModel;
import com.softawii.hermes.entity.PlayerModel;
import com.softawii.hermes.event.PlayerEvents;
import com.softawii.hermes.service.PlayerService;
import com.softawii.hermes.utils.CompassUtils;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Optional;

public class CompassStateNoTarget implements CompassStateInterface {
    private final HermesPlugin plugin;
    private final PlayerService service;
    private final PlayerEvents events;
    private final Player player;

    public CompassStateNoTarget(HermesPlugin plugin, PlayerService service, PlayerEvents events, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.service = service;
        this.events = events;
    }

    @Override
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(!CompassUtils.isCompass(this.plugin, event.getItem())) return;

        // check if the player right-clicked
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Gui gui = this.createCompassGui(event.getItem());
            gui.open(this.player);
        }
    }

    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        // No Target Set - Do Nothing
    }

    @Override
    public void onWorldChange(PlayerChangedWorldEvent event) {
        // No Target Set - Do Nothing
    }

    private Gui createCompassGui(ItemStack compassItem) {
        return CompassUtils.getGui(compassItem, this.plugin, this.service, this.player, this.events);
    }
}
