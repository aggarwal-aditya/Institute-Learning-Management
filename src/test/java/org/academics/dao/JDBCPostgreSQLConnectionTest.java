package org.academics.dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JDBCPostgreSQLConnectionTest {

        @Test
        void getInstance() {
            JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
            assertNotNull(jdbc);
        }

        @Test
        void getConnection() {
            JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
            assertNotNull(jdbc.getConnection());
        }

}