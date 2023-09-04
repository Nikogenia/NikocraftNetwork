package de.nikogenia.nnproxy.api;

import de.nikogenia.nnproxy.Main;
import de.nikogenia.nnproxy.utils.AESUtils;
import de.nikogenia.nnproxy.utils.RSAUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyPair;
import java.util.Arrays;

public class APIClient extends Thread {

    private Socket socket;

    private DataInputStream input;
    private DataOutputStream output;

    private String version;
    private String apiVersion;

    private boolean running;

    private SecretKey key;

    public APIClient() {

        super("API Client Thread");

    }

    public boolean connect() throws IOException {

        String ip = Main.getAPIConfig().getIp();
        int port = Main.getAPIConfig().getPort();

        Main.getInstance().getLogger().info("Try to connect to " + ip + ":" + port);

        socket = new Socket(ip, port);
        socket.setSoTimeout(10000);

        Main.getInstance().getLogger().info("Connection to " + socket.getRemoteSocketAddress() + " opened. Initialize");

        Main.getInstance().getLogger().fine("Create streams");
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        boolean success = initialize(Main.getGeneralConfig().getAPIKey());

        if (!success) {
            input.close();
            output.close();
            socket.close();
        }

        return success;

    }

    public boolean initialize(String apiKey) throws IOException {

        Main.getInstance().getLogger().fine("Send client information");
        output.writeUTF(Main.NAME + "-" + Main.VERSION + "-" + Main.API_VERSION);

        Main.getInstance().getLogger().fine("Get server information");
        String[] answer = input.readUTF().split("-");
        if (!answer[0].equals(Main.NAME)) {
            Main.getInstance().getLogger().info("Connection failed! Invalid server.");
            return false;
        };
        version = answer[1];
        apiVersion = answer[2];
        if (!apiVersion.equals(Main.API_VERSION)) {
            Main.getInstance().getLogger().info("Connection failed! Invalid server version.");
            return false;
        };

        Main.getInstance().getLogger().fine("Generate RSA keys and send public key");
        KeyPair keyPair = RSAUtils.generate();
        output.writeUTF(RSAUtils.exportKey(keyPair.getPublic()));

        try {
            Main.getInstance().getLogger().fine("Get AES key");
            key = AESUtils.importKey(RSAUtils.decrypt(input.readUTF(), keyPair.getPrivate()));
        } catch (Exception e) {
            Main.getInstance().getLogger().info("Connection failed! Invalid AES key.");
            return false;
        }

        Main.getInstance().getLogger().fine("Send ID");
        send(APIMessage.AUTH_ID, "proxy");
        Main.getInstance().getLogger().fine("Send API key");
        send(APIMessage.AUTH_KEY, apiKey);

        Main.getInstance().getLogger().fine("Get confirmation");
        Pair<APIMessage, String> confirmation = receive();

        if (confirmation == null) {
            Main.getInstance().getLogger().info("Connection failed! Connection error.");
            return false;
        }

        if (confirmation.getLeft().equals(APIMessage.INVALID_ID)) {
            Main.getInstance().getLogger().info("Connection failed! Invalid ID.");
            return false;
        }

        if (confirmation.getLeft().equals(APIMessage.INVALID_KEY)) {
            Main.getInstance().getLogger().info("Connection failed! Invalid key.");
            return false;
        }

        if (confirmation.getLeft().equals(APIMessage.AUTH_SUCCESSFUL)) return true;
        else {
            Main.getInstance().getLogger().info("Connection failed! Unexpected error.");
            return false;
        }

    }

    private void timeout() {

        try {
            Thread.sleep(Main.getGeneralConfig().getAPIConnectionRetry() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        running = true;

        while (running) {

            try {
                if (!connect()) {
                    timeout();
                    continue;
                }
            } catch (IOException e) {
                Main.getInstance().getLogger().info("Connection failed! Connection error.");
                timeout();
                continue;
            }

            Main.getInstance().getLogger().info("Connection with " + socket.getRemoteSocketAddress() + " successful. Server version: " + version);

            handle();

            try {
                Main.getInstance().getLogger().fine("Close streams ...");
                input.close();
                output.close();

                Main.getInstance().getLogger().fine("Close socket ...");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            timeout();

        }

    }

    public void handle() {

        send(APIMessage.SERVER_LIST);

        while (running) {

            Pair<APIMessage, String> received = receive();

            if (received == null) {
                Main.getInstance().getLogger().info("Connection lost! Connection error.");
                return;
            }

            if (received.getLeft().equals(APIMessage.CLOSED)) {
                Main.getInstance().getLogger().info("Connection lost! Master closed.");
                return;
            }

            if (received.getLeft().equals(APIMessage.SERVER_LIST)) {
                if (!received.getRight().isEmpty())
                    Main.getInstance().updateServers(received.getRight().split(";"));
            }

        }

        send(APIMessage.DISCONNECT);

    }

    public void exit() {

        running = false;

    }

    public void send(APIMessage message, String data) {

        try {

            String encrypted = AESUtils.encrypt(message.getValue() + data, key);

            if (encrypted == null) return;

            output.writeUTF(encrypted);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void send(APIMessage message) {

        send(message, "");

    }

    public Pair<APIMessage, String> receive() {

        while (true) {

            try {

                String received = AESUtils.decrypt(input.readUTF(), key);

                if (received == null) return null;

                APIMessage message = APIMessage.fromValue(Integer.parseInt(received.substring(0, APIMessage.getValueSize())));

                return Pair.of(message, received.substring(APIMessage.getValueSize()));

            }
            catch (SocketTimeoutException ignored) {}
            catch (IOException | IndexOutOfBoundsException e) {
                e.printStackTrace();
                return null;
            }

        }

    }

    public Socket getSocket() { return socket; }

    public String getVersion() { return version; }

    public String getAPIVersion() { return apiVersion; }

}
