package de.nikogenia.nnmaster;

import de.nikogenia.nnmaster.api.APIServer;
import de.nikogenia.nnmaster.config.GeneralConfig;
import de.nikogenia.nnmaster.config.SQLConfig;
import de.nikogenia.nnmaster.docker.DockerManager;
import de.nikogenia.nnmaster.server.ServerManager;
import de.nikogenia.nnmaster.sql.SQLManager;
import de.nikogenia.nnmaster.utils.FileConfig;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.TimeZone;

public class Main {

    private static Main instance;

    public static final String NAME = "NikocraftNetwork";
    public static final String VERSION = "0.0.1";
    public static final String AUTHOR = "Nikogenia";
    public static final String API_VERSION = "1.0";

    private SQLConfig sqlConfig;
    private GeneralConfig generalConfig;

    private ZoneId timeZone;

    private SQLManager sqlManager;

    private APIServer apiServer;

    private DockerManager dockerManager;

    private ServerManager serverManager;


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
        sqlConfig = (SQLConfig) FileConfig.load("./configs/SQLConfig.yaml", SQLConfig.class);
        sqlConfig.save("./configs/SQLConfig.yaml");
        generalConfig = new GeneralConfig();

        System.out.println("Load SQL manager");
        sqlManager = new SQLManager();
        if (!sqlManager.isConnected()) return;

        timeZone = ZoneId.of(generalConfig.getTimeZone());
        System.out.println("Using time zone " + timeZone.getId());

        System.out.println("Load API server");
        apiServer = new APIServer();
        if (!apiServer.isRunning()) return;
        apiServer.start();

        System.out.println("Load Docker manager");
        dockerManager = new DockerManager();
        if (!dockerManager.isConnected()) return;

        System.out.println("Load server manager");
        serverManager = new ServerManager();

        System.out.println("Start servers");
        serverManager.run();

    }

    public void exit() {

        if (apiServer != null) apiServer.exit();

        if (serverManager != null) serverManager.exit();

        if (dockerManager != null) dockerManager.exit();

        if (sqlManager != null) sqlManager.exit();

        System.out.println("Exited");

    }

    public static Main getInstance() { return instance; }

    public static SQLConfig getSQLConfig() { return instance.sqlConfig; }

    public static GeneralConfig getGeneralConfig() { return instance.generalConfig; }

    public static ZoneId getTimeZone() { return instance.timeZone; }

    public static SQLManager getSQLManager() { return instance.sqlManager; }

    public static APIServer getAPIServer() { return instance.apiServer; }

    public static DockerManager getDockerManager() { return instance.dockerManager; }

    public static ServerManager getServerManager() { return instance.serverManager; }

}
