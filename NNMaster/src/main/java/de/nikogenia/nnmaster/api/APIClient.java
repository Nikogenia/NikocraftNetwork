package de.nikogenia.nnmaster.api;

import de.nikogenia.nnmaster.Main;
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

    public APIClient(Socket socket) {

        super("API Client Thread - " + socket.getRemoteSocketAddress());

        this.socket = socket;

        try {

            socket.setSoTimeout(5000);

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            if (!initialize()) {
                exit();
                return;
            }

            send(APIMessage.AUTH_SUCCESSFUL);

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            exit();
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
                    exit();
                }
                return;
            }

            if (received.getLeft().equals(APIMessage.DISCONNECT)) {
                Main.getLogger().info("API Connection from " + socket.getRemoteSocketAddress() + " lost. Disconnect ...");
                exit();
                return;
            }

            /*

            if (received.getLeft().equals(APIMessage.CONSOLE_INPUT)) {

                Main.getProxyProcess().write(received.getRight());

            }

            if (received.getLeft().equals(APIMessage.CONSOLE_OUTPUT)) {

                send(APIMessage.CONSOLE_OUTPUT, Main.getProxyProcess().getOutputCache());

            }

            if (received.getLeft().equals(APIMessage.CONSOLE_HISTORY)) {

                send(APIMessage.CONSOLE_HISTORY, String.join("\n", Main.getProxyProcess().getHistory()));

            }

            */

        }

    }

    public void exit() {

        if (connected) Main.getAPIServer().getClients().remove(this);

        connected = false;
        closed = true;

        try {

            input.close();
            output.close();

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public boolean isConnected() { return connected; }

    public String getVersion() { return version; }

    public String getAPIVersion() { return apiVersion; }

    public Socket getSocket() { return socket; }

}
