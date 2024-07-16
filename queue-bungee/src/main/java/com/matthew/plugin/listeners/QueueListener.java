package com.matthew.plugin.listeners;

import com.matthew.plugin.modules.manager.ServerModuleManager;
import com.matthew.plugin.modules.queue.QueueModule;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class QueueListener implements Listener {

    private final Plugin plugin;

    private final QueueModule module = ServerModuleManager.getInstance().getRegisteredModule(QueueModule.class);

    @EventHandler
    public void onConnect(ServerConnectEvent event) {

    }
}
