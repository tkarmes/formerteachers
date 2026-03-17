package com.formerteachers.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    public static String formatPostedDate(LocalDateTime createdAt) {
        if (createdAt == null) {
            return "Posted on unknown date";
        }

        LocalDateTime now = LocalDateTime.now();
        long daysAgo = ChronoUnit.DAYS.between(createdAt.toLocalDate(), now.toLocalDate());

        if (daysAgo == 0) {
            return "Posted today";
        } else if (daysAgo == 1) {
            return "Posted yesterday";
        } else if (daysAgo < 30) {
            return "Posted " + daysAgo + " days ago";
        } else {
            return "Posted on " + createdAt.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        }
    }
}