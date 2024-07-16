package com.matthew.plugin.modules.manager;

import com.matthew.plugin.api.Module;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;


@Getter
public class ModuleManager implements Module {

    private static ModuleManager instance;

    private final Set<Module> registeredModules;

    private ModuleManager() {
        registeredModules = new HashSet<>();
    }

    public static ModuleManager getInstance() {
        if(instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    public ModuleManager registerModule(Module module) {
        registeredModules.add(module);
        return this; //used for method chaining in BungeeQueuePlugin#onEnable
    }

    public <T extends Module> T getRegisteredModule(Class<T> clazz) {
        for(Module module: registeredModules) {
            if(clazz.isInstance(module)) {
                return clazz.cast(module);
            }
        }
        return null;
    }

    @Override
    public void setUp() {
        for(Module module: registeredModules) {
            module.setUp();
        }
    }

    @Override
    public void teardown() {
        for(Module module: registeredModules) {
            module.teardown();
        }
    }
}
