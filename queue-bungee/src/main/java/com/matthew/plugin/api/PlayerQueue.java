package com.matthew.plugin.api;

import com.matthew.plugin.queue.QueuedPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface PlayerQueue {

    void addPlayer(ProxiedPlayer player);

    QueuedPlayer getNextPlayer() throws InterruptedException;

    int getSize();

    boolean isEmpty();

    int getPlayerPosition(ProxiedPlayer player);

    QueuedPlayer find(ProxiedPlayer player);
}
