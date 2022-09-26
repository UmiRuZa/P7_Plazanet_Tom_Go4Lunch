package com.project.go4lunch;

import org.junit.Test;

import static org.junit.Assert.*;

import com.project.go4lunch.fragments.bottom.chat.MessageViewHolder;
import com.project.go4lunch.fragments.bottom.places.PlacesAdapter;
import com.project.go4lunch.fragments.bottom.places.PlacesViewHolder;
import com.project.go4lunch.fragments.bottom.places.PlacesViewHolder_ViewBinding;
import com.project.go4lunch.fragments.drawer.SettingsFragment;
import com.project.go4lunch.model.Message;
import com.project.go4lunch.model.restaurant.Result;
import com.project.go4lunch.ui.MainActivity;

import java.time.Year;
import java.util.Calendar;
import java.util.Date;


public class Go4LunchUnitTest {
    @Test
    public void getRating_isCorrect() {
        Result testResult = new Result();
        testResult.setRating(4.0);

        float testedRating = PlacesViewHolder.getRating(testResult);
        float expectedRating = (float) 2.4;

        assertEquals(expectedRating, testedRating, (float) 0.0);
    }

    @Test
    public void convertDateToHour_isCorrect() {
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.set(Calendar.HOUR_OF_DAY, 15);
        testCalendar.set(Calendar.MINUTE, 15);
        Date testDate = testCalendar.getTime();

        String expectedDate = "15:15";

        assertEquals(expectedDate, MessageViewHolder.convertDateToHour(testDate));
    }

    @Test
    public void progressFormat_U_10_isCorrect() {
        int testProgress_U_10 = 6;
        String expectedProgress_U_10 = "600m";

        assertEquals(expectedProgress_U_10, SettingsFragment.progressFormat(testProgress_U_10));
    }

    @Test
    public void progressFormat_O_10_isCorrect() {
        int testProgress_O_10 = 42;
        String expectedProgress_O_10 = "4,2 km";

        assertEquals(expectedProgress_O_10, SettingsFragment.progressFormat(testProgress_O_10));
    }

    @Test
    public void calculateTimeDiff_PM() {
        Calendar testCurrentTime = Calendar.getInstance();
        testCurrentTime.set(Calendar.YEAR, 2022);
        testCurrentTime.set(Calendar.MONTH, 2);
        testCurrentTime.set(Calendar.DAY_OF_MONTH, 15);
        testCurrentTime.set(Calendar.HOUR_OF_DAY, 15);
        testCurrentTime.set(Calendar.MINUTE, 15);
        testCurrentTime.set(Calendar.SECOND, 0);
        testCurrentTime.set(Calendar.MILLISECOND, 0);

        Calendar testDueTime = Calendar.getInstance();
        testDueTime.set(Calendar.YEAR, 2022);
        testDueTime.set(Calendar.MONTH, 2);
        testDueTime.set(Calendar.DAY_OF_MONTH, 15);

        long expectedTimeDiff = 74700000;

        assertEquals(expectedTimeDiff, MainActivity.calculateTimeDiff(testCurrentTime, testDueTime));
    }

    @Test
    public void calculateTimeDiff_AM() {
        Calendar testCurrentTime = Calendar.getInstance();

        testCurrentTime.set(Calendar.YEAR, 2022);
        testCurrentTime.set(Calendar.MONTH, 2);
        testCurrentTime.set(Calendar.DAY_OF_MONTH, 15);
        testCurrentTime.set(Calendar.HOUR_OF_DAY, 10);
        testCurrentTime.set(Calendar.MINUTE, 42);
        testCurrentTime.set(Calendar.SECOND, 0);
        testCurrentTime.set(Calendar.MILLISECOND, 0);


        Calendar testDueTime = Calendar.getInstance();

        testDueTime.set(Calendar.YEAR, 2022);
        testDueTime.set(Calendar.MONTH, 2);
        testDueTime.set(Calendar.DAY_OF_MONTH, 15);


        long expectedTimeDiff = 4680000;

        assertEquals(expectedTimeDiff, MainActivity.calculateTimeDiff(testCurrentTime, testDueTime));
    }
}