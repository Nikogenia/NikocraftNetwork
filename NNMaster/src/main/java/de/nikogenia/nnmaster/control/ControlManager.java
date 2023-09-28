package de.nikogenia.nnmaster.control;

import de.nikogenia.nnmaster.Main;
import de.nikogenia.nnmaster.server.RemoteServer;
import de.nikogenia.nnmaster.server.Server;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;

public class ControlManager {

    Socket socket;

    public ControlManager() {

        IO.Options options = IO.Options.builder()
                .setAuth(Collections.singletonMap("token", Main.getGeneralConfig().getAPIKey()))
                .build();

        socket = IO.socket(URI.create("ws://host.docker.internal:8080"), options);

        socket.on("connect", args -> Main.getLogger().info("Connected to control server"));

        socket.on("get_servers", args -> {
            try {
                JSONArray servers = new JSONArray();
                for (Server server : Main.getServerManager().getServers()) {
                    JSONObject s = new JSONObject();
                    s.put("name", server.getName());
                    s.put("address", server.getAddress());
                    s.put("enabled", server.isEnabled());
                    s.put("type", server.getType());
                    s.put("agent", (server instanceof RemoteServer) ? ((RemoteServer) server).getAgent() : "master");
                    s.put("mode", server.getMode());
                    servers.put(s);
                }
                JSONObject response = new JSONObject(Collections.singletonMap("servers", servers));
                socket.emit("servers", response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socket.on("get_logs", args -> {
            try {
                JSONObject response = new JSONObject();
                for (Server server : Main.getServerManager().getServers()) {
                    response.put(server.getName(), server.getLogs());
                }
                socket.emit("logs", response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socket.connect();

    }

    public void sendLineUpdate(String server, String line) {
        try {
            JSONObject response = new JSONObject();
            response.put("server", server);
            response.put("line", line);
            socket.emit("line_update", response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void exit() {

        socket.close();

    }

}
