package de.nikogenia.nnmaster.server;

import java.sql.Timestamp;

public abstract class Server {

    private String name;
    private String address;
    private String type;
    private boolean enabled;
    private Timestamp created;
    private String mode;

    public Server(String name, String address, String type, boolean enabled, Timestamp created, String mode) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.enabled = enabled;
        this.created = created;
        this.mode = mode;
    }

    public abstract void create();

    public abstract void start();

    public abstract void stop();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
