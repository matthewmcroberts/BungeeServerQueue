package com.matthew.plugin.queue;

import com.matthew.plugin.api.PlayerQueue;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Optional;


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

    @Override
    public QueuedPlayer find(ProxiedPlayer player) {
        Optional<QueuedPlayer> result = queue.stream()
                .filter(queuedPlayer -> queuedPlayer.getPlayer().equals(player))
                .findFirst();

        return result.orElse(null); // Return null if the player is not found in the queue
    }
}
