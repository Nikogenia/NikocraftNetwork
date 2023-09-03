package de.nikogenia.nnmaster.server;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.InternetProtocol;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import de.nikogenia.nnmaster.Main;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class WaterfallServer extends Server {

    public WaterfallServer(String name, String address, String type, boolean enabled, Timestamp created, String mode) {

        super(name, address, type, enabled, created, mode);

        Main.getLogger().fine("Server: " + name + " | " + address + " | " + type + " | " + enabled + " | "
                + created.toInstant().atZone(Main.getTimeZone()).format(DateTimeFormatter.ISO_DATE_TIME) + " | " + mode);

    }

    @Override
    public void create() {

        Main.getDockerManager().createContainer(
                "nikogenia/mc-waterfall:latest",
                Main.getGeneralConfig().getDockerPrefix() + getName(),
                Main.getGeneralConfig().getDockerNetwork(),
                getAddress(),
                true,
                new String[]{
                        Main.getGeneralConfig().getRootPath() + getName() + ":/proxy",
                }
        );

    }

    @Override
    public void start() {

        Main.getDockerManager().startContainer(Main.getGeneralConfig().getDockerPrefix() + getName());

    }

    @Override
    public void stop() {

        Main.getDockerManager().stopContainer(Main.getGeneralConfig().getDockerPrefix() + getName());

    }

}
