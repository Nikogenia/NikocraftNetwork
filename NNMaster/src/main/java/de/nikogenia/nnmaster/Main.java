package de.nikogenia.nnmaster;

import de.nikogenia.nnmaster.api.APIServer;
import de.nikogenia.nnmaster.configurations.APIConfiguration;
import de.nikogenia.nnmaster.configurations.SQLConfiguration;
import de.nikogenia.nnmaster.sql.SQLManager;
import de.nikogenia.nnmaster.utils.Configuration;

public class Main {

    private static Main instance;

    public static final String NAME = "NikocraftNetwork";
    public static final String VERSION = "Alpha-0.0.1";
    public static final String AUTHOR = "Nikogenia";
    public static final String API_VERSION = "1.0";

    private SQLConfiguration sqlConfiguration;
    private APIConfiguration apiConfiguration;

    private APIServer apiServer;

    private SQLManager sqlManager;

    public static void main(String[] args) {

        instance = new Main();

        boolean debug = false;
        for (String arg : args) {
            if (arg.equals("-d") | arg.equals("--debug")) {
                debug = true;
                break;
            }
        }

        System.out.println("Start instance (debug mode: " + debug + ")");

        instance.run(debug);

    }

    public void run(boolean debug) {

        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));

        System.out.println("Load configurations");
        sqlConfiguration = (SQLConfiguration) Configuration.load("./configs/SQLConfiguration.yaml", SQLConfiguration.class);
        sqlConfiguration.save("./configs/SQLConfiguration.yaml");
        apiConfiguration = (APIConfiguration) Configuration.load("./configs/APIConfiguration.yaml", APIConfiguration.class);
        apiConfiguration.save("./configs/APIConfiguration.yaml");

        System.out.println("Load SQL manager");
        sqlManager = new SQLManager();
        if (!sqlManager.isConnected()) return;

        System.out.println("Load API server");
        apiServer = new APIServer();
        if (!apiServer.isRunning()) return;
        apiServer.start();

    }

    public void exit() {

        if (apiServer != null) apiServer.exit();

        if (sqlManager != null) sqlManager.exit();

        System.out.println("Exited.");

    }

    public static Main getInstance() { return instance; }

    public static SQLConfiguration getSQLConfiguration() { return instance.sqlConfiguration; }

    public static APIConfiguration getAPIConfiguration() { return instance.apiConfiguration; }

    public static APIServer getAPIServer() { return instance.apiServer; }

    public static SQLManager getSQLManager() {return instance.sqlManager; }

}
