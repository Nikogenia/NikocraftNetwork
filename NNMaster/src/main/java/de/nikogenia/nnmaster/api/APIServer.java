package de.nikogenia.nnmaster.api;

import de.nikogenia.nnmaster.Main;

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

        super("Server Thread");

        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));

        clients = new ArrayList<>();

        try {

            server = new ServerSocket(Main.getAPIConfiguration().getPort());
            server.setSoTimeout(1000);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        System.out.println("Started listening for API client on " + server.getLocalPort() + ".");

        running = true;

        while (running) {

            try {

                Socket connection = server.accept();

                System.out.println("New API client connection from " + connection.getRemoteSocketAddress() + " opened. Initialize ...");

                APIClient client = new APIClient(connection);

                if (client.isConnected()) {

                    client.start();

                    clients.add(client);

                    System.out.println("API client connection from " + connection.getRemoteSocketAddress() + " successful. Client version: " + client.getVersion());

                }
                else System.out.println("API client connection from " + connection.getRemoteSocketAddress() + " failed.");

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

        System.out.println("Shutting down API server ...");

        running = false;

        for (APIClient client : clients) {

            System.out.println("API client connection from " + client.getSocket().getRemoteSocketAddress() + " closed. Server shutdown ...");

            client.send(APIMessage.CLOSED);

            client.exit();

        }

    }

    public void setRunning(boolean running) { this.running = running; }

    public List<APIClient> getClients() { return clients; }

}
