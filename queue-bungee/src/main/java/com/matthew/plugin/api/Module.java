package com.matthew.plugin.api;

public interface Module {

    /**
     * Sets up the module.
     */
    void setUp();

    /**
     * Tears down any additional allocated resources
     */
    void teardown();
}
