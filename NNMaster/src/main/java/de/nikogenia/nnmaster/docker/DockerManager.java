package de.nikogenia.nnmaster.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Binds;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;
import de.nikogenia.nnmaster.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DockerManager {

    private boolean connected;

    private DockerClient client;

    public DockerManager() {

        client = DockerClientBuilder.getInstance().build();

        for (Container container : getContainers()) {
            System.out.println("Container: " + container.getId() + " | " + Arrays.toString(container.getNames()) + " | " + container.getImage() + " | " + container.getState());
        }

        pullImages();

        for (Image image : getImages()) {
            System.out.println("Image: " + image.getId() + " | " + Arrays.toString(image.getRepoTags()));
        }

        connected = true;

    }

    public void createContainer(String image, String name, String network, String address, String[] binds) {

        for (Container container : getContainers()) {
            if (container.getNames()[0].equals("/" + name)) return;
        }

        System.out.println("Create container " + name + " with address " + address);

        client.createContainerCmd(image)
                .withName(name)
                .withHostConfig(HostConfig
                        .newHostConfig()
                        .withNetworkMode(network)
                        .withBinds(Binds.fromPrimitive(binds)))
                .withIpv4Address(address)
                .withStdinOpen(true)
                .exec();

    }

    public void startContainer(String name) {

        for (Container container : getContainers()) {
            if (container.getNames()[0].equals("/" + name)) {

                System.out.println("Start container " + name);

                try {
                    client.startContainerCmd(container.getId()).exec();
                } catch (NotModifiedException ignored) {}

                return;

            }
        }

    }

    public void stopContainer(String name) {

        for (Container container : getContainers()) {
            if (container.getNames()[0].equals("/" + name)) {

                System.out.println("Stop container " + name);

                try {
                    client.stopContainerCmd(container.getId()).exec();
                } catch (NotModifiedException ignored) {}

                return;

            }
        }

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

    public List<Image> getAllImages() {

        return client.listImagesCmd().withShowAll(true).exec();

    }

    public List<Image> getImages() {

        List<Image> sorted = new ArrayList<>();

        for (Image image : getAllImages()) {
            if (image.getRepoTags()[0].startsWith("nikogenia/mc"))
                sorted.add(image);
        }

        return sorted;

    }
    public boolean isImageInstalled(String name, String tag) {

        for (Image image : getImages()) {
            if (Arrays.asList(image.getRepoTags()).contains(name + ":" + tag))
                return true;
        }

        return false;

    }

    public void pullImage(String name, String tag) {

        try {
            client.pullImageCmd(name).withTag(tag).exec(new PullImageResultCallback()).awaitCompletion(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void pullImages() {

        System.out.println("Pull images");
        if (!isImageInstalled("nikogenia/mc-paper", "latest")) pullImage("nikogenia/mc-paper", "latest");

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
