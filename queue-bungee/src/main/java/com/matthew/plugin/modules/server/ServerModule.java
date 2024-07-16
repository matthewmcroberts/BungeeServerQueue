package com.matthew.plugin.modules.server;

import com.matthew.plugin.api.Module;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

public class ServerModule implements Module {

    @Getter
    public enum ServerStatus {
        ONLINE("ONLINE"),
        OFFLINE("OFFLINE");

        private final String text;

        ServerStatus(String text) {
            this.text = text;
        }
    }

    private ProxyServer proxy;

    public ServerStatus checkMainServerStatus() {
        //will implement after I implement config
        return ServerStatus.ONLINE;
    }

    public ServerStatus checkQueueServerStatus(String serverName) {
        //will implement after I implement config
        return ServerStatus.OFFLINE;
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
