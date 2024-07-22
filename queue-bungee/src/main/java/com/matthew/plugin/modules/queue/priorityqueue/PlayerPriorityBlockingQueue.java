package com.matthew.plugin.modules.queue.priorityqueue;

import com.matthew.plugin.api.PriorityQueue;
import lombok.NonNull;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * TODO: NEED TO DO DUPLICATE ENTRIES CHECKS. BEST TO USE A SET OF UUIDS OR STRINGS OR SOMETHING RATHER THAN RUNNING FIND()
 */

public class PlayerPriorityBlockingQueue implements PriorityQueue {

    private final PriorityBlockingQueue<QueuedPlayer> queue;

    public PlayerPriorityBlockingQueue() {
        queue = new PriorityBlockingQueue<>();
    }

    @Override
    public void addPlayer(@NonNull ProxiedPlayer player) {
        synchronized (this) {
            if (!containsPlayer(player)) {
                queue.put(new QueuedPlayer(player));
            }
        }
    }

    @Override
    public QueuedPlayer takeNextPlayer() throws InterruptedException {
        return queue.take();
    }

    @Override
    public QueuedPlayer getNextPlayer() {
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
        synchronized (this) {
            QueuedPlayer[] snapshot = queue.toArray(new QueuedPlayer[0]);
            for (int i = 0; i < snapshot.length; i++) {
                if (snapshot[i].getPlayer().equals(player)) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    @Override
    public QueuedPlayer find(@NonNull ProxiedPlayer player) {
        synchronized (this) {
            for (QueuedPlayer queuedPlayer : queue) {
                if (queuedPlayer.getPlayer().equals(player)) {
                    return queuedPlayer;
                }
            }
        }
        return null;
    }

    @Override
    public boolean removePlayer(@NonNull ProxiedPlayer player) {
        synchronized (this) {
            QueuedPlayer target = null;
            for (QueuedPlayer queuedPlayer : queue) {
                if (queuedPlayer.getPlayer().equals(player)) {
                    target = queuedPlayer;
                    break;
                }
            }
            if (target != null) {
                return queue.remove(target);
            }
        }
        return false;
    }

    @Override
    public boolean updatePlayerPriority(@NonNull ProxiedPlayer player) {
        synchronized (this) {
            boolean removed = removePlayer(player);
            if (removed) {
                queue.put(new QueuedPlayer(player));
                return true;
            }
        }
        return false;
    }

    private boolean containsPlayer(@NonNull ProxiedPlayer player) {
        synchronized (this) {
            for (QueuedPlayer queuedPlayer : queue) {
                if (queuedPlayer.getPlayer().equals(player)) {
                    return true;
                }
            }
        }
        return false;
    }
}
