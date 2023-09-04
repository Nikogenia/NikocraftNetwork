package de.nikogenia.nnmaster.api.handler;

import de.nikogenia.nnmaster.api.APIClient;
import de.nikogenia.nnmaster.api.APIMessage;

public class ControlAPIHandler extends APIHandler {

    public ControlAPIHandler(APIClient client) {
        super(client);
    }

    @Override
    public void receive(APIMessage message, String data) {

    }

}
