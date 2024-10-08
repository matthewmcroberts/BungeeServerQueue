package com.matthew.plugin.modules.server;

import com.matthew.plugin.api.Module;
import com.matthew.plugin.exceptions.MainServerNotConfiguredException;
import com.matthew.plugin.modules.ModuleManager;
import com.matthew.plugin.modules.settings.SettingsConstants;
import com.matthew.plugin.modules.settings.SettingsModule;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
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

    private final Plugin plugin;

    private final SettingsModule settings = ModuleManager.getInstance().getRegisteredModule(SettingsModule.class);

    /*
    public ServerStatus checkMainServerStatus() {
        final Optional<String> MAIN_SERVER_OPTIONAL = settings.getString(SettingsConstants.CONFIG_MAIN_SERVER);

        if (MAIN_SERVER_OPTIONAL.isEmpty()) {
            throw new MainServerNotConfiguredException("Main server is not configured. Please check your settings.");
        }

        return checkServerStatus(MAIN_SERVER_OPTIONAL.get());
    }
     */

    public void checkQueueServerStatus(@NonNull final String serverName, @NonNull Consumer<String> callback) {
        executor.submit(() -> {
            ServerStatus status = checkServerStatus(serverName);

            /*
            For now, run the callback function on the main thread just in case in the future, if thread-safety
            is required, the acceptance of the callback function is already thread-safe
            If I didn't implement it this way then it would be possible for the background threads, (executor's threads)
            to manipulate data relating to the server or bungee api, rather than a thread related to the bungeecord
            scheduler.
            */
             proxy.getScheduler().schedule(plugin, () -> callback.accept(status.getText()), 0, TimeUnit.SECONDS);
        });
    }

    public ServerStatus checkIfExists(@NonNull final String serverName) {
        return proxy.getServerInfo(serverName) == null ? ServerStatus.NOT_FOUND : ServerStatus.EXISTS;
    }

    public List<String> getAvailableServers() {
        return settings.getAvailableServers();
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
                socket.connect(address, 1000);
                return ServerStatus.ONLINE;
            }
        } catch (Exception e) {
            proxy.getLogger().warning("'" + serverName + "' is offline: " + e.getMessage());
            return ServerStatus.OFFLINE;
        }
    }
}
