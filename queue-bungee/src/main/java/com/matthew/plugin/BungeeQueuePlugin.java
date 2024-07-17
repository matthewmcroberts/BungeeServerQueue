package com.matthew.plugin;

import com.matthew.plugin.commands.MyCommand;
import com.matthew.plugin.modules.chat.MessageModule;
import com.matthew.plugin.modules.manager.ModuleManager;
import com.matthew.plugin.modules.queue.QueueModule;
import com.matthew.plugin.modules.server.ServerModule;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeQueuePlugin extends Plugin {

    private ModuleManager moduleManager;

    @Override
    public void onEnable() {

        getLogger().info("Registering module(s)");
        moduleManager = ModuleManager.getInstance();
        moduleManager.registerModule(new QueueModule())
                .registerModule(new ServerModule())
                .registerModule(new MessageModule(this));

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