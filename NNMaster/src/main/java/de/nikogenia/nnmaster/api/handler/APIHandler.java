package de.nikogenia.nnmaster.api.handler;

import de.nikogenia.nnmaster.api.APIClient;

public abstract class APIHandler {

    private APIClient client;

    public APIHandler(APIClient client) {
        this.client = client;
    }

    public abstract void receive();

    public APIClient getClient() { return client; }

}
