package de.nikogenia.nnmaster.api;

import de.nikogenia.nnmaster.Main;
import de.nikogenia.nnmaster.api.handler.APIHandler;
import de.nikogenia.nnmaster.api.handler.ProxyAPIHandler;
import de.nikogenia.nnmaster.utils.AESUtils;
import de.nikogenia.nnmaster.utils.RSAUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.PublicKey;

public class APIClient extends Thread {

    private final Socket socket;

    private DataInputStream input;
    private DataOutputStream output;

    private boolean connected;

    private boolean closed;

    private String version;
    private String apiVersion;

    private SecretKey key;

    private APIHandler handler;

    public APIClient(Socket socket) {

        super("API Client Thread - " + socket.getRemoteSocketAddress());

        this.socket = socket;

        try {

            socket.setSoTimeout(5000);

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            if (!initialize()) {
                exit(true);
                return;
            }

            send(APIMessage.AUTH_SUCCESSFUL);

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            exit(true);
            return;
        }

        connected = true;

    }

    public boolean initialize() throws IOException, NullPointerException {

        output.writeUTF(Main.NAME + "-" + Main.VERSION + "-" + Main.API_VERSION);

        String[] answer = input.readUTF().split("-");
        if (!answer[0].equals(Main.NAME)) return false;
        version = answer[1];
        apiVersion = answer[2];
        if (!apiVersion.equals(Main.API_VERSION)) return false;

        PublicKey publicKey = RSAUtils.importKey(input.readUTF());
        if (publicKey == null) return false;

        key = AESUtils.generate();
        String encryptedKey = RSAUtils.encrypt(AESUtils.exportKey(key), publicKey);
        if (encryptedKey == null) return false;

        output.writeUTF(encryptedKey);

        String id = receive().getRight();
        if (id.equals("control")) {

        }
        else if (id.equals("proxy")) {
            handler = new ProxyAPIHandler(this);
        }
        else {
            send(APIMessage.INVALID_ID);
            return false;
        }

        if (!Main.getGeneralConfig().getAPIKey().equals(receive().getRight())) {
            send(APIMessage.INVALID_KEY);
            return false;
        }

        return true;

    }

    @Override
    public void run() {

        while (true) {

            Pair<APIMessage, String> received = receive();

            if (received == null) {
                if (!closed) {
                    Main.getLogger().info("API Connection from " + socket.getRemoteSocketAddress() + " lost. Connection error ...");
                    exit(true);
                }
                return;
            }

            if (received.getLeft().equals(APIMessage.DISCONNECT)) {
                Main.getLogger().info("API Connection from " + socket.getRemoteSocketAddress() + " lost. Disconnect ...");
                exit(true);
                return;
            }

            handler.receive();

        }

    }

    public void exit(boolean remove) {

        if (connected & remove) Main.getAPIServer().getClients().remove(this);

        connected = false;
        closed = true;

        try {

            input.close();
            output.close();

            socket.close();

        } catch (IOException e) {
            Main.getLogger().warning("Failed to close socket: " + e);
        }

    }

    public void send(APIMessage message, String data) {

        try {

            String encrypted = AESUtils.encrypt(message.getValue() + data, key);

            if (encrypted == null) return;

            output.writeUTF(encrypted);

        } catch (IOException e) {
            Main.getLogger().warning("Failed to send message: " + e);
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
                Main.getLogger().warning("Failed to receive message: " + e);
                return null;
            }

        }

    }

    public boolean isConnected() { return connected; }

    public String getVersion() { return version; }

    public String getAPIVersion() { return apiVersion; }

    public Socket getSocket() { return socket; }

    public APIHandler getHandler() { return handler; }

}
