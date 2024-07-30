package com.matthew.plugin.modules.messages;

import com.matthew.plugin.api.Module;
import com.matthew.plugin.modules.ModuleManager;
import com.matthew.plugin.modules.server.ServerModule;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class MessageModule implements Module {

    private final Plugin plugin;

    private Map<String, String> cache;

    private final ServerModule module = ModuleManager.getInstance().getRegisteredModule(ServerModule.class);

    public TextComponent buildMessage(String key) {
        return this.buildMessage(key, (Object) null);
    }

    public TextComponent buildMessage(String key, Object... args) {
        String message = cache.get(key);

        if (message == null) {
            plugin.getLogger().warning("Message key '" + key + "' not found");
            return new TextComponent();
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        if ((args != null) && (args.length > 0)) {
            for (int i = 0; i < args.length; i++) {
                message = message.replace("<" + i + ">", String.valueOf(args[i]));
            }
        }
        return new TextComponent(message);
    }

    //Hmm... starting to realize this might be overkill for just checking a quick socket connection
    public void buildServerListMessage(List<String> servers, Consumer<TextComponent> callback) {
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (String server : servers) {
            CompletableFuture<String> future = new CompletableFuture<>();
            module.checkQueueServerStatus(server, status -> {
                String serverStatus = server + " - " + status.getText();
                future.complete(serverStatus);
            });
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allOf.thenRun(() -> {
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 0; i < futures.size(); i++) {
                try {
                    messageBuilder.append(futures.get(i).get());
                    if (i < futures.size() - 1) {
                        messageBuilder.append("\n");
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Error completing all futures: " + e.getMessage());
                }
            }

            String message = ChatColor.translateAlternateColorCodes('&', messageBuilder.toString());

            // Ensure callback runs on the main thread
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> callback.accept(new TextComponent(message)), 0, TimeUnit.SECONDS);
        });
    }



    @Override
    public void setUp() {

        File configFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!configFile.exists()) {
            try (InputStream inputStream = plugin.getResourceAsStream("messages.yml")) {
                if (inputStream != null) {
                    Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    plugin.getLogger().info("Copied messages.yml from resources to plugin folder.");
                } else {
                    plugin.getLogger().warning("Default messages.yml not found in resources.");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Error copying messages.yml from resources: " + e.getMessage());
            }
        }

        try {
            Yaml yaml = new Yaml();
            cache = yaml.load(Files.newBufferedReader(configFile.toPath()));
        } catch (IOException e) {
            plugin.getLogger().warning("Error loading messages.yml: " + e.getMessage());
        }

        if (cache != null) {
            plugin.getLogger().info("Loaded messages from messages.yml: " + cache.keySet());
        }
    }

    @Override
    public void teardown() {

    }
}
