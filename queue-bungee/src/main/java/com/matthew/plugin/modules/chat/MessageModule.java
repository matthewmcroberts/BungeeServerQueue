package com.matthew.plugin.modules.chat;

import com.matthew.plugin.api.Module;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@RequiredArgsConstructor
public class MessageModule implements Module {

    private final Plugin plugin;

    private Map<String, String> cache;

    public String buildMessage(String key, Object... args) {
        String message = cache.get(key);

        if (message == null) {
            plugin.getLogger().warning("Message key '" + key + "' not found");
            return "";
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        if ((args != null) && (args.length > 0)) {
            for (int i = 0; i < args.length; i++) {
                message = message.replace("<" + i + ">", String.valueOf(args[i]));
            }
        }
        return message;
    }

    @Override
    public void setUp() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

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
            plugin.getLogger().info("Test message: " + cache.get("messages.test"));
        }
    }

    @Override
    public void teardown() {

    }
}
