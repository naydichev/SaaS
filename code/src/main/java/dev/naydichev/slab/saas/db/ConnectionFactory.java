package dev.naydichev.slab.saas.db;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.LambdaRuntime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static Connection connection;
    private static final LambdaLogger LOG = LambdaRuntime.getLogger();

    public static Connection getConnection() {
        return connection;
    }

    public static void createConnection(String jdbcUrl, String username, String password) {
        try {
            DriverManager.setLoginTimeout(5);
            LOG.log(String.format("Connecting to '%s' with username '%s'", jdbcUrl, username));
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Could not connect to DB", e);
        }
    }

}
