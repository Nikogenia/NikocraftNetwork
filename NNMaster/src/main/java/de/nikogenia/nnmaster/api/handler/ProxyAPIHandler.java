package de.nikogenia.nnmaster.api.handler;

import de.nikogenia.nnmaster.Main;
import de.nikogenia.nnmaster.api.APIClient;
import de.nikogenia.nnmaster.api.APIMessage;
import de.nikogenia.nnmaster.server.Server;
import org.apache.commons.lang3.StringUtils;

public class ProxyAPIHandler extends APIHandler {

    public ProxyAPIHandler(APIClient client) {
        super(client);
    }

    @Override
    public void receive(APIMessage message, String data) {

        if (message.equals(APIMessage.SERVER_LIST)) {

            StringBuilder servers = new StringBuilder();

            for (Server server : Main.getServerManager().getServers()) {
                if (!server.isEnabled()) continue;
                if (server.getType().equals("waterfall")) continue;
                servers.append(server.getName()).append("#").append(server.getAddress()).append(";");
            }

            getClient().send(APIMessage.SERVER_LIST, StringUtils.removeEnd(servers.toString(), ";"));

        }

    }

}
