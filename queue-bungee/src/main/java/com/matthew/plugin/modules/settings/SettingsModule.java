package com.matthew.plugin.modules.settings;

import com.matthew.plugin.api.Module;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@RequiredArgsConstructor
public class SettingsModule implements Module {

    private final Plugin plugin;

    @Getter
    private List<String> availableServers;

    @Getter
    private Configuration config;

    public Optional<Integer> getSlots(String key) {
        key = key.toUpperCase();
        return getInteger(SettingsConstants.CONFIG_TARGET_SERVERS + "." + key + "." + SettingsConstants.CONFIG_MAX_SLOTS);
    }

    public Optional<String> getNameExact(String key) {
        key = key.toUpperCase();
        return getString(SettingsConstants.CONFIG_TARGET_SERVERS + "." + key + "." + SettingsConstants.CONFIG_SERVER_NAME);
    }

    public Optional<Boolean> getBoolean(final String path) {
        if (config.contains(path)) {
            return Optional.of(config.getBoolean(path));
        }
        return Optional.empty();
    }

    public Optional<Integer> getInteger(final String path) {
        if (config.contains(path)) {
            return Optional.of(config.getInt(path));
        }
        return Optional.empty();
    }

    public Optional<String> getString(final String path) {
        if (config.contains(path)) {
            return Optional.of(config.getString(path));
        }
        return Optional.empty();
    }

    @Override
    public void setUp() {
        final String PATH = "bungeeconfig.yml";
        File configFile = new File(plugin.getDataFolder(), PATH);

        if (!configFile.exists()) {
            try (InputStream in = this.getClass().getResourceAsStream("/" + PATH)) {
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                } else {
                    plugin.getLogger().log(Level.SEVERE, "Resource " + PATH + " not found in the plugin jar.");
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Unable to copy " + PATH + " to " + configFile.getAbsolutePath(), e);
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to load configuration file: " + configFile.getAbsolutePath(), e);
        }

        final List<String> serverKeys = config.getSection(SettingsConstants.CONFIG_TARGET_SERVERS).getKeys().stream().toList();
        for(String key : serverKeys) {
            Optional<String> serverName = getNameExact(key);
            serverName.ifPresent(availableServers::add);
        }

    }

    @Override
    public void teardown() {
        // Perform any necessary cleanup here
    }
}
