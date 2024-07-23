package com.matthew.plugin.listeners;

import com.matthew.plugin.modules.ModuleManager;
import com.matthew.plugin.modules.queue.QueueModule;
import com.matthew.plugin.modules.queue.events.PlayerPriorityQueueJoinEvent;
import com.matthew.plugin.modules.queue.priorityqueue.QueuedPlayer;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

@RequiredArgsConstructor
public class QueueListener implements Listener {

    private final Plugin plugin;
    private final QueueModule module = ModuleManager.getInstance().getRegisteredModule(QueueModule.class);

    /*
    TODO: make this display actionbar if enabled in config
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onQueueJoin(PlayerPriorityQueueJoinEvent e) {
        ProxiedPlayer player = e.getPlayer();
        player.sendMessage(new TextComponent("Queue join event fired"));
    }
/*
    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLogin(PreLoginEvent event) {
        // Example logic for PreLoginEvent
        // You could use this event to perform any pre-login checks or preparations if needed
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        // Example logic for PostLoginEvent
        // Automatically add player to a default queue or based on some logic
        module.addPlayer("default_server", player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerConnect(ServerConnectEvent event) throws InterruptedException {
        ProxiedPlayer player = event.getPlayer();
        String serverName = event.getTarget().getName();

        // Check if the player is in the queue for the target server
        QueuedPlayer queuedPlayer = module.find(serverName, player);
        if (queuedPlayer != null) {
            // If the player is in the queue, perform actions like reserving a spot or managing priorities
            // Remove the player from the queue after they successfully connect to the server
            module.getNextPlayer(serverName);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String serverName = event.getKickedFrom().getName();

        // Handle the case where a player is kicked from a server
        // Optionally, re-add the player to the queue or handle based on your queue logic
        module.addPlayer(serverName, player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDisconnect(ServerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        // Remove the player from all queues they might be in
    }
 */
}
