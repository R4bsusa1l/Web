package ch.zhaw.it.pm3.occupi.util;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Enum representing weekdays with short and full German labels.
 * Provides utility methods for weekday conversions and validations.
 */
public enum Weekdays {
    /** Monday - index 0. */
    MONDAY("Mo", "Montag", DayOfWeek.MONDAY),
    /** Tuesday - index 1. */
    TUESDAY("Di", "Dienstag", DayOfWeek.TUESDAY),
    /** Wednesday - index 2. */
    WEDNESDAY("Mi", "Mittwoch", DayOfWeek.WEDNESDAY),
    /** Thursday - index 3. */
    THURSDAY("Do", "Donnerstag", DayOfWeek.THURSDAY),
    /** Friday - index 4. */
    FRIDAY("Fr", "Freitag", DayOfWeek.FRIDAY),
    /** Saturday - index 5. */
    SATURDAY("Sa", "Samstag", DayOfWeek.SATURDAY),
    /** Sunday - index 6. */
    SUNDAY("So", "Sonntag", DayOfWeek.SUNDAY);

    private final String shortLabel;
    private final String fullLabel;
    private final DayOfWeek dayOfWeek;

    Weekdays(String shortLabel, String fullLabel, DayOfWeek dayOfWeek) {
        this.shortLabel = shortLabel;
        this.fullLabel = fullLabel;
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Gets the short German label (e.g., "Mo", "Di").
     *
     * @return the short label
     */
    private String getShortLabel() {
        return shortLabel;
    }

    /**
     * Gets the full German label (e.g., "Montag", "Dienstag").
     *
     * @return the full label
     */
    public String getFullLabel() {
        return fullLabel;
    }

    /**
     * Returns a list of all short labels (Mo, Di, Mi, Do, Fr, Sa, So).
     *
     * @return list of all short labels
     */
    public static List<String> getShortLabels() {
        return Arrays.stream(values())
                .map(Weekdays::getShortLabel)
                .toList();
    }

    /**
     * Converts a DayOfWeek to a Weekday enum.
     *
     * @param dayOfWeek the DayOfWeek to convert
     * @return the corresponding Weekdays enum, or null if dayOfWeek is null
     */
    public static Weekdays fromDayOfWeek(DayOfWeek dayOfWeek) {
        Optional<Weekdays> optional = Arrays.stream(values())
                .filter(w -> w.dayOfWeek == dayOfWeek)
                .findFirst();
        return optional.orElse(null);
    }

    /**
     * Returns the number of supported weekdays (7).
     *
     * @return the count of weekdays (7)
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static int count() {
        return values().length;
    }
}