package com.matthew.plugin.test;

import com.matthew.plugin.api.Module;
import com.matthew.plugin.modules.ModuleManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerModuleManagerTest {

    private ModuleManager serverModuleManager;

    @BeforeEach
    void setUp() {
        serverModuleManager = ModuleManager.getInstance();
        // Clear the registered modules before each test to ensure a clean slate
        serverModuleManager.getRegisteredModules().clear();
    }

    @Test
    void testSingletonInstance() {
        ModuleManager instance1 = ModuleManager.getInstance();
        ModuleManager instance2 = ModuleManager.getInstance();
        assertSame(instance1, instance2, "Instances should be the same (singleton pattern)");
    }

    @Test
    void testRegisterModule() {
        Module mockModule = mock(Module.class);
        serverModuleManager.registerModule(mockModule);
        assertTrue(serverModuleManager.getRegisteredModules().contains(mockModule), "Module should be registered");
    }

    @Test
    void testGetRegisteredModule() {
        class TestModule implements Module {
            @Override
            public void setUp() {}
            @Override
            public void teardown() {}
        }
        TestModule testModule = new TestModule();
        serverModuleManager.registerModule(testModule);
        TestModule retrievedModule = serverModuleManager.getRegisteredModule(TestModule.class);
        assertSame(testModule, retrievedModule, "Retrieved module should be the same as the registered module");
    }

    @Test
    void testGetRegisteredModule_NotFound() {
        Module nonExistentModule = serverModuleManager.getRegisteredModule(Module.class);
        assertNull(nonExistentModule, "Should return null when no module of the given class is registered");
    }

    @Test
    void testSetUp() {
        Module mockModule1 = mock(Module.class);
        Module mockModule2 = mock(Module.class);
        serverModuleManager.registerModule(mockModule1);
        serverModuleManager.registerModule(mockModule2);

        serverModuleManager.setUp();

        verify(mockModule1, times(1)).setUp();
        verify(mockModule2, times(1)).setUp();
    }

    @Test
    void testTeardown() {
        Module mockModule1 = mock(Module.class);
        Module mockModule2 = mock(Module.class);
        serverModuleManager.registerModule(mockModule1);
        serverModuleManager.registerModule(mockModule2);

        serverModuleManager.teardown();

        verify(mockModule1, times(1)).teardown();
        verify(mockModule2, times(1)).teardown();
    }
}