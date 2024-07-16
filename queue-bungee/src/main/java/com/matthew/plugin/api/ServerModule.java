package com.matthew.plugin.api;

public interface ServerModule  {

    /**
     * Sets up the module.
     */
    void setUp();

    /**
     * Tears down any additional allocated resources
     */
    void teardown();
}
