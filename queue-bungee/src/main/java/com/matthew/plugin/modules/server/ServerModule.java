package com.matthew.plugin.modules.server;

import com.matthew.plugin.api.Module;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerModule implements Module {

    @Getter
    public enum ServerStatus {
        ONLINE("ONLINE"),
        OFFLINE("OFFLINE"),
        EXISTS("EXISTS"),
        NOT_FOUND("NOT FOUND");

        private final String text;

        ServerStatus(String text) {
            this.text = text;
        }
    }

    private ProxyServer proxy;

    public ServerStatus checkMainServerStatus() {
        //will implement after I implement config settings that explicitly states the main server name
        return ServerStatus.ONLINE;
    }

    public ServerStatus checkQueueServerStatus(@NonNull final String serverName) {
        ServerStatus existenceStatus = checkIfExists(serverName);
        if (existenceStatus == ServerStatus.NOT_FOUND) {
            proxy.getLogger().warning("'" + serverName + "' not found. Please check your bungee configuration.");
            return ServerStatus.NOT_FOUND;
        }

        try {
            ServerInfo serverInfo = proxy.getServerInfo(serverName);
            InetSocketAddress address = (InetSocketAddress) serverInfo.getSocketAddress();
            try (Socket socket = new Socket()) {
                socket.connect(address, 1000); // Will throw exception if not reachable
                return ServerStatus.ONLINE;
            }
        } catch (Exception e) {
            proxy.getLogger().warning("'" + serverName + "' is offline: " + e.getMessage());
            return ServerStatus.OFFLINE;
        }
    }

    public ServerStatus checkIfExists(@NonNull final String serverName) {
        return proxy.getServerInfo(serverName) == null ? ServerStatus.NOT_FOUND : ServerStatus.EXISTS;
    }

    @Override
    public void setUp() {
        proxy = ProxyServer.getInstance();
    }

    @Override
    public void teardown() {
        // No allocated resources in need of being released
    }
}
