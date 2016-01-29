package net.jeedup.util

import java.text.SimpleDateFormat

/**
 * Created by zack on 6/13/14.
 */
//@CompileStatic
class DateTimeUtil {

    public static String secsToMMSS(Long secsIn) {

        Long hours = secsIn / 3600
        Long remainder = secsIn % 3600
        Long minutes = remainder / 60
        Long seconds = remainder % 60

        return ((minutes < 10 ? "0" : "") + minutes + ":" + (seconds< 10 ? "0" : "") + seconds )
    }

    public static Long hhMMSSToSecs(String string) {
        String[] vals = string.split(':')
        int length = vals.length
        Long seconds = Long.parseLong(vals[length - 1])
        Long minutes = Long.parseLong(vals[length - 2])
        Long hours = 0L
        if (length > 2) {
            hours = Long.parseLong(vals[length - 3])
        }

        seconds += (minutes * 60)
        seconds += (hours * 60 * 60)

        return seconds
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy, h:mm a z")
        return sdf.format(date)
    }

    public static Date iso8601ToDate(String iso8601) {
        return parseDateWithFormat(iso8601, "yyyy-MM-dd'T'HH:mm:ss")
    }

    public static Date rfc2822ToDate(String rfc2882) {
        return parseDateWithFormat(rfc2882, "EEE, dd MMM yyyy HH:mm:ss zzz")
    }

    public static Date parseDate(String date) {
        String[] formats = [
            "yyyy-MM-dd",
            "EEE MMM dd HH:mm:ss z yyyy",
            "yyyy-MM-dd'T'HH:mm:ss",
            "MMMM dd, yyyy, h:mm a z",
            "MM-dd-yy hh:mma",
            "EEE MMM dd HH:mm:ss z yyyy",
            "MMMM dd, yyyy",
            "yyyy/MM/dd",
            "EEE, dd MMM yyyy",
            "yyyy-M-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss'+0000'"
        ]

        for (String format : formats) {
            Date result = parseDateWithFormat(date, format)
            if (result != null) {
                return result
            }
        }
        return null
    }

    public static Date parseDateWithFormat(String date, String format) {
        Date result = null
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format)
            result = sdf.parse(date)
        } catch (Exception e) { }

        return result
    }

    /**
     *
     * Parses seconds (in/out) from a string.
     * Can be 00:00:00 format, blank/-1 for "none"
     * and a normal string
     *
     * @param str formatted seconds
     * @return Long seconds
     */
    public static Long parseSecsFromString(String str) {
        Long result = null
        if (str == null) {
            return result
        }

        if (str.contains(':')) {
            try {
                result = DateTimeUtil.hhMMSSToSecs(str)
            } catch (Exception e) {
                System.err.println(e.getMessage())
            }
        } else if (str == "" || str == "-1") {
            result = -1L;
        } else {
            try {
                result = Long.parseLong(str)
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage())
            }
        }

        return result
    }
}
