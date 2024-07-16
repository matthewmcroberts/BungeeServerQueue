package com.matthew.plugin.queue;

import com.matthew.plugin.api.PlayerQueue;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.PriorityBlockingQueue;


public class PlayerPriorityBlockingQueue implements PlayerQueue {

    private final PriorityBlockingQueue<QueuedPlayer> queue;

    public PlayerPriorityBlockingQueue() {
        this.queue = new PriorityBlockingQueue<>();
    }

    @Override
    public void addPlayer(ProxiedPlayer player) {
        queue.put(new QueuedPlayer(player));
    }

    @Override
    public QueuedPlayer getNextPlayer() throws InterruptedException {
        return queue.take();
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
    public int getPlayerPosition(ProxiedPlayer player) {
        synchronized (queue) {
            int position = 1;
            for (QueuedPlayer queuedPlayer : queue) {
                if (queuedPlayer.getPlayer().equals(player)) {
                    return position;
                }
                position++;
            }
        }
        return -1;
    }

    @Override
    public QueuedPlayer find(ProxiedPlayer player) {
        synchronized (queue) {
            for (QueuedPlayer queuedPlayer : queue) {
                if (queuedPlayer.getPlayer().equals(player)) {
                    return queuedPlayer;
                }
            }
        }
        return null;
    }
}
