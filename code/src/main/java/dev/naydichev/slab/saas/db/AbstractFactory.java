package dev.naydichev.slab.saas.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractFactory {
    private final Connection connection;

    PreparedStatement getPreparedStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new IllegalArgumentException(
                String.format("Could not create prepared statement: '%s'", sql),
                e
            );
        }
    }
}