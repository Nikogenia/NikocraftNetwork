package de.nikogenia.nnmaster.sql;

import de.nikogenia.nnmaster.Main;
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

        statement.execute("""
                CREATE TABLE IF NOT EXISTS general (
                    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(64) NOT NULL UNIQUE,
                    value LONGTEXT DEFAULT null
                );
                """);

        statement.execute("""
                CREATE TABLE IF NOT EXISTS server (
                    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(32) NOT NULL UNIQUE,
                    address VARCHAR(15) NOT NULL UNIQUE,
                    agent VARCHAR(32) NOT NULL DEFAULT 'master',
                    type VARCHAR(32) NOT NULL DEFAULT 'paper',
                    enabled BOOLEAN NOT NULL DEFAULT false,
                    created TIMESTAMP NOT NULL DEFAULT current_timestamp,
                    mode VARCHAR(32) NOT NULL DEFAULT 'off'
                );
                """);

        statement.execute("""
                CREATE TABLE IF NOT EXISTS player (
                    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                    uuid CHAR(36) NOT NULL UNIQUE,
                    name VARCHAR(16) NOT NULL UNIQUE,
                    online BOOLEAN NOT NULL DEFAULT false,
                    server BIGINT UNSIGNED NOT NULL DEFAULT 1,
                    time_played INT UNSIGNED NOT NULL DEFAULT 0,
                    first_joined TIMESTAMP DEFAULT null,
                    number_joined INT UNSIGNED NOT NULL DEFAULT 0,
                    last_disconnect TIMESTAMP DEFAULT null,
                    FOREIGN KEY (server) REFERENCES server(id)
                );
                """);

    }

    public void loadGeneralConfig() throws SQLException {

        Main.getGeneralConfig().setAPIPort(Integer.parseInt(loadGeneralConfigEntry("api_port", "42420")));
        Main.getGeneralConfig().setAPIKey(loadGeneralConfigEntry("api_key", RandomStringUtils.randomAlphanumeric(32)));
        Main.getGeneralConfig().setName(loadGeneralConfigEntry("name", "pixplex"));
        Main.getGeneralConfig().setFullName(loadGeneralConfigEntry("full_name", "PixPlex"));
        Main.getGeneralConfig().setDockerPrefix(loadGeneralConfigEntry("docker_prefix", "pixplex-"));
        Main.getGeneralConfig().setRootPath(loadGeneralConfigEntry("root_path", "/root/pixplex/"));
        Main.getGeneralConfig().setDockerNetwork(loadGeneralConfigEntry("docker_network", "pixplex"));
        Main.getGeneralConfig().setTimeZone(loadGeneralConfigEntry("time_zone", "Europe/Berlin"));

    }

    public String loadGeneralConfigEntry(String name, String defaultValue) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("""
                SELECT value FROM general WHERE name = ?;
                """);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) return rs.getString("value");

        saveGeneralConfigEntry(name, defaultValue);

        return defaultValue;

    }

    public void saveGeneralConfigEntry(String name, String value) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("""
                INSERT INTO general (name, value) VALUES (?, ?)
                ON DUPLICATE KEY UPDATE value = ?;
                """);
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
