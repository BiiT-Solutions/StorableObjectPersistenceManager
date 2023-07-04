package com.biit.persistence.utils;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {
        // Private constructor to hide the implicit public one.
    }

    public static String createId() {
        final UUID uuid = java.util.UUID.randomUUID();
        return uuid.toString();
    }
}
