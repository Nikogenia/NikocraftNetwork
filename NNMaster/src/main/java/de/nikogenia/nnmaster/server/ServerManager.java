package de.nikogenia.nnmaster.server;

import de.nikogenia.nnmaster.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ServerManager {

    private List<Server> servers;

    public ServerManager() {

        servers = new ArrayList<>();

        Main.getLogger().info("Load servers");
        loadServers();

    }

    public void loadServers() {

        servers.clear();

        try {

            ResultSet rs = Main.getSQLManager().getStatement().executeQuery("""
                    SELECT * FROM server;
                    """);

            while (rs.next()) {

                String name = rs.getString("name");
                String address = rs.getString("address");
                String agent = rs.getString("agent");
                String type = rs.getString("type");
                boolean enabled = rs.getBoolean("enabled");
                Timestamp created = rs.getTimestamp("created");
                String mode = rs.getString("mode");

                if (!agent.equals("master")) {
                    servers.add(new RemoteServer(name, address, type, enabled, created, mode).setAgent(agent));
                    continue;
                }

                if (type.equals("paper")) {
                    servers.add(new PaperServer(name, address, type, enabled, created, mode));
                }

                if (type.equals("waterfall")) {
                    servers.add(new WaterfallServer(name, address, type, enabled, created, mode));
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void run() {

        for (Server server : servers) {

            if (server.isEnabled()) server.create();

            server.start();

        }

    }

    public void exit() {

        for (Server server : servers) {
            server.stop();
        }

    }

    public List<Server> getServers() {
        return servers;
    }

}
