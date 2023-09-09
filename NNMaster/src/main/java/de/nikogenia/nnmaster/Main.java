package de.nikogenia.nnmaster;

import de.nikogenia.nnmaster.api.APIServer;
import de.nikogenia.nnmaster.config.GeneralConfig;
import de.nikogenia.nnmaster.config.SQLConfig;
import de.nikogenia.nnmaster.control.ControlManager;
import de.nikogenia.nnmaster.docker.DockerManager;
import de.nikogenia.nnmaster.server.ServerManager;
import de.nikogenia.nnmaster.sql.SQLManager;
import de.nikogenia.nnmaster.utils.FileConfig;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

public class Main {

    private static Main instance;

    public static final String NAME = "NikocraftNetwork";
    public static final String VERSION = "0.0.1";
    public static final String AUTHOR = "Nikogenia";
    public static final String API_VERSION = "1.0";

    private boolean debug;

    private Logger logger;

    private ReentrantLock lock;

    private SQLConfig sqlConfig;
    private GeneralConfig generalConfig;

    private ZoneId timeZone;

    private SQLManager sqlManager;

    private APIServer apiServer;

    private DockerManager dockerManager;

    private ServerManager serverManager;

    private ControlManager controlManager;


    public static void main(String[] args) {

        instance = new Main();

        instance.debug = false;
        for (String arg : args) {
            if (arg.equals("-d") | arg.equals("--debug")) {
                instance.debug = true;
                break;
            }
        }

        instance.run();

    }

    public void run() {

        lock = new ReentrantLock();

        if (!initializeLogger()) System.exit(1);

        logger.info("Author: " + AUTHOR);
        logger.info("Version: " + VERSION);
        logger.info("API version: " + API_VERSION);
        logger.info("Debug mode: " + debug);

        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));

        logger.info("Load configurations");
        sqlConfig = (SQLConfig) FileConfig.load("./configs/SQLConfig.yaml", SQLConfig.class);
        sqlConfig.save("./configs/SQLConfig.yaml");
        generalConfig = new GeneralConfig();

        logger.info("Load SQL manager");
        sqlManager = new SQLManager();
        if (!sqlManager.isConnected()) return;

        timeZone = ZoneId.of(generalConfig.getTimeZone());
        logger.info("Using time zone " + timeZone.getId());

        logger.info("Load API server");
        apiServer = new APIServer();
        if (!apiServer.isRunning()) return;
        apiServer.start();

        logger.info("Load Docker manager");
        dockerManager = new DockerManager();
        if (!dockerManager.isConnected()) return;

        logger.info("Load server manager");
        serverManager = new ServerManager();

        logger.info("Load control manager");
        controlManager = new ControlManager();

        logger.info("Start servers");
        serverManager.run();

    }

    public void exit() {

        try {

            if (controlManager != null) controlManager.exit();

            if (apiServer != null) apiServer.exit();

            if (serverManager != null) serverManager.exit();

            if (dockerManager != null) dockerManager.exit();

            if (sqlManager != null) sqlManager.exit();

            logger.info("Exited");

        }
        finally {
            CustomLogManager.resetFinally();
        }

    }

    static {
        System.setProperty("java.util.logging.manager", CustomLogManager.class.getName());
    }

    public static class CustomLogManager extends LogManager {
        static CustomLogManager instance;
        public CustomLogManager() { instance = this; }
        @Override public void reset() {}
        private void reset0() { super.reset(); }
        public static void resetFinally() { instance.reset0(); }
    }

    private boolean initializeLogger() {

        File loggingDirectory = new File("./logs");

        if (!loggingDirectory.exists()) loggingDirectory.mkdirs();

        logger = Logger.getLogger("Master");

        if (debug) logger.setLevel(Level.FINEST);
        else logger.setLevel(Level.CONFIG);

        logger.setUseParentHandlers(false);

        Formatter formatter = new Formatter() {

            @Override
            public String format(LogRecord record) {

                String result = "[" + DateTimeFormatter.ofPattern("dd LLL HH:mm:ss").format(LocalDateTime.now()) + "] ";

                result += "[" + record.getLevel() + "] ";

                result += "[" + record.getLoggerName() + "] ";

                return result + record.getMessage() + "\n";

            }

        };

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logger.getLevel());
        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler("./logs/log_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now()) + ".log");
        } catch (IOException e) {
            System.out.println("SEVERE: Fatal error occurred on initializing main logger!");
            e.printStackTrace();
            return false;
        }

        consoleHandler.setFormatter(formatter);
        fileHandler.setFormatter(formatter);

        logger.addHandler(consoleHandler);
        logger.addHandler(fileHandler);

        return true;

    }

    public static Main getInstance() { return instance; }

    public static Logger getLogger() { return instance.logger; }

    public static SQLConfig getSQLConfig() { return instance.sqlConfig; }

    public static GeneralConfig getGeneralConfig() { return instance.generalConfig; }

    public static ZoneId getTimeZone() { return instance.timeZone; }

    public static SQLManager getSQLManager() { return instance.sqlManager; }

    public static APIServer getAPIServer() { return instance.apiServer; }

    public static DockerManager getDockerManager() { return instance.dockerManager; }

    public static ServerManager getServerManager() { return instance.serverManager; }

}
