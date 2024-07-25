package com.matthew.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class BungeeQueueBukkitPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("BungeeQueueBukkitPlugin started");
        Objects.requireNonNull(getCommand("test")).setExecutor(new TestCommand());
    }

    @Override
    public void onDisable() {}
}