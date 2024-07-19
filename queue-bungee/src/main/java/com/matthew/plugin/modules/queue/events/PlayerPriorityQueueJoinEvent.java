package com.matthew.plugin.modules.queue.events;

import com.matthew.plugin.modules.ModuleManager;
import com.matthew.plugin.modules.queue.QueueModule;
import com.matthew.plugin.modules.queue.priorityqueue.PlayerPriorityBlockingQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

@Getter
@RequiredArgsConstructor
public class PlayerPriorityQueueJoinEvent extends Event implements Cancellable {

    private final ProxiedPlayer player;

    //The queue they are currently in
    private final String serverName;

    private boolean cancelled;

    private final QueueModule module = ModuleManager.getInstance().getRegisteredModule(QueueModule.class);

    public PlayerPriorityBlockingQueue getQueue() {
        return module.getQueue(player);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
