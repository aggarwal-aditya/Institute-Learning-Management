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
        assert ((currentDate).getCurrentDate().toString().equals("2018-09-05"));
    }
    @Test
    void getInstance() {
        CurrentDate currentDate = CurrentDate.getInstance();
        assertNotNull(currentDate);
    }


}