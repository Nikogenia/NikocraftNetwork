package de.nikogenia.nnmaster.server;

import java.util.Arrays;

public enum ServerMode {

    OFF,
    ALWAYS_ON;

    public int getValue() {

        return Arrays.binarySearch(values(), this);

    }

    public static ServerMode fromValue(int value) {

        for (ServerMode mode : values()) {

            if (mode.getValue() == value) return mode;

        }

        return null;

    }

}
