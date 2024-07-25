package com.matthew.plugin.modules;

import com.matthew.plugin.api.Module;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * The ModuleManager class is responsible for managing and coordinating the lifecycle
 * of all registered modules. It implements the Module interface, allowing it to be
 * treated as a module itself.
 */
@Getter
public class ModuleManager implements Module {

    private static ModuleManager instance;

    private final Set<Module> registeredModules;

    private ModuleManager() {
        registeredModules = new HashSet<>();
    }

    /**
     * Returns the singleton instance of ModuleManager, creating it if it does not exist.
     *
     * @return The singleton instance of ModuleManager.
     */
    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    /**
     * Registers a new module and adds it to the set of registered modules.
     * This method supports method chaining by returning the ModuleManager instance.
     *
     * @param module The module to register.
     * @return The ModuleManager instance.
     */
    public ModuleManager registerModule(Module module) {
        registeredModules.add(module);
        return this;
    }

    /**
     * Retrieves a registered module of the specified class type.
     *
     * @param <T> The type of the module.
     * @param clazz The class of the module to retrieve.
     * @return The registered module of the specified type, or null if not found.
     */
    public <T extends Module> T getRegisteredModule(Class<T> clazz) {
        for (Module module : registeredModules) {
            if (clazz.isInstance(module)) {
                return clazz.cast(module);
            }
        }
        return null;
    }

    /**
     * Sets up all registered modules by calling their setUp method.
     */
    @Override
    public void setUp() {
        for (Module module : registeredModules) {
            module.setUp();
        }
    }

    /**
     * Tears down all registered modules by calling their teardown method.
     */
    @Override
    public void teardown() {
        for (Module module : registeredModules) {
            module.teardown();
        }
    }
}
