package org.academics.utility;

import java.time.LocalDate;

public class CurrentDate {
    private static CurrentDate instance = null;
    private LocalDate currentDate;

    /**
     * Private constructor to create an instance of CurrentDate with the current date.
     */
    private CurrentDate() {
        currentDate = LocalDate.now();
    }

    /**
     * Returns an instance of CurrentDate.
     *
     * @return an instance of CurrentDate.
     */
    public static CurrentDate getInstance() {
        if (instance == null) {
            instance = new CurrentDate();
        }
        return instance;
    }

    /**
     * Returns the current date.
     *
     * @return the current date.
     */
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    /**
     * Overwrites the current date with a new date.
     *
     * @param year  the year of the new date.
     * @param month the month of the new date.
     * @param day   the day of the new date.
     */
    public void overwriteCurrentDate(int year, int month, int day) {
        currentDate = LocalDate.of(year, month, day);
    }
}
