package com.softawii.hermes;

import com.softawii.hermes.event.ServerInteractionsEvents;
import com.softawii.hermes.service.PlayerService;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Plugin(name = "Hermes", version = "1.0")
@Description(value = "A plugin to navigate, explore and locate your way around the world.")
@LoadOrder(value = PluginLoadOrder.POSTWORLD)
@Author(value= "yaansz")
@LogPrefix(value = "Hermes")
@ApiVersion(ApiVersion.Target.v1_20)
public class HermesPlugin extends JavaPlugin {

    private ServerInteractionsEvents serverInteractionsEvents;
    private AnnotationConfigApplicationContext context;

    @Override
    public void onEnable() {
        // Spring Loader
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        this.context = new AnnotationConfigApplicationContext();
        this.context.scan("com.softawii.hermes");
        this.context.refresh();

        // Variable Loading
        PlayerService playerService = this.context.getBean(PlayerService.class);

        this.serverInteractionsEvents = new ServerInteractionsEvents(this, playerService);
    }
    @Override
    public void onDisable() {
        this.serverInteractionsEvents.close();

        this.context.close();
    }


}