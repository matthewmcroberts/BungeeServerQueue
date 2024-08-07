package com.matthew.plugin.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Utility class for handling plugin messaging between BungeeCord and Bukkit.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChannelMessaging implements Listener {

    private static Plugin plugin;

    @Getter
    private static final ChannelMessaging instance = new ChannelMessaging();

    private final ConcurrentMap<String, CompletableFuture<Boolean>> permissionFutures = new ConcurrentHashMap<>();

    /**
     * Registers the plugin instance and listener.
     *
     * @param plugin The plugin instance to register.
     */
    public void register(Plugin plugin) {
        ChannelMessaging.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, instance);
    }

    /**
     * Requests permission check from the server.
     *
     * @param player     The player for whom the permission is being checked.
     * @param permission The permission node to check.
     * @return A CompletableFuture that will complete with the result of the permission check.
     */
    public CompletableFuture<Boolean> hasPermission(final ProxiedPlayer player, final String permission) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        String key = player.getUniqueId().toString() + ":" + permission;
        permissionFutures.put(key, future);

        try (ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(b)) {

            out.writeUTF("CheckPermission");
            out.writeUTF(player.getName());
            out.writeUTF(permission);

            if (player.getServer() != null) {
                player.getServer().sendData("bungee:queue", b.toByteArray());
            } else {
                plugin.getLogger().severe("Player is not connected to a server: " + player.getName());
                future.completeExceptionally(new IllegalStateException("Player is not connected to a server."));
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error while sending data: " + e.getMessage());
            future.completeExceptionally(e);
        }

        return future;
    }

    @EventHandler
    public void onPluginMessage(final PluginMessageEvent event) {
        if (!event.getTag().equals("bungee:queue")) {
            return;
        }
        
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()))) {
            String subChannel = in.readUTF();

            if ("PermissionResponse".equals(subChannel)) {
                String playerName = in.readUTF();
                String permission = in.readUTF();
                boolean hasPermission = in.readBoolean();

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
                if (player != null) {
                    String key = player.getUniqueId().toString() + ":" + permission;
                    CompletableFuture<Boolean> future = permissionFutures.remove(key);

                    if (future != null && !future.isDone()) {
                        future.complete(hasPermission);
                    }
                } else {
                    plugin.getLogger().warning("Player not found: " + playerName);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error processing plugin message: " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().severe("Unexpected error processing plugin message: " + e.getMessage());
        }
    }
}

