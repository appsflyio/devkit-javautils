package io.appsfly.core;

public class AppsflyException extends Exception {
    private final String message;
    AppsflyException(String message) {
        this.message = message;
    }
}