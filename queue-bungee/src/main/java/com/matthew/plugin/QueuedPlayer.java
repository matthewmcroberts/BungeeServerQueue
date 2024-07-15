package com.matthew.plugin;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class QueuedPlayer implements Comparable<QueuedPlayer> {

    private final ProxiedPlayer player;
    private final int priority;

    public QueuedPlayer(ProxiedPlayer player) {
        this.player = player;
        this.priority = 0;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(QueuedPlayer other) {
        // Higher priority players should be served first, hence the negative comparison
        return Integer.compare(other.priority, this.priority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QueuedPlayer that = (QueuedPlayer) o;

        return player.equals(that.player);
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }
}

