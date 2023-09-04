package de.nikogenia.nnmaster.api.handler;

import de.nikogenia.nnmaster.api.APIClient;
import de.nikogenia.nnmaster.api.APIMessage;

public abstract class APIHandler {

    private APIClient client;

    public APIHandler(APIClient client) {
        this.client = client;
    }

    public abstract void receive(APIMessage message, String data);

    public APIClient getClient() { return client; }

}
