package com.cosmicode.roomie.util;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Locale;

public class RoomieTimeUtil {

    private static DateTimeFormatter inputDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withLocale(Locale.ROOT)
            .withChronology(ISOChronology.getInstanceUTC());

    private static DateTimeFormatter outputDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZoneUTC();

    public static DateTime instantStringToDateTime(String date){
        return inputDateTimeFormatter.parseDateTime(date);
    }

    public static Calendar dateStringToCalendar(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(instantStringToDateTime(date).toDate());
        return calendar;
    }

    public static String formatInstantStringDateTime(String date){
        return inputDateTimeFormatter.parseDateTime(date).toString("yyyy/MM/dd hh:mm a");
    }

    public static String formatInstantStringDate(String date){
        return inputDateTimeFormatter.parseDateTime(date).toString("yyyy/MM/dd");
    }

    public static String formatInstantStringTime(String date){
        return inputDateTimeFormatter.parseDateTime(date).toString("hh:mm a");
    }

    public static String datetimeToInstantString(DateTime datetime){
        return outputDateTimeFormatter.print(datetime);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null)
            return false;
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
