package me.paper.skills.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String format(final long miliseconds) {

        if (miliseconds < 0) {
            return "a moment.";
        }

            String output = "";
            final long days = TimeUnit.MILLISECONDS.toDays(miliseconds);
            long millis = miliseconds - TimeUnit.DAYS.toMillis(days);
            final long hours = TimeUnit.MILLISECONDS.toHours(millis);
            millis -= TimeUnit.HOURS.toMillis(hours);
            final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            millis -= TimeUnit.MINUTES.toMillis(minutes);
            final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            if (days > 1L) {
                output = output + days + " days ";
            } else if (days == 1L) {
                output = output + days + " day ";
            }
            if (hours > 1L) {
                output = output + hours + " hours ";
            } else if (hours == 1L) {
                output = output + hours + " hour ";
            }
            if (minutes > 1L) {
                output = output + minutes + " minutes ";
            } else if (minutes == 1L) {
                output = output + minutes + " minute ";
            }
            if (seconds > 1L) {
                output = output + seconds + " seconds ";
            } else if (seconds == 1L) {
                output = output + seconds + " second ";
            }
            return output;
        }
    }