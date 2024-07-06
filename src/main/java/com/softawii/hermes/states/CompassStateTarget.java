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
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

public class CompassStateTarget implements CompassStateInterface {
    private final HermesPlugin plugin;
    private final PlayerService service;
    private final PlayerEvents events;
    private final Player player;

    public CompassStateTarget(HermesPlugin plugin, PlayerService service, PlayerEvents events, Player player) {
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
        } else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // show distance to target
            ItemStack compass = event.getItem();
            CompassMeta meta = (CompassMeta) compass.getItemMeta();
            Location lodestone = meta.getLodestone();
            double distance = lodestone.distance(player.getLocation());
            DecimalFormat f = new DecimalFormat("##.00");
            events.sendTextComponent(ChatColor.GREEN + f.format(distance) + " blocks away from target location", true, false);
        }
    }

    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        ItemStack compass = events.getCompass();
        if(compass == null) return;

        CompassMeta meta = (CompassMeta) compass.getItemMeta();
        Location lodestone = meta.getLodestone();
        double distance = lodestone.distance(player.getLocation());

        if(distance < 10) {
            // target reached
            events.sendTextComponent(ChatColor.GOLD + "Target reached", true, false);

            Firework firework = lodestone.getWorld().spawn(lodestone, Firework.class);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BALL).build());
            fireworkMeta.setPower(1);
            firework.setFireworkMeta(fireworkMeta);

            // play sound
            lodestone.getWorld().playSound(lodestone, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);

            // reset compass
            meta.setLodestone(null);
            meta.setLodestoneTracked(false);
            compass.setItemMeta(meta);

            this.events.changeState(new CompassStateNoTarget(this.plugin, this.service, this.events, this.player));
        }
    }

    @Override
    public void onWorldChange(PlayerChangedWorldEvent event) {
        ItemStack compass = events.getCompass();
        if(compass == null) return;

        CompassMeta meta = (CompassMeta) compass.getItemMeta();

        // reset compass
        meta.setLodestone(null);
        meta.setLodestoneTracked(false);
        compass.setItemMeta(meta);

        this.events.changeState(new CompassStateNoTarget(this.plugin, this.service, this.events, this.player));
    }

    private Gui createCompassGui(ItemStack compassItem) {
        return CompassUtils.getGui(compassItem, this.plugin, this.service, this.player, this.events);
    }
}
