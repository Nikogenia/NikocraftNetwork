package de.nikogenia.nnmaster;

import de.nikogenia.nnmaster.api.APIServer;
import de.nikogenia.nnmaster.configurations.APIConfiguration;
import de.nikogenia.nnmaster.configurations.SQLConfiguration;
import de.nikogenia.nnmaster.utils.Configuration;

import java.sql.Connection;
import java.sql.Statement;

public class Main {

    private static Main instance;

    public static final String NAME = "Nikocraft Network";
    public static final String VERSION = "Alpha-0.0.1";
    public static final String AUTHOR = "Nikogenia";
    public static final String API_VERSION = "Nikogenia";

    private SQLConfiguration sqlConfiguration;
    private APIConfiguration apiConfiguration;

    private APIServer apiServer;

    private Connection connection;
    private Statement statement;

    public static void main(String[] args) {

        instance = new Main();

        boolean debug = false;
        for (String arg : args) {
            if (arg.equals("-d") | arg.equals("--debug")) {
                debug = true;
                break;
            }
        }

        instance.run(debug);

    }

    public void run(boolean debug) {

        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));

        sqlConfiguration = (SQLConfiguration) Configuration.load("./configs/SQLConfiguration.yaml", SQLConfiguration.class);
        sqlConfiguration.save("./configs/SQLConfiguration.yaml");
        apiConfiguration = (APIConfiguration) Configuration.load("./configs/APIConfiguration.yaml", APIConfiguration.class);
        apiConfiguration.save("./configs/APIConfiguration.yaml");

        apiServer = new APIServer();
        apiServer.start();

    }

    public void exit() {

    }

    public static Main getInstance() { return instance; }

    public static SQLConfiguration getSQLConfiguration() { return instance.sqlConfiguration; }

    public static APIConfiguration getAPIConfiguration() { return instance.apiConfiguration; }

    public static APIServer getAPIServer() { return instance.apiServer; }

}
