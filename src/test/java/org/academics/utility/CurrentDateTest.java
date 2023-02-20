package org.academics.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrentDateTest {
    @Test
    void getCurrentDate() {
        CurrentDate currentDate = CurrentDate.getInstance();
        assertNotNull(currentDate.getCurrentDate());
    }

    @Test
    void overwriteCurrentDate() {
        CurrentDate currentDate = CurrentDate.getInstance();
        currentDate.overwriteCurrentDate(2018, 9, 5); // Set to October 5th, 2018
        assertEquals(2018, currentDate.getCurrentDate().getYear()+1900);
        assertEquals(9, currentDate.getCurrentDate().getMonth()); // October is month 9
        assertEquals(5, currentDate.getCurrentDate().getDate()); // Check the day of the month
    }
    @Test
    void getInstance() {
        CurrentDate currentDate = CurrentDate.getInstance();
        assertNotNull(currentDate);
    }


}