package com.matthew.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;

public class BungeeQueueBukkitPlugin extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        // Use a valid channel name with a ':' separator
        getServer().getMessenger().registerIncomingPluginChannel(this, "bungee:queue", this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bungee:queue");
        Bukkit.getLogger().info("BungeeQueueBukkitPlugin started");
    }

    @Override
    public void onDisable() {
        // Unregister channels to clean up
        getServer().getMessenger().unregisterIncomingPluginChannel(this, "bungee:queue");
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, "bungee:queue");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("bungee:queue")) {
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
                out.writeUTF(playerName);
                out.writeUTF(permission);
                out.writeBoolean(hasPermission);

                player.sendPluginMessage(this, "bungee:queue", b.toByteArray());
            }
        } catch (IOException e) {
            getLogger().severe("Error processing plugin message: " + e.getMessage());
        } catch (Exception e) {
            getLogger().severe("Unexpected error processing plugin message: " + e.getMessage());
        }
    }
}
