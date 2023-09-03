package de.nikogenia.nnproxy.sql;

import de.nikogenia.nnproxy.Main;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.*;

public class SQLManager {

    private boolean connected;

    private Connection connection;
    private Statement statement;

    public SQLManager() {

        try {

            connection = DriverManager.getConnection("jdbc:mysql://" + Main.getSQLConfig().getIp() +
                    ":" + Main.getSQLConfig().getPort() + "/" + Main.getSQLConfig().getDatabase(),
                    Main.getSQLConfig().getUser(), Main.getSQLConfig().getPassword());

            statement = connection.createStatement();

            createTables();

            loadGeneralConfig();

            connected = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createTables() throws SQLException {

    }

    public void loadGeneralConfig() throws SQLException {

        Main.getGeneralConfig().setAPIKey(loadGeneralConfigEntry("api_key", RandomStringUtils.randomAlphanumeric(32)));
        Main.getGeneralConfig().setName(loadGeneralConfigEntry("name", "pixplex"));
        Main.getGeneralConfig().setFullName(loadGeneralConfigEntry("full_name", "PixPlex"));
        Main.getGeneralConfig().setTimeZone(loadGeneralConfigEntry("time_zone", "Europe/Berlin"));

    }

    public String loadGeneralConfigEntry(String name, String defaultValue) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("SELECT value FROM general WHERE name = ?;");
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) return rs.getString("value");

        saveGeneralConfigEntry(name, defaultValue);

        return defaultValue;

    }

    public void saveGeneralConfigEntry(String name, String value) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO general (name, value) VALUES (?, ?) ON DUPLICATE KEY UPDATE value = ?;");
        stmt.setString(1, name);
        stmt.setString(2, value);
        stmt.setString(3, value);
        stmt.executeUpdate();

    }

    public PreparedStatement getPreparedStatement(String sql) throws SQLException {

        return connection.prepareStatement(sql);

    }

    public void exit() {

        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean isConnected() {
        return connected;
    }

    public Statement getStatement() {
        return statement;
    }

}
