package com.matthew.plugin;


import com.matthew.plugin.commands.MyCommand;
import com.matthew.plugin.modules.i18n.I18nModule;
import com.matthew.plugin.modules.i18n.UTF8ResourceBundleControl;
import com.matthew.plugin.modules.manager.ModuleManager;
import com.matthew.plugin.modules.queue.QueueModule;
import com.matthew.plugin.modules.server.ServerModule;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Locale;
import java.util.ResourceBundle;

public class BungeeQueuePlugin extends Plugin {

    private ModuleManager moduleManager;

    @Override
    public void onEnable() {

        final String BASE_NAME = "i18n.BungeeQueue";

        getLogger().info("Registering module(s)");
        moduleManager = ModuleManager.getInstance();
        moduleManager.registerModule(new QueueModule())
                .registerModule(new ServerModule())
                .registerModule(new I18nModule(BASE_NAME));

        getLogger().info("Adding locale(s)");
        moduleManager.getRegisteredModule(I18nModule.class).addTranslation(Locale.ENGLISH,
                ResourceBundle.getBundle(BASE_NAME, Locale.ENGLISH, UTF8ResourceBundleControl.get()), true);

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