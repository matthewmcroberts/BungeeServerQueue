package com.matthew.plugin;


import com.matthew.plugin.commands.MyCommand;
import com.matthew.plugin.modules.manager.ServerModuleManager;
import com.matthew.plugin.modules.queue.QueueModule;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeQueuePlugin extends Plugin {

    private ServerModuleManager moduleManager;

    @Override
    public void onEnable() {

        getLogger().info("Registering module(s)");
        moduleManager = ServerModuleManager.getInstance();
        moduleManager.registerModule(new QueueModule());

        getLogger().info("Setting up module(s)");
        moduleManager.setUp();

        getLogger().info("Registering command(s)");
        getProxy().getPluginManager().registerCommand(this, new MyCommand());

        getLogger().info("BungeeQueuePlugin is enabled");
    }

    @Override
    public void onDisable() {
        moduleManager.teardown();
    }
}