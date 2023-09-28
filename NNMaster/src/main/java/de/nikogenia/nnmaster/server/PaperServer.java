package de.nikogenia.nnmaster.server;

import de.nikogenia.nnmaster.Main;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class PaperServer extends Server {

    public PaperServer(String name, String address, String type, boolean enabled, Timestamp created, String mode) {

        super(name, address, type, enabled, created, mode);

        Main.getLogger().fine("Server: " + name + " | " + address + " | " + type + " | " + enabled + " | "
                + created.toInstant().atZone(Main.getTimeZone()).format(DateTimeFormatter.ISO_DATE_TIME) + " | " + mode);

    }

    @Override
    public void create() {

        Main.getDockerManager().createContainer(
                "nikogenia/mc-paper:latest",
                Main.getGeneralConfig().getDockerPrefix() + getName(),
                Main.getGeneralConfig().getDockerNetwork(),
                getAddress(),
                false,
                new String[]{
                        Main.getGeneralConfig().getRootPath() + getName() + ":/server",
                }
        );

    }

    @Override
    public void start() {

        Main.getDockerManager().startContainer(Main.getGeneralConfig().getDockerPrefix() + getName());

        Main.getDockerManager().getLog(Main.getGeneralConfig().getDockerPrefix() + getName(), (line) -> {
            setLogs(getLogs() + line + "\n");
            Main.getControlManager().sendLineUpdate(getName(), line);
        });

    }

    @Override
    public void stop() {

        Main.getDockerManager().stopContainer(Main.getGeneralConfig().getDockerPrefix() + getName());

    }

}
