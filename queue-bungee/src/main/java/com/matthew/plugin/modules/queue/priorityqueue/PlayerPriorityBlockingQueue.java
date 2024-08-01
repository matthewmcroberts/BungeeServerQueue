package com.matthew.plugin.modules.queue.priorityqueue;

import com.matthew.plugin.api.PriorityQueue;
import lombok.NonNull;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;


public class PlayerPriorityBlockingQueue implements PriorityQueue {

    private final PriorityBlockingQueue<QueuedPlayer> queue;

    // Set to efficiently check for duplicate player entries
    private final Set<String> playerIds;

    public PlayerPriorityBlockingQueue() {
        queue = new PriorityBlockingQueue<>();
        playerIds = new HashSet<>();
    }

    /**
     * Adds a player to the queue if they are not already present.
     *
     * @param player The player to be added.
     */
    @Override
    public void addPlayer(@NonNull ProxiedPlayer player) {
        synchronized (playerIds) {
            // Add player ID to the set and add to the queue if successful
            if (playerIds.add(player.getUniqueId().toString())) {
                queue.put(new QueuedPlayer(player));
            }
        }
    }

    /**
     * Retrieves and removes the next player from the queue, blocking if necessary until a player is available.
     *
     * @return The next player in the queue.
     * @throws InterruptedException If interrupted while waiting.
     */
    @Override
    public QueuedPlayer takeNextPlayer() throws InterruptedException {
        QueuedPlayer player = queue.take();
        synchronized (playerIds) {
            // Remove player ID from the set
            playerIds.remove(player.getPlayer().getUniqueId().toString());
        }
        return player;
    }

    /**
     * Retrieves, but does not remove, the next player from the queue.
     *
     * @return The next player in the queue, or null if the queue is empty.
     */
    @Override
    @Nullable
    public QueuedPlayer getNextPlayer() {
        return queue.peek();
    }

    /**
     * Returns the number of players in the queue.
     *
     * @return The size of the queue.
     */
    @Override
    public int getSize() {
        return queue.size();
    }

    /**
     * Checks if the queue is empty.
     *
     * @return True if the queue is empty, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Returns the position of a player in the queue.
     *
     * @param player The player to find.
     * @return The 1-based position of the player in the queue, or -1 if the player is not in the queue.
     */
    @Override
    public int getPlayerPosition(@NonNull ProxiedPlayer player) {
        QueuedPlayer[] snapshot = queue.toArray(new QueuedPlayer[0]);
        for (int i = 0; i < snapshot.length; i++) {
            if (snapshot[i].getPlayer().equals(player)) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Finds and returns a player in the queue.
     *
     * @param player The player to find.
     * @return The queued player if found, or null if the player is not in the queue.
     */
    @Override
    @Nullable
    public QueuedPlayer find(@NonNull ProxiedPlayer player) {
        for (QueuedPlayer queuedPlayer : queue) {
            if (queuedPlayer.getPlayer().equals(player)) {
                return queuedPlayer;
            }
        }
        return null;
    }

    /**
     * Removes a player from the queue.
     *
     * @param player The player to be removed.
     * @return True if the player was removed, false otherwise.
     */
    @Override
    public boolean removePlayer(@NonNull ProxiedPlayer player) {
        synchronized (playerIds) {
            QueuedPlayer target = null;
            for (QueuedPlayer queuedPlayer : queue) {
                if (queuedPlayer.getPlayer().equals(player)) {
                    target = queuedPlayer;
                    break;
                }
            }
            if (target != null) {
                playerIds.remove(player.getUniqueId().toString());
                return queue.remove(target);
            }
        }
        return false;
    }

    /**
     * Updates the priority of a player by removing and re-adding them to the queue.
     *
     * @param player The player whose priority is to be updated.
     * @return True if the priority was updated, false otherwise.
     */
    @Override
    public boolean updatePlayerPriority(@NonNull ProxiedPlayer player) {
        synchronized (playerIds) {
            if (removePlayer(player)) {
                addPlayer(player);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the queue contains a given player.
     *
     * @param player The player to check for.
     * @return True if the player is in the queue, false otherwise.
     */
    private boolean containsPlayer(@NonNull ProxiedPlayer player) {
        synchronized (playerIds) {
            return playerIds.contains(player.getUniqueId().toString());
        }
    }
}
