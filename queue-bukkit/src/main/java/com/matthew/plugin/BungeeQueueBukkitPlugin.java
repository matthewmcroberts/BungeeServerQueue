package com.matthew.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;

public class BungeeQueueBukkitPlugin extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeQueue", this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeQueue");
        Bukkit.getLogger().info("BungeeQueueBukkitPlugin started");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeePerms")) {
            return;
        }

        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subChannel = in.readUTF();
            if (subChannel.equals("CheckPermission")) {
                String playerName = in.readUTF();
                String permission = in.readUTF();
                Player targetPlayer = Bukkit.getPlayer(playerName);
                boolean hasPermission = targetPlayer != null && targetPlayer.hasPermission(permission);

                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("PermissionResponse");
                out.writeBoolean(hasPermission);
                player.sendPluginMessage(this, "BungeeQueue", b.toByteArray());
            }
        } catch (IOException e) {
            getLogger().severe(e.getMessage());
        }
    }
}