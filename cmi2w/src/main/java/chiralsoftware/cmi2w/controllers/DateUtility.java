package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.security.JpaUserDetails;
import static java.lang.System.currentTimeMillis;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Simple class for formatting dates
 *
 */
public final class DateUtility {

    private static final Logger LOG = Logger.getLogger(DateUtility.class.getName());

    private static TimeZone getTz() {
        return ((JpaUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTimeZone();
    }

    /**
     * Format a time as a year-month-day
     */
    public String showDay(long l) {
        if (l == 0)
            return "-";

        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(l));
    }

    /**
     * Format a time as a year-month-day
     */
    public String showTime(long l) {
        if (l == 0)
            return "-";
        final DateFormat df =
                new SimpleDateFormat("MMM dd y, hh:mma");
        df.setTimeZone(getTz());
        return df.format(new Date(l));
    }

    /**
     * Format a time as 11:14 AM, Tue, 8 Oct
     */
    public String showQuickTime(Long l) {
        if (l == null)
            return "-";
        if (l == 0)
            return "-";
        final DateFormat df = new SimpleDateFormat("hh:mm a, EEE, MMM d");
        df.setTimeZone(getTz());
        return df.format(new Date(l));
    }

    /**
     * The datetime-local input type needs a format like:
     *
     * @param l
     * @return
     */
    public String showDateFormDatetimeLocal(Long l) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        final long t = l == null ? currentTimeMillis() : l.longValue();

        df.setTimeZone(getTz());
        return df.format(new Date(t));
    }

    private static final long milsInDay = 1000 * 60 * 60 * 24;

    /**
     * Show a date in green if it should be called in the next day or two, and
     * red if it is over-due for a call
     */
    public String showDateColor(Long d) {
        if (d == null)
            return "";
        if (d == 0)
            return "";
        final long currentTime = currentTimeMillis();

        if (d < currentTime + milsInDay)
            return "color: red";
        if (d < currentTime + 3 * milsInDay)
            return "color: green";
        return "color: blue";
    }

    /** This takes a string in the input form from a HTML5 datetime-local input */
    public static Date parseInputString(String s) {
        if (s == null)
            return null;
        try {
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            df.setTimeZone(getTz());
            final Date d = df.parse(s);
            return d;
        } catch (ParseException pe) {
            LOG.log(INFO, "Couldn't parse string: " + s, pe);
        }
        return null;
    }
}
