package de.nikogenia.nnproxy.config;

import de.nikogenia.nnproxy.utils.FileConfig;

public class APIConfig extends FileConfig {

    private String ip = "";
    private Integer port = 42420;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
