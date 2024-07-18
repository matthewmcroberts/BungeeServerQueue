package com.matthew.plugin.modules.settings;

import com.matthew.plugin.api.Module;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RequiredArgsConstructor
public class SettingsModule implements Module {

    private final Plugin plugin;

    @Getter
    private Configuration config;

    @Nullable
    public Boolean getBoolean(@NonNull final String path) {
        if (config.contains(path)) {
            return config.getBoolean(path);
        }
        return null;
    }

    @Nullable
    public Integer getInteger(@NonNull final String path) {
        if (config.contains(path)) {
            return config.getInt(path);
        }
        return null;
    }

    @Nullable
    public String getString(@NonNull final String path) {
        if (config.contains(path)) {
            return config.getString(path);
        }
        return null;
    }

    @Override
    public void setUp() {
        final String PATH = "bungeeconfig.yml";
        File configFile = new File(plugin.getDataFolder(), PATH);

        if (!configFile.exists()) {
            try (InputStream in = this.getClass().getResourceAsStream(PATH)) {
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                }
            } catch (IOException e) {
                plugin.getLogger().info("Unable to copy " + PATH + " to " + configFile.getAbsolutePath());
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            plugin.getLogger().info("Unable to load " + PATH + " to " + configFile.getAbsolutePath());
        }
    }

    @Override
    public void teardown() {

    }
}
