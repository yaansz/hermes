package com.softawii.hermes.states;

import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public interface CompassStateInterface {
    void onPlayerInteractEvent(PlayerInteractEvent event);
    void onPlayerMoveEvent(PlayerMoveEvent event);
    void onWorldChange(PlayerChangedWorldEvent event);
}
