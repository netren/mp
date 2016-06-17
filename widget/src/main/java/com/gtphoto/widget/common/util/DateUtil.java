package com.gtphoto.widget.common.util;


import android.app.Application;

import com.gtphoto.widget.R;
import com.gtphoto.widget.common.box.Box;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by kennymac on 15/9/29.
 */
public class DateUtil {

    public static String getForJsDate(Date date) {
        return String.format("\"%tF\",", date);
    }
    public static String getShowDateString(Date date) {
        return getShowDateString(date, false);
    }



    public static String getShowDateString(Date date, boolean isUseFull) {

        Calendar cale = (Calendar)Calendar.getInstance().clone();

        cale.setTime(date);
        int year = cale.get(Calendar.YEAR);

        Date now = new Date();

        Calendar nowCale = (Calendar)Calendar.getInstance().clone();
        nowCale.setTime(now);
        if (year == nowCale.get(Calendar.YEAR) && !isUseFull) {
            return Box.get(Application.class).getString(R.string.month_day_format, cale.get(Calendar.MONTH) + 1, cale.get(Calendar.DAY_OF_MONTH));
        }
        else {
            return Box.get(Application.class).getString(R.string.year_month_day_format, cale.get(Calendar.MONTH) + 1, cale.get(Calendar.DAY_OF_MONTH), cale.get(Calendar.YEAR));
        }
    }

    static public boolean isSameDay(Date lhs , Date rhs ) {

        Calendar lhscale = (Calendar)Calendar.getInstance().clone();
        Calendar rhscale = (Calendar)Calendar.getInstance().clone();
        lhscale.setTime(lhs);
        rhscale.setTime(rhs);
        return lhscale.get(Calendar.YEAR) == rhscale.get(Calendar.YEAR)
                && lhscale.get(Calendar.MONTH) == rhscale.get(Calendar.MONTH)
                && lhscale.get(Calendar.DAY_OF_MONTH) == rhscale.get(Calendar.DAY_OF_MONTH);

    }

    public static class DateDelta {
        public int year = 0;
        public int month = 0;
        public int date = 0;
        public boolean isBefore;
    }

    static int getMonthTotal (int month, int year) {
        if (month < 7) {
            if (month != 1) {
                return 31 - month % 2;
            }
            else {
                if (year % 4 == 0) {
                    if (year % 100 != 0) {
                        return 29;
                    }
                    else if (year % 400 == 0) {
                        return 29;
                    }

                }
                return 28;
            }
        }
        else {
            return 30 + month % 2;
        }
    }

    public static Calendar getNewCalendar(Date date) {
        Calendar cale = (Calendar)Calendar.getInstance().clone();

        cale.setTime(date);
        return cale;
    }
    private static int getPreviewMonthTotal(Date date) {
        Calendar cale = (Calendar)Calendar.getInstance().clone();

        cale.setTime(date);

        int nowYear = cale.get(Calendar.YEAR);
        int nowMonth = cale.get(Calendar.MONTH);
        --nowMonth;
        if (nowMonth < 0) {
            nowMonth = 11;
            --nowYear;
        }
        return getMonthTotal(nowMonth, nowYear);
    }

    static public DateDelta getDateDelta(Date nowDate, Date beforeDate) {

        Calendar nowCale = getNewCalendar(nowDate);
        Calendar beforeCale = getNewCalendar(beforeDate);

//        int deltaYear = nowCale.get(Calendar.YEAR) - beforeCale.get(Calendar.YEAR);

        int deltaDate = (int)((nowDate.getTime() - beforeDate.getTime()) / 1000 / 60 / 60 / 24);
        if (deltaDate < 0) {
            DateDelta ret = getDateDelta(beforeDate, nowDate);
            ret.isBefore = true;

            return ret;
        }


        int deltaDay = nowCale.get(Calendar.DAY_OF_MONTH) - beforeCale.get(Calendar.DAY_OF_MONTH);
        int deltaMonth = nowCale.get(Calendar.MONTH) - beforeCale.get(Calendar.MONTH);
        int deltaYear = nowCale.get(Calendar.YEAR) - beforeCale.get(Calendar.YEAR);
//        int needMinusMonth = 0;
        if (deltaDay < 0) {
            deltaDay = getPreviewMonthTotal(nowDate) + deltaDay;
            --deltaMonth;

        }

        if (deltaMonth < 0) {
            --deltaYear;
            deltaMonth = 12 + deltaMonth;
        }

        DateDelta ret = new DateDelta();
        ret.year = deltaYear;
        ret.month = deltaMonth;
        ret.date = deltaDay;
        ret.isBefore = false;
        return ret;
    }


    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }

    public static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

}
