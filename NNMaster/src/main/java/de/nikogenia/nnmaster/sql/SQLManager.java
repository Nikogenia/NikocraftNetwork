package de.nikogenia.nnmaster.sql;

import de.nikogenia.nnmaster.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLManager {

    private boolean connected;

    private Connection connection;
    private Statement statement;

    public SQLManager() {

        try {

            connection = DriverManager.getConnection("jdbc:mysql://" + Main.getSQLConfiguration().getIp() +
                    ":" + Main.getSQLConfiguration().getPort() + "/" + Main.getSQLConfiguration().getDatabase(),
                    Main.getSQLConfiguration().getUser(), Main.getSQLConfiguration().getPassword());

            statement = connection.createStatement();

            createTables();

            connected = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void createTables() throws SQLException {

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
                    enabled BOOLEAN NOT NULL DEFAULT false,
                    created TIMESTAMP NOT NULL DEFAULT current_timestamp,
                    mode TINYINT NOT NULL DEFAULT 0
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

}
