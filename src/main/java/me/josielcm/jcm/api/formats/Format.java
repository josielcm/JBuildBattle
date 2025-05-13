package me.josielcm.jcm.api.formats;

import java.util.regex.Pattern;

public class Format {

    private static final Pattern SPACING_CHARS_REGEX = Pattern.compile("[_ \\-]+");

    public static String formatTime(int time) {
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;

        StringBuilder formatted = new StringBuilder();

        if (hours > 0) {
            formatted.append(hours).append(":");
            formatted.append(String.format("%02d:", minutes));
        } else if (minutes > 0) {
            formatted.append(minutes).append(":");
        }

        if (seconds < 10 && (minutes > 0 || hours > 0)) {
            formatted.append("0");
        }
        formatted.append(seconds);

        return formatted.toString();
    }

    public static String removeSpacingChars(String string) {
        return SPACING_CHARS_REGEX.matcher(string).replaceAll("");
    }

}
