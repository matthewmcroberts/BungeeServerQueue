package com.matthew.plugin.modules.server;

import com.matthew.plugin.api.Module;
import com.matthew.plugin.exceptions.MainServerNotConfiguredException;
import com.matthew.plugin.modules.ModuleManager;
import com.matthew.plugin.modules.settings.SettingsConstants;
import com.matthew.plugin.modules.settings.SettingsModule;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.*;

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

    private ExecutorService executor;

    private final SettingsModule module = ModuleManager.getInstance().getRegisteredModule(SettingsModule.class);

    public CompletableFuture<ServerStatus> checkMainServerStatus() {
        return CompletableFuture.supplyAsync(() -> {
            final Optional<String> MAIN_SERVER_OPTIONAL = module.getString(SettingsConstants.CONFIG_MAIN_SERVER);

            if (MAIN_SERVER_OPTIONAL.isEmpty()) {
                throw new MainServerNotConfiguredException("Main server is not configured. Please check your settings.");
            }

            return checkServerStatus(MAIN_SERVER_OPTIONAL.get());
        }, executor);
    }

    public CompletableFuture<ServerStatus> checkQueueServerStatus(@NonNull final String serverName) {
        return CompletableFuture.supplyAsync(() -> checkServerStatus(serverName), executor);
    }

    public ServerStatus checkIfExists(@NonNull final String serverName) {
        return proxy.getServerInfo(serverName) == null ? ServerStatus.NOT_FOUND : ServerStatus.EXISTS;
    }

    @Override
    public void setUp() {
        proxy = ProxyServer.getInstance();

        executor = new ThreadPoolExecutor(
                10,
                50,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>()
        );
    }

    @Override
    public void teardown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();

                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    proxy.getLogger().severe("Executor service did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();

            Thread.currentThread().interrupt();
        }
    }

    private ServerStatus checkServerStatus(String serverName) {
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
}
