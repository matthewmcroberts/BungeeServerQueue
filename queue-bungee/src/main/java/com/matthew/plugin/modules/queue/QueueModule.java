package com.matthew.plugin.modules.queue;

import com.matthew.plugin.queue.PlayerPriorityBlockingQueue;
import com.matthew.plugin.queue.QueuedPlayer;
import com.matthew.plugin.api.Module;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;

public class QueueModule implements Module {

    //Allocated servers are not released, therefore WeakHashMap will not work for time being.
    private final Map<String, PlayerPriorityBlockingQueue> queues = new HashMap<>();

    public void addPlayer(String serverName, ProxiedPlayer player) {
        queues.computeIfAbsent(serverName, k -> new PlayerPriorityBlockingQueue()).addPlayer(player);
    }

    public QueuedPlayer getNextPlayer(String serverName) throws InterruptedException {
        PlayerPriorityBlockingQueue queue = queues.get(serverName);
        return queue != null ? queue.takeNextPlayer() : null;
    }

    public int getSize(String serverName) {
        PlayerPriorityBlockingQueue queue = queues.get(serverName);
        return queue != null ? queue.getSize() : -1;
    }

    public boolean isEmpty(String serverName) {
        PlayerPriorityBlockingQueue queue = queues.get(serverName);
        return (queue == null) || queue.isEmpty();
    }

    public int getPlayerPosition(String serverName, ProxiedPlayer player) {
        PlayerPriorityBlockingQueue queue = queues.get(serverName);
        return queue != null ? queue.getPlayerPosition(player) : -1;
    }

    public QueuedPlayer find(String serverName, ProxiedPlayer player) {
        PlayerPriorityBlockingQueue queue = queues.get(serverName);
        return queue != null ? queue.find(player) : null;
    }

    public boolean updatePlayerPriority (String serverName, ProxiedPlayer player) {
        PlayerPriorityBlockingQueue queue = queues.get(serverName);
        return queue != null && queue.updatePlayerPriority(player);
    }

    public boolean removePlayer(String serverName, ProxiedPlayer player) {
        PlayerPriorityBlockingQueue queue = queues.get(serverName);
        return queue != null && queue.removePlayer(player);
    }

    @Override
    public void setUp() {
        // no resources in need of allocation
    }

    @Override
    public void teardown() {
        // no allocated resources in need of teardown
    }
}
