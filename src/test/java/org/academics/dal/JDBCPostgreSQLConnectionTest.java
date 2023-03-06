package org.academics.dal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class JDBCPostgreSQLConnectionTest {
    private static JDBCPostgreSQLConnection jdbcPostgreSQLConnection;

    @BeforeAll
    public static void setUp() {
        jdbcPostgreSQLConnection = JDBCPostgreSQLConnection.getInstance();
    }

    @Test
    public void testGetInstance() {
        assertNotNull(jdbcPostgreSQLConnection);
    }

    @Test
    public void testGetConnection() throws SQLException {
        Connection connection = jdbcPostgreSQLConnection.getConnection();
        assertNotNull(connection);
        connection.close();
    }

    @Test
    public void testMultipleGetInstanceCallsReturnSameInstance() {
        JDBCPostgreSQLConnection instance1 = JDBCPostgreSQLConnection.getInstance();
        JDBCPostgreSQLConnection instance2 = JDBCPostgreSQLConnection.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testConnectionNotNullAfterMultipleGetConnectionCalls() throws SQLException {
        Connection connection1 = jdbcPostgreSQLConnection.getConnection();
        connection1.close();
        Connection connection2 = jdbcPostgreSQLConnection.getConnection();
        assertNotNull(connection1);
        assertNotNull(connection2);
        connection2.close();
    }


    @AfterAll
    public static void tearDown() throws SQLException {
        if (jdbcPostgreSQLConnection != null) {
            Connection connection = jdbcPostgreSQLConnection.getConnection();
            connection.close();
        }
    }
}
