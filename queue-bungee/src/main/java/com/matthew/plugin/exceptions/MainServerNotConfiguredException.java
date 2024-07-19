package com.matthew.plugin.exceptions;

public class MainServerNotConfiguredException extends RuntimeException {
    public MainServerNotConfiguredException(final String message) {
        super(message);
    }
}
