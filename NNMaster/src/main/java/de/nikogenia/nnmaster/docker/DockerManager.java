package de.nikogenia.nnmaster.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;
import de.nikogenia.nnmaster.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DockerManager {

    private boolean connected;

    private DockerClient client;

    public DockerManager() {

        client = DockerClientBuilder.getInstance().build();

        System.out.println("Containers:");
        for (Container container : getContainers()) {
            System.out.println("Container: " + container.getId() + " | " + container.getNames()[0] + " | " + container.getImage() + " | " + container.getState() + " | " + container.getStatus());
        }

        System.out.println("All Containers:");
        for (Container container : getAllContainers()) {
            System.out.println("Container: " + container.getId() + " | " + container.getNames()[0] + " | " + container.getImage() + " | " + container.getState() + " | " + container.getStatus());
        }

        connected = true;

    }

    public List<Container> getAllContainers() {

        return client.listContainersCmd().withShowAll(true).exec();

    }

    public List<Container> getContainers() {

        List<Container> sorted = new ArrayList<>();

        for (Container container : getAllContainers()) {
            if (container.getNames()[0].startsWith("/" + Main.getGeneralConfig().getDockerPrefix()))
                sorted.add(container);
        }

        return sorted;

    }

    public void exit() {

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isConnected() {
        return connected;
    }

}
