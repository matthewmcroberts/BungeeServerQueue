package com.matthew.plugin.queue;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Getter
public class QueuedPlayer implements Comparable<QueuedPlayer> {

    private final ProxiedPlayer player;
    private final int priority;

    public QueuedPlayer(ProxiedPlayer player) {
        this.player = player;
        this.priority = calculatePriority(player);
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

    private int calculatePriority(ProxiedPlayer player) {
        if(player.hasPermission("queue.priority.highest")) {
            return 1;
        } else if(player.hasPermission("queue.priority.medium")) {
            return 2;
        }
        return 3; // Normal priority
    }
}

