package de.nikogenia.nnmaster.api;

import java.util.Arrays;

public enum APIMessage {

    INVALID_ID,
    INVALID_KEY,
    AUTH_SUCCESSFUL,

    CLOSED,
    KICKED,
    DISCONNECT,

    CONSOLE_LINE_UPDATE,
    CONSOLE_INPUT,
    CONSOLE_OUTPUT,
    CONSOLE_HISTORY;

    public String getValue() {

        return format(Arrays.binarySearch(values(), this));

    }

    public int getRawValue() {

        return Arrays.binarySearch(values(), this);

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
