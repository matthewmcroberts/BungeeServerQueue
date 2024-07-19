package com.matthew.plugin.api;

import com.matthew.plugin.modules.queue.priorityqueue.QueuedPlayer;
import lombok.NonNull;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Will be useful once Non-Blocking queues are implemented
 */
public interface PriorityQueue {

    /**
     * Adds a player to the queue.
     *
     * @param player The player to be added to the queue.
     */
    void addPlayer(ProxiedPlayer player);

    /**
     * Retrieves and removes the next player from the queue, waiting if necessary
     * until an element becomes available.
     *
     * @return The next player in the queue.
     * @throws InterruptedException if interrupted while waiting.
     */
    QueuedPlayer takeNextPlayer() throws InterruptedException;

    /**
     * Retrieves, but does not remove, the next player from the queue,
     * waiting if necessary until an element becomes available.
     *
     * @return The next player in the queue.
     * @throws InterruptedException if interrupted while waiting.
     */
    QueuedPlayer getNextPlayer() throws InterruptedException;

    /**
     * Returns the number of players currently in the queue.
     *
     * @return The size of the queue.
     */
    int getSize();

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Returns the position of a specific player in the queue.
     *
     * @param player The player whose position is to be determined.
     * @return The position of the player in the queue, or -1 if the player is not in the queue.
     */
    int getPlayerPosition(ProxiedPlayer player);

    /**
     * Finds and returns a specific player from the queue.
     *
     * @param player The player to be found.
     * @return The queued player if found, null otherwise.
     */
    QueuedPlayer find(ProxiedPlayer player);

    /**
     * Removes a specific player from the queue.
     *
     * @param player The player to be removed from the queue.
     * @return true if the player was successfully removed, false otherwise.
     */
    boolean removePlayer(@NonNull ProxiedPlayer player);

    /**
     * Re-evaluates the player's current permissions and determines if a priority node has changed.
     *
     * @param player The player whose priority is to be updated.
     * @return true if the player's priority was successfully updated, false otherwise.
     */
    boolean updatePlayerPriority(@NonNull ProxiedPlayer player);
}
