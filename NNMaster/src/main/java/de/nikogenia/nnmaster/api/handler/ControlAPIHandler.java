package de.nikogenia.nnmaster.api.handler;

import de.nikogenia.nnmaster.Main;
import de.nikogenia.nnmaster.api.APIClient;
import de.nikogenia.nnmaster.api.APIMessage;
import de.nikogenia.nnmaster.server.RemoteServer;
import de.nikogenia.nnmaster.server.Server;
import org.apache.commons.lang3.StringUtils;

public class ControlAPIHandler extends APIHandler {

    public ControlAPIHandler(APIClient client) {
        super(client);
    }

    @Override
    public void receive(APIMessage message, String data) {

        if (message.equals(APIMessage.CONTROL_SERVERS)) {

            StringBuilder servers = new StringBuilder();

            for (Server server : Main.getServerManager().getServers()) {
                servers.append(server.getName())
                        .append("#").append(server.getAddress())
                        .append("#").append(server.isEnabled())
                        .append("#").append(server.getType())
                        .append("#").append((server instanceof RemoteServer) ? ((RemoteServer) server).getAgent() : "master")
                        .append("#").append(server.getMode()).append(";");
            }

            getClient().send(APIMessage.CONTROL_SERVERS, StringUtils.removeEnd(servers.toString(), ";"));

        }

    }

}
