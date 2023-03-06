package org.academics.dal;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class dbUtilsTest {

    @Test
    public void testValidateEventTime() {
        try {
            // Set up test data
            String eventType = " grades_submission";
            int year = 2023;
            int semester = 1;
            LocalDate currentDate = LocalDate.of(2023, 3, 6);
            ResultSet expectedResultSet = null;

            // Execute method under test
            ResultSet actualResultSet = dbUtils.validateEventTime(eventType, year, semester, currentDate);

            // Assert expected result
            assertNotNull(actualResultSet);
        } catch (SQLException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testGetCurrentSession() {
        try {
            // Set up test data
            LocalDate currentDate = LocalDate.of(2023, 3, 6);
            ResultSet expectedResultSet = null;

            // Execute method under test
            ResultSet actualResultSet = dbUtils.getCurrentSession(currentDate);

            // Assert expected result
            assertNotNull(actualResultSet);
        } catch (SQLException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testGetDepartmentIDs() {
        try {
            // Execute method under test
            ResultSet actualResultSet = dbUtils.getDepartmentIDs();
            // Assert expected result
            assertNotNull(actualResultSet);
        } catch (SQLException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
