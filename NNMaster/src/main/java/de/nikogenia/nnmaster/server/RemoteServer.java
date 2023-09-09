package de.nikogenia.nnmaster.server;

import java.sql.Timestamp;

public class RemoteServer extends Server {

    private String agent;

    public RemoteServer(String name, String address, String type, boolean enabled, Timestamp created, String mode) {
        super(name, address, type, enabled, created, mode);
    }

    @Override
    public void create() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    public String getAgent() {
        return agent;
    }

    public RemoteServer setAgent(String agent) {
        this.agent = agent;
        return this;
    }

}
