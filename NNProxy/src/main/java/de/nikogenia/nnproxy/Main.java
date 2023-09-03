package de.nikogenia.nnproxy;

import de.nikogenia.nnproxy.config.APIConfig;
import de.nikogenia.nnproxy.config.GeneralConfig;
import de.nikogenia.nnproxy.config.SQLConfig;
import de.nikogenia.nnproxy.sql.SQLManager;
import de.nikogenia.nnproxy.utils.FileConfig;
import net.md_5.bungee.api.plugin.Plugin;

import java.time.ZoneId;

public final class Main extends Plugin {

    private static Main instance;

    public static final String NAME = "NikocraftNetwork";
    public static final String VERSION = "0.0.1";
    public static final String AUTHOR = "Nikogenia";
    public static final String API_VERSION = "1.0";

    private SQLConfig sqlConfig;
    private APIConfig apiConfig;
    private GeneralConfig generalConfig;

    private ZoneId timeZone;

    private SQLManager sqlManager;

    @Override
    public void onEnable() {

        instance = this;

        System.out.println("Load configurations");
        sqlConfig = (SQLConfig) FileConfig.load("./configs/SQLConfig.yaml", SQLConfig.class);
        sqlConfig.save("./configs/SQLConfig.yaml");
        apiConfig = (APIConfig) FileConfig.load("./configs/APIConfig.yaml", APIConfig.class);
        apiConfig.save("./configs/APIConfig.yaml");
        generalConfig = new GeneralConfig();

        System.out.println("Load SQL manager");
        sqlManager = new SQLManager();
        if (!sqlManager.isConnected()) return;

        timeZone = ZoneId.of(generalConfig.getTimeZone());
        System.out.println("Using time zone " + timeZone.getId());



    }

    @Override
    public void onDisable() {

        if (sqlManager != null) sqlManager.exit();

        System.out.println("Exited");

    }

    public static Main getInstance() { return instance; }

    public static SQLConfig getSQLConfig() { return instance.sqlConfig; }

    public static APIConfig getAPIConfig() { return instance.apiConfig; }

    public static GeneralConfig getGeneralConfig() { return instance.generalConfig; }

    public static ZoneId getTimeZone() { return instance.timeZone; }

    public static SQLManager getSQLManager() { return instance.sqlManager; }

}
