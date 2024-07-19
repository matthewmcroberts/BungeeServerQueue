package com.matthew.plugin.modules.queue.priorityqueue;

import com.matthew.plugin.api.PriorityQueue;
import lombok.NonNull;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

public class PlayerPriorityBlockingQueue implements PriorityQueue {

    private final PriorityBlockingQueue<QueuedPlayer> queue;

    public PlayerPriorityBlockingQueue() {
        queue = new PriorityBlockingQueue<>();
    }

    @Override
    public void addPlayer(@NonNull ProxiedPlayer player) {
        queue.put(new QueuedPlayer(player));
    }

    @Override
    public QueuedPlayer takeNextPlayer() throws InterruptedException {
        return queue.take();
    }

    @Override
    public QueuedPlayer getNextPlayer() throws InterruptedException {
        return queue.peek();
    }

    @Override
    public int getSize() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public int getPlayerPosition(@NonNull ProxiedPlayer player) {

        //Using snapshot rather than dealing with synchronization overhead
        QueuedPlayer[] snapshot = queue.toArray(new QueuedPlayer[0]);
        for (int i = 0; i < snapshot.length; i++) {
            if (snapshot[i].getPlayer().equals(player)) {
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public QueuedPlayer find(@NonNull ProxiedPlayer player) {
        for (QueuedPlayer queuedPlayer : queue) {
            if (queuedPlayer.getPlayer().equals(player)) {
                return queuedPlayer;
            }
        }
        return null;
    }

    @Override
    public boolean removePlayer(@NonNull ProxiedPlayer player) {
        Iterator<QueuedPlayer> iterator = queue.iterator();
        while (iterator.hasNext()) {
            QueuedPlayer queuedPlayer = iterator.next();
            if (queuedPlayer.getPlayer().equals(player)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    /*
    This will reevaluate the player's current permissions and determine if a priority node has changed
     */
    @Override
    public boolean updatePlayerPriority(@NonNull ProxiedPlayer player) {
        boolean removed = removePlayer(player);
        if (removed) {
            queue.put(new QueuedPlayer(player));
            return true;
        }
        return false;
    }
}
