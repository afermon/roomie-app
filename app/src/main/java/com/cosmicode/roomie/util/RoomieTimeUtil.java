package com.cosmicode.roomie.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class RoomieTimeUtil {

    private static DateTimeFormatter instantDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withLocale(Locale.ROOT)
            .withChronology(ISOChronology.getInstanceUTC());

    public static DateTime instantUTCStringToDateTime(String date){
        return instantDateTimeFormatter.parseDateTime(date).withZone(DateTimeZone.forID(TimeZone.getDefault().getID()));
    }

    public static String dateTimeToInstantUTCString(DateTime datetime){
        return instantDateTimeFormatter.print(datetime);
    }

    public static Calendar instantUTCStringToCalendar(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(instantUTCStringToDateTime(date).toDate());
        return calendar;
    }

    public static String instantUTCStringToLocalDateTimeString(String date){
        return instantUTCStringToDateTime(date).toString("yyyy/MM/dd hh:mm a");
    }

    public static String instantUTCStringToLocalDateString(String date){
        return instantUTCStringToDateTime(date).toString("yyyy/MM/dd");
    }

    public static String instantUTCStringToLocalTimeString(String date){
        return instantUTCStringToDateTime(date).toString("hh:mm a");
    }

    public static DateTime calendarToDateTime(Calendar calendar){
        return new DateTime(calendar.getTimeInMillis(), DateTimeZone.forID(calendar.getTimeZone().getID()));
    }

    public static String calendarToInstantUTCString(Calendar calendar){
        return dateTimeToInstantUTCString(calendarToDateTime(calendar));
    }

    public static String calendarToDateString(Calendar calendar){
        return  instantUTCStringToLocalDateString(calendarToInstantUTCString(calendar));
    }

    public static String calendarToTimeString(Calendar calendar){
        return  instantUTCStringToLocalTimeString(calendarToInstantUTCString(calendar));
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null)
            return false;
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
