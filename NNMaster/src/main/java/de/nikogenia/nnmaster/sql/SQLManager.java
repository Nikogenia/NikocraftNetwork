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

            connected = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

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
