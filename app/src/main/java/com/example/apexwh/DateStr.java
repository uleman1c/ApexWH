package com.example.apexwh;

import android.icu.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateStr {

    public static String FromYmdhmsToDmyhms(String value){

        return value.length() < 14 ? "" : value.substring(6, 8) + "." + value.substring(4, 6) + "." + value.substring(0, 4)
                + " " + value.substring(8, 10) + ":" + value.substring(10, 12) + ":" + value.substring(12, 14);

    }

    public static String FromYmdhmsToDmy(String value){

        return value.length() < 8 ? "" : value.substring(6, 8) + "." + value.substring(4, 6) + "." + value.substring(0, 4);

    }

    public static String NowYmdhms() {

        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");

        Calendar calendar = new GregorianCalendar();
        calendar.roll(Calendar.HOUR_OF_DAY, timeZone.getRawOffset() / (3600 * 1000));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        return simpleDateFormat.format(calendar.getTime());

    }

    public static String CalendarToYmd(Calendar calendar) {

        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        return simpleDateFormat.format(calendar.getTime());

    }

}

