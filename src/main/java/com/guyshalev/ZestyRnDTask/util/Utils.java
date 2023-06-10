package com.guyshalev.ZestyRnDTask.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

public class Utils {
    private static final String PATTERN_FORMAT = "dd MM yyyy HH:mm:ss";

    public static List<String> splitStringByDelimiter(String stringToSplit, String delimiter) {
        return List.of(stringToSplit.split(delimiter));
    }

    public static String formatTime(TemporalAccessor timeToFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
                .withZone(ZoneId.systemDefault());
        return formatter.format(timeToFormat);
    }
}
