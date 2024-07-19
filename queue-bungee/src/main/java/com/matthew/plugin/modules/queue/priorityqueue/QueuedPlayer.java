package com.matthew.plugin.modules.queue.priorityqueue;

import com.matthew.plugin.modules.ModuleManager;
import com.matthew.plugin.modules.settings.SettingsConstants;
import com.matthew.plugin.modules.settings.SettingsModule;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Optional;

@Getter
public class QueuedPlayer implements Comparable<QueuedPlayer> {

    private final SettingsModule settings = ModuleManager.getInstance().getRegisteredModule(SettingsModule.class);

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
        Optional<String> priorityHigh = settings.getString(SettingsConstants.PERMISSION_PRIORITY_HIGH_NODE);
        Optional<String> priorityMedium = settings.getString(SettingsConstants.PERMISSION_PRIORITY_MEDIUM_NODE);

        if(priorityHigh.isPresent() && player.hasPermission(priorityHigh.get())) {
            return 1;
        } else if(priorityMedium.isPresent() && player.hasPermission(priorityMedium.get())) {
            return 2;
        }
        return 3; // Normal priority
    }
}

