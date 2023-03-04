package org.academics.dal;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        Connection connection2 = jdbcPostgreSQLConnection.getConnection();
        assertNotNull(connection1);
        assertNotNull(connection2);
        connection1.close();
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
