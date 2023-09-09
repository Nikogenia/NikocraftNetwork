package de.nikogenia.nnmaster.api;

import java.util.Arrays;

public enum APIMessage {

    AUTH_ID(0),
    AUTH_KEY(1),
    INVALID_ID(2),
    INVALID_KEY(3),
    AUTH_SUCCESSFUL(4),

    CLOSED(100),
    DISCONNECT(101),

    SERVER_LIST(200),

    CONTROL_SERVERS(300);

    private final int value;

    APIMessage(int value) {
        this.value = value;
    }

    public String getValue() {

        return format(value);

    }

    public int getRawValue() {

        return value;

    }

    public static String format(int value) {

        return "0".repeat(getValueSize() - String.valueOf(value).length()) + value;

    }

    public static int getValueSize() {
        return 10;
    }

    public static APIMessage fromValue(int value) {

        for (APIMessage message : values()) {

            if (message.getRawValue() == value) return message;

        }

        return null;

    }

}
