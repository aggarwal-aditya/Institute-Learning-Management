package org.academics;

import java.util.Calendar;
import java.util.Date;

public class CurrentDate {
    private static CurrentDate instance = null;
    private Calendar calendar;

    private CurrentDate() {
        calendar = Calendar.getInstance();
    }

    public static CurrentDate getInstance() {
        if (instance == null) {
            instance = new CurrentDate();
        }
        return instance;
    }

    public Date getCurrentDate() {
        return calendar.getTime();
    }

    public void overwriteCurrentDate(int year, int month, int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
    }
}
