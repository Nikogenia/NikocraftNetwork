package de.nikogenia.nnmaster.configurations;

import de.nikogenia.nnmaster.utils.Configuration;

public class APIConfiguration extends Configuration {

    private int port = 42420;
    private String key = "DefaultKey";

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
