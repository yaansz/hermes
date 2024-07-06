package com.softawii.hermes.utils;

import com.softawii.hermes.HermesPlugin;
import com.softawii.hermes.entity.LocationModel;
import com.softawii.hermes.entity.PlayerModel;
import com.softawii.hermes.event.PlayerEvents;
import com.softawii.hermes.service.PlayerService;
import com.softawii.hermes.states.CompassStateTarget;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Optional;

public class CompassUtils {

    public static boolean isCompass(HermesPlugin plugin, ItemStack item) {
        if(item == null)
            return false;

        if(item.getItemMeta() == null)
            return false;

        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        return container.get(new NamespacedKey(plugin, "hermes-compass"), PersistentDataType.STRING) != null;
    }

    public static Gui getGui(ItemStack compassItem, HermesPlugin plugin, PlayerService service, Player player, PlayerEvents events) {
        Optional<PlayerModel> modelOptional = service.getPlayerById(player.getUniqueId());

        if(modelOptional.isEmpty())
            return new Gui(1, "Hermes Compass", new HashSet<>());

        PlayerModel model = modelOptional.get();
        int size = model.getLocations().size();
        int rows = (int) Math.ceil(size / 9.0);

        if(rows == 0) rows = 1;

        HashSet<InteractionModifier> modifiers = new HashSet<>();
        Gui gui = new Gui(rows, "Hermes Compass", modifiers);

        for(LocationModel in : model.getLocations()) {
            if(!player.getWorld().getName().equals(in.getWorld()))
                continue;

            ItemStack item = new ItemStack(Material.NETHER_STAR);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(in.toDisplayString());
            item.setItemMeta(meta);

            GuiItem guiItem = ItemBuilder.from(item).asGuiItem(event -> {
                Location location = new Location(Bukkit.getWorld(in.getWorld()), in.getX(), in.getY(), in.getZ());
                CompassMeta compass = (CompassMeta) compassItem.getItemMeta();
                compass.setLodestoneTracked(false);
                compass.setLodestone(location);
                compassItem.setItemMeta(compass);

                events.changeState(new CompassStateTarget(plugin, service, events, player));
                events.sendTextComponent(ChatColor.AQUA + "" + net.md_5.bungee.api.ChatColor.ITALIC + "Compass updated to " + in.getName(), true, false);

                player.closeInventory();
                event.setCancelled(true);
            });

            gui.addItem(guiItem);
        }

        return gui;
    }
}
