package de.nikogenia.nnmaster.server;

import java.sql.Timestamp;

public class RemoteServer extends Server {

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

}
