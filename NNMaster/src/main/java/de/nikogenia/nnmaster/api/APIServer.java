package de.nikogenia.nnmaster.api;

import de.nikogenia.nnmaster.Main;
import de.nikogenia.nnmaster.api.handler.ControlAPIHandler;
import de.nikogenia.nnmaster.api.handler.ProxyAPIHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class APIServer extends Thread {

    private ServerSocket server;

    private List<APIClient> clients;

    private boolean running;

    public APIServer() {

        super("API Server Thread");

        clients = new ArrayList<>();

        try {

            server = new ServerSocket(Main.getGeneralConfig().getAPIPort());
            server.setSoTimeout(1000);
            running = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        Main.getLogger().info("Started listening for API client on " + server.getLocalPort() + ".");

        while (running) {

            try {

                Socket connection = server.accept();

                Main.getLogger().info("New API client connection from " + connection.getRemoteSocketAddress() + " opened. Initialize ...");

                APIClient client = new APIClient(connection);

                if (client.isConnected()) {

                    client.start();

                    clients.add(client);

                    Main.getLogger().info("API client connection from " + connection.getRemoteSocketAddress() + " successful. Client version: " + client.getVersion());

                }
                else Main.getLogger().info("API client connection from " + connection.getRemoteSocketAddress() + " failed.");

            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }

        }

        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void exit() {

        Main.getLogger().info("Shutting down API server ...");

        running = false;

        for (APIClient client : clients) {

            Main.getLogger().info("API client connection from " + client.getSocket().getRemoteSocketAddress() + " closed. Server shutdown ...");

            client.send(APIMessage.CLOSED);

            client.exit(false);

        }

    }

    public boolean isRunning() {
        return running;
    }

    public List<APIClient> getClients() { return clients; }

    public APIClient getProxy() {

        for (APIClient client : clients) {
            if (client.getHandler() instanceof ProxyAPIHandler) return client;
        }

        return null;

    }

    public APIClient getControl() {

        for (APIClient client : clients) {
            if (client.getHandler() instanceof ControlAPIHandler) return client;
        }

        return null;

    }

}
