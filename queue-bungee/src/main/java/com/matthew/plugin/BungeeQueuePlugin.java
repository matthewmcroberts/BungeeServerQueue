package com.matthew.plugin;

import com.matthew.plugin.commands.MyCommand;
import com.matthew.plugin.listeners.QueueListener;
import com.matthew.plugin.modules.messages.MessageModule;
import com.matthew.plugin.modules.ModuleManager;
import com.matthew.plugin.modules.queue.QueueModule;
import com.matthew.plugin.modules.server.ServerModule;
import com.matthew.plugin.modules.settings.SettingsModule;
import com.matthew.plugin.util.ChannelMessaging;
import net.md_5.bungee.api.plugin.Plugin;


public class BungeeQueuePlugin extends Plugin {

    private ModuleManager moduleManager;

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        getLogger().info("Registering module(s)");
        moduleManager = ModuleManager.getInstance();
        moduleManager.registerModule(new QueueModule())
                .registerModule(new ServerModule())
                .registerModule(new MessageModule(this))
                .registerModule(new SettingsModule(this));

        getLogger().info("Setting up module(s)");
        moduleManager.setUp();

        getLogger().info("Registering listener(s)");
        getProxy().getPluginManager().registerListener(this, new QueueListener(this));

        getLogger().info("Registering channel messaging");
        getProxy().registerChannel("BungeeCord");
        getProxy().registerChannel("bungee:queue");
        ChannelMessaging.getInstance().register(this);

        getLogger().info("Registering command(s)");
        getProxy().getPluginManager().registerCommand(this, new MyCommand(this));

        getLogger().info("BungeeQueuePlugin is enabled");
    }

    @Override
    public void onDisable() {
        moduleManager.teardown();
    }
}