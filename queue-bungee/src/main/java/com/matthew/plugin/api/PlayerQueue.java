package com.matthew.plugin.api;

import com.matthew.plugin.queue.QueuedPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Will be useful once Non-Priority and Non-Blocking queues are implemented
 * Definitely going to need to make some adjustments though as this currently is tightly coupled with QueuedPlayer
 * Current idea is to implement a wrapper
 */
public interface PlayerQueue {

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
}

