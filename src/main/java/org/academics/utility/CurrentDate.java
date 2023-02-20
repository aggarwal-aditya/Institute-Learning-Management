package org.academics.utility;

import java.time.LocalDate;
import java.time.ZoneId;

public class CurrentDate {
    private static CurrentDate instance = null;
    private LocalDate currentDate;

    private CurrentDate() {
        currentDate = LocalDate.now();
    }

    public static CurrentDate getInstance() {
        if (instance == null) {
            instance = new CurrentDate();
        }
        return instance;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void overwriteCurrentDate(int year, int month, int day) {
        currentDate = LocalDate.of(year, month, day);
    }
}
