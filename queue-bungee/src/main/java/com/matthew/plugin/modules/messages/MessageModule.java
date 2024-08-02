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

    /**
     * Builds a message from the cache based on the provided key.
     *
     * @param key the key to retrieve the message from the cache.
     * @return the constructed TextComponent message.
     */
    public TextComponent buildMessage(String key) {
        return this.buildMessage(key, (Object) null);
    }

    /**
     * Builds a message from the cache based on the provided key and formats it with provided arguments.
     *
     * @param key the key to retrieve the message from the cache.
     * @param args the arguments to format the message.
     * @return the constructed TextComponent message.
     */
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

    /**
     * Builds a message from the cache and executes the provided callback with the result.
     *
     * @param key the key to retrieve the message from the cache.
     * @param callback the callback to execute with the constructed message.
     */
    public void buildThenRunMessage(String key, Consumer<TextComponent> callback) {
        callback.accept(this.buildMessage(key, (Object) null));
    }

    /**
     * Builds a message from the cache, formats it with provided arguments, and executes the provided callback with the result.
     *
     * @param key the key to retrieve the message from the cache.
     * @param callback the callback to execute with the constructed message.
     * @param args the arguments to format the message.
     */
    public void buildThenRunMessage(String key, Consumer<TextComponent> callback, Object... args) {
        callback.accept(this.buildMessage(key, args));
    }

    /**
     * Builds a message containing the status of all available servers and executes the provided callback with the result.
     * The server statuses are checked asynchronously.
     *
     * @param callback the callback to execute with the constructed message.
     */
    public void buildThenRunServerListMessage(Consumer<TextComponent> callback) {
        List<CompletableFuture<TextComponent>> futures = new ArrayList<>();
        TextComponent headerMessage = buildMessage("serverstatusheader");

        for (String server : module.getAvailableServers()) {
            CompletableFuture<TextComponent> future = new CompletableFuture<>();
            module.checkQueueServerStatus(server, status -> {
                TextComponent serverStatusMessage = buildMessage("serverstatus", server, status);
                future.complete(serverStatusMessage);
            });
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allOf.thenRun(() -> {
            TextComponent finalMessage = new TextComponent(headerMessage);

            for (int i = 0; i < futures.size(); i++) {
                try {
                    TextComponent serverStatusMessage = futures.get(i).get();
                    finalMessage.addExtra(serverStatusMessage);
                    if (i < futures.size() - 1) {
                        finalMessage.addExtra("\n");
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Error completing all futures: " + e.getMessage());
                }
            }

            // Ensure callback runs on the main thread because in our case we are using it to send a message to the player
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> callback.accept(finalMessage), 0, TimeUnit.SECONDS);
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
        // No teardown actions needed
    }
}
