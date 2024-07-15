package com.matthew.plugin;


import com.matthew.plugin.api.PlayerQueue;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerPriorityBlockingQueue implements PlayerQueue {

    private final PriorityBlockingQueue<QueuedPlayer> queue;

    public PlayerPriorityBlockingQueue() {
        this.queue = new PriorityBlockingQueue<>();
    }

    public void addPlayer(ProxiedPlayer player) {
        queue.put(new QueuedPlayer(player));
    }

    public QueuedPlayer getNextPlayer() throws InterruptedException {
        return queue.take();
    }

    public int getQueueSize() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getPlayerPosition(ProxiedPlayer player) {
        AtomicInteger position = new AtomicInteger();

        int playerPosition = queue.stream()
                .sorted() // Ensure the stream is sorted according to priority
                .map(QueuedPlayer::getPlayer)
                .peek(p -> {
                    if (p.equals(player)) {
                        position.set(position.get() + 1);
                    }
                    position.incrementAndGet();
                })
                .toList()
                .indexOf(player);

        // Adjust to 1-based index or handle not found case
        return playerPosition == -1 ? -1 : playerPosition + 1;
    }
}
