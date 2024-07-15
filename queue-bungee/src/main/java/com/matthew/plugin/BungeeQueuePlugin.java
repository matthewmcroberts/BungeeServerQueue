package com.matthew.plugin;


import com.matthew.plugin.commands.MyCommand;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeQueuePlugin extends Plugin {

    @Override
    public void onEnable() {
        getLogger().info("BungeeQueuePlugin is enabled");
        getProxy().getPluginManager().registerCommand(this, new MyCommand());
    }

    @Override
    public void onDisable() {

    }
}