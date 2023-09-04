package de.nikogenia.nnproxy;

import de.nikogenia.nnproxy.api.APIClient;
import de.nikogenia.nnproxy.config.APIConfig;
import de.nikogenia.nnproxy.config.GeneralConfig;
import de.nikogenia.nnproxy.config.SQLConfig;
import de.nikogenia.nnproxy.listeners.ConnectionListeners;
import de.nikogenia.nnproxy.sql.SQLManager;
import de.nikogenia.nnproxy.utils.FileConfig;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private APIClient apiClient;

    @Override
    public void onEnable() {

        instance = this;

        getLogger().info("Load configurations");
        sqlConfig = (SQLConfig) FileConfig.load("./configs/SQLConfig.yaml", SQLConfig.class);
        sqlConfig.save("./configs/SQLConfig.yaml");
        apiConfig = (APIConfig) FileConfig.load("./configs/APIConfig.yaml", APIConfig.class);
        apiConfig.save("./configs/APIConfig.yaml");
        generalConfig = new GeneralConfig();

        getLogger().info("Load SQL manager");
        sqlManager = new SQLManager();
        if (!sqlManager.isConnected()) return;

        timeZone = ZoneId.of(generalConfig.getTimeZone());
        getLogger().info("Using time zone " + timeZone.getId());

        apiClient = new APIClient();
        apiClient.start();

        getProxy().getPluginManager().registerListener(this, new ConnectionListeners());

    }

    @Override
    public void onDisable() {

        if (apiClient != null) apiClient.exit();

        if (sqlManager != null) sqlManager.exit();

        getLogger().info("Exited");

    }

    public void updateServers(String[] servers) {

        getProxy().getServers().clear();

        for (String server : servers) {
            String name = server.split("#")[0];
            String address = server.split("#")[1];
            getProxy().getServers().put(name, getProxy().constructServerInfo(
                    name, new InetSocketAddress(address, 25565), "", false));
        }

    }

    public String getMotd() {

        try {

            ResultSet rs = sqlManager.getStatement().executeQuery(
                    "SELECT line1, line2 FROM motd WHERE name = (SELECT value FROM general WHERE name = 'motd')");

            if (!rs.next()) return generalConfig.getFullName() + " - ERROR";

            return rs.getString("line1") + "\n" + rs.getString("line2");

        } catch (SQLException e) {
            e.printStackTrace();
            return generalConfig.getFullName() + " - ERROR";
        }

    }

    public static Main getInstance() { return instance; }

    public static SQLConfig getSQLConfig() { return instance.sqlConfig; }

    public static APIConfig getAPIConfig() { return instance.apiConfig; }

    public static GeneralConfig getGeneralConfig() { return instance.generalConfig; }

    public static ZoneId getTimeZone() { return instance.timeZone; }

    public static SQLManager getSQLManager() { return instance.sqlManager; }

    public static APIClient getAPIClient() { return instance.apiClient; }

}
